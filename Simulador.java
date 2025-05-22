

import cidade.Grafo;
import cidade.Intersecao;
import cidade.Mapa;
import cidade.Rua;
import estruturas.*;
import semaforo.*;
import trafego.Estatisticas;
import trafego.GeradorVeiculos;
import trafego.Veiculo;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


public class Simulador {

    private Mapa mapa;
    private ControladorSemaforos controladorSemaforos;
    private GeradorVeiculos geradorVeiculos;
    private Lista<Veiculo> veiculos;
    private Estatisticas estatisticas;
    private Timer timer;
    private int tempoSimulacao;
    private int intervaloGeracao;
    private Random random;
    private boolean pausado;

    private static final int LIMITE_VEICULOS = 1000;
    private static final int INICIAL_VEICULOS = 1000;
    private static final int INCREMENTO_VEICULOS = 5;

    public Simulador() {
        this.mapa = new Mapa();
        this.controladorSemaforos = new ControladorSemaforos();
        this.veiculos = new Lista<>();
        this.estatisticas = new Estatisticas();
        this.random = new Random();
        this.pausado = false;
    }

    public void iniciar(String arquivoMapa, int duracaoMinutos, ModoOperacao modoOperacao) {
        mapa.carregarMapa(arquivoMapa);
        inicializarSemaforos(mapa.getGrafo(), modoOperacao);
        this.geradorVeiculos = new GeradorVeiculos(mapa.getGrafo());
        this.tempoSimulacao = 0;
        this.intervaloGeracao = 1;

        Lista<Veiculo> iniciais = geradorVeiculos.gerarVeiculos(INICIAL_VEICULOS);
        for (int i = 0; i < iniciais.tamanho(); i++) {
            Veiculo veiculo = iniciais.obter(i);
            veiculos.adicionar(veiculo);
            estatisticas.registrarVeiculoCriado();
            definirRotaDijkstra(veiculo);
            veiculo.getOrigem().adicionarVeiculo(veiculo);
            System.out.printf("[GERADO] Veículo %s - Origem: %s | Destino: %s\n",
                    veiculo.getId(), veiculo.getOrigem().getNome(), veiculo.getDestino().getNome());
        }

        System.out.println("Simulação iniciada - Modo: " + modoOperacao);
        System.out.println("Duração: " + duracaoMinutos + " minutos simulados");

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (!pausado) {
                    passoSimulacao();
                    tempoSimulacao++;

                    if (tempoSimulacao >= duracaoMinutos) {
                        encerrar();
                    }
                }
            }
        }, 0, 1000);
    }

    private void inicializarSemaforos(Grafo grafo, ModoOperacao modo) {
        Lista<Intersecao> intersecoes = grafo.getVertices();
        for (int i = 0; i < intersecoes.tamanho(); i++) {
            if (random.nextDouble() < 0.7) {
                Intersecao intersecao = intersecoes.obter(i);
                Semaforo semaforo = new Semaforo(intersecao, 10, 2, 10, modo);
                controladorSemaforos.adicionarSemaforo(semaforo);
                intersecao.setSemaforo(semaforo);
            }
        }
        controladorSemaforos.configurarModoOperacao(modo);
        System.out.println("Semáforos configurados: " + controladorSemaforos.getSemaforos().tamanho());
    }

    private void passoSimulacao() {
        // Geração incremental de veículos
        if (tempoSimulacao % intervaloGeracao == 0 && veiculos.tamanho() < LIMITE_VEICULOS) {
            int faltantes = LIMITE_VEICULOS - veiculos.tamanho();
            int gerarAgora = Math.min(INCREMENTO_VEICULOS, faltantes);

            Lista<Veiculo> novos = geradorVeiculos.gerarVeiculos(gerarAgora);
            for (int i = 0; i < novos.tamanho(); i++) {
                Veiculo veiculo = novos.obter(i);
                veiculos.adicionar(veiculo);
                estatisticas.registrarVeiculoCriado();
                definirRotaDijkstra(veiculo);
                veiculo.getOrigem().adicionarVeiculo(veiculo);
                System.out.printf("[GERADO] Veículo %s - Origem: %s | Destino: %s\n",
                        veiculo.getId(), veiculo.getOrigem().getNome(), veiculo.getDestino().getNome());
            }
        }

        // Atualiza veículos
        Lista<Veiculo> veiculosRemover = new Lista<>();
        for (int i = 0; i < veiculos.tamanho(); i++) {
            Veiculo veiculo = veiculos.obter(i);
            if (veiculo.isChegou()) {
                estatisticas.registrarVeiculoChegou(veiculo);
                veiculosRemover.adicionar(veiculo);
            } else {
                veiculo.atualizar();
            }
        }

        for (int i = 0; i < veiculosRemover.tamanho(); i++) {
            veiculos.remover(veiculosRemover.obter(i));
        }

        // Atualiza semáforos depois que as filas mudaram
        controladorSemaforos.atualizarSemaforos();

        // Estatísticas
        estatisticas.setTempoSimulacao(tempoSimulacao);
        estatisticas.atualizarRuasCongestionadas(mapa.getGrafo().getArestas());

        // Log dos semáforos mais congestionados
        if (tempoSimulacao % 10 == 0) {
            Lista<Semaforo> semaforos = ordenarSemaforosPorFila(controladorSemaforos.getSemaforos());
            System.out.println("\n[SEMAFOROS] Top congestionados:");
            for (int i = 0; i < Math.min(3, semaforos.tamanho()); i++) {
                Semaforo s = semaforos.obter(i);
                String status = s.estaVerde() ? "VERDE" : (s.estaAmarelo() ? "AMARELO" : "VERMELHO");
                System.out.printf("- %s (Fila: %d veículos) | Status: %s | Tempo restante: %ds\n",
                        s.getIntersecao().getNome(),
                        s.getIntersecao().getFilaVeiculos().tamanho(),
                        status,
                        s.getTempoRestante());
            }
        }
    }

    private Lista<Semaforo> ordenarSemaforosPorFila(Lista<Semaforo> semaforos) {
        Lista<Semaforo> ordenados = new Lista<>();
        for (int i = 0; i < semaforos.tamanho(); i++) {
            Semaforo atual = semaforos.obter(i);
            int j = 0;
            while (j < ordenados.tamanho() &&
                    atual.getIntersecao().getFilaVeiculos().tamanho() <
                    ordenados.obter(j).getIntersecao().getFilaVeiculos().tamanho()) {
                j++;
            }
            ordenados.inserir(j, atual);
        }
        return ordenados;
    }

    private void definirRotaDijkstra(Veiculo veiculo) {
        Fila<Intersecao> caminho = Dijkstra.encontrarMenorCaminho(mapa.getGrafo(), veiculo.getOrigem(), veiculo.getDestino());
        Lista<Rua> rota = new Lista<>();

        Intersecao anterior = caminho.desenfileirar();
        while (!caminho.vazia()) {
            Intersecao atual = caminho.desenfileirar();
            Rua rua = mapa.getGrafo().getAresta(anterior, atual);
            if (rua != null) {
                rota.adicionar(rua);
            }
            anterior = atual;
        }

        veiculo.setRota(rota);
    }

    public void pausar() {
        pausado = true;
    }

    public void continuar() {
        pausado = false;
    }

    public void encerrar() {
        if (timer != null) {
            timer.cancel();
        }
        estatisticas.imprimirEstatisticas();
        System.out.println("\nSimulação encerrada após " + tempoSimulacao + " minutos.");
    }
}