// Imports
import cidade.Grafo;
import cidade.Intersecao;
import cidade.Mapa;
import cidade.Rua;
import estruturas.Lista;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import semaforo.*;
import trafego.*;

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

    private static final int LIMITE_VEICULOS = 10000;
    private static final int INICIAL_VEICULOS = 5000;
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
            definirRotaSimplificada(veiculo);
            veiculo.getOrigem().adicionarVeiculo(veiculo);
            System.out.printf("[GERADO] Veículo %s - Origem: %s | Destino: %s\n", veiculo.getId(), veiculo.getOrigem().getNome(), veiculo.getDestino().getNome());
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
        // Geração de novos veículos
        if (tempoSimulacao % intervaloGeracao == 0 && veiculos.tamanho() < LIMITE_VEICULOS) {
            int faltantes = LIMITE_VEICULOS - veiculos.tamanho();
            int gerarAgora = Math.min(INCREMENTO_VEICULOS, faltantes);

            Lista<Veiculo> novos = geradorVeiculos.gerarVeiculos(gerarAgora);
            for (int i = 0; i < novos.tamanho(); i++) {
                Veiculo veiculo = novos.obter(i);
                veiculos.adicionar(veiculo);
                estatisticas.registrarVeiculoCriado();
                definirRotaSimplificada(veiculo);
                veiculo.getOrigem().adicionarVeiculo(veiculo);
                System.out.printf("[GERADO] Veículo %s - Origem: %s | Destino: %s\n",
                        veiculo.getId(), veiculo.getOrigem(), veiculo.getDestino());
            }
        }

        controladorSemaforos.atualizarSemaforos();

        Lista<Veiculo> veiculosRemover = new Lista<>();
        for (int i = 0; i < veiculos.tamanho(); i++) {
            Veiculo veiculo = veiculos.obter(i);
            if (veiculo.isChegou()) {
                estatisticas.registrarVeiculoChegou(veiculo);
                System.out.printf("[FINALIZADO] Veículo %s - Tempo viagem: %d min | Espera: %d min\n",
                        veiculo.getId(), veiculo.getTempoViagem(), veiculo.getTempoEspera());
                veiculosRemover.adicionar(veiculo);
            } else {
                if (veiculo.getRuaAtual() == null && !veiculo.getRota().vazia()) {
                    Rua proximaRua = veiculo.getRota().obter(0);
                    Intersecao intersecaoAtual = veiculo.getIntersecaoAtual();

                    if (intersecaoAtual.getSemaforo() != null && !intersecaoAtual.getSemaforo().estaVerde()) {
                        veiculo.incrementarTempoEspera();
                    } else {
                        if (proximaRua.adicionarVeiculo()) {
                            veiculo.avancarNaRota();
                            intersecaoAtual.removerVeiculo();
                            veiculo.setRuaAtual(proximaRua);
                        }
                    }
                }
                veiculo.atualizar();
            }
        }

        for (int i = 0; i < veiculosRemover.tamanho(); i++) {
            veiculos.remover(veiculosRemover.obter(i));
        }

        estatisticas.atualizarRuasCongestionadas(mapa.getGrafo().getArestas());

        // Print de semáforos congestionados
        Lista<Semaforo> semaforos = ordenarSemaforosPorFila(controladorSemaforos.getSemaforos());
        System.out.println("\n[SEMAFOROS] Top congestionados:");
        for (int i = 0; i < Math.min(3, semaforos.tamanho()); i++) {
            Semaforo s = semaforos.obter(i);
            System.out.printf("- %s (Fila: %d veículos)\n",
                    s.getIntersecao(), s.getIntersecao().getFilaVeiculos().tamanho());
        }

        // Logs temporizados
        if (tempoSimulacao % 10 == 0) {
            System.out.println("\nMinuto " + tempoSimulacao);
            System.out.println("Veículos em trânsito: " + veiculos.tamanho());
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

    private void definirRotaSimplificada(Veiculo veiculo) {
        Lista<Rua> rota = new Lista<>();
        Lista<Rua> arestas = mapa.getGrafo().getArestasAdjacentes(veiculo.getOrigem());
        for (int i = 0; i < 3 && i < arestas.tamanho(); i++) {
            rota.adicionar(arestas.obter(i));
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

    public void exibirEstadoVeiculo(Veiculo veiculo) {
        System.out.printf("Veículo %d na %s | espera: %d | semáforo: %s\n",
                veiculo.getId(),
                veiculo.getIntersecaoAtual().getNome(),
                veiculo.getTempoEspera(),
                veiculo.getIntersecaoAtual().getSemaforo() == null ? "SEM SEMÁFORO" :
                        (veiculo.getIntersecaoAtual().getSemaforo().estaVerde() ? "VERDE" : "VERMELHO"));
    }
}
