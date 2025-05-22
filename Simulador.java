import cidade.*;
import estruturas.*;
import semaforo.*;
import trafego.*;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class Simulador {

    // Instâncias principais do sistema de simulação
    private final Mapa mapa = new Mapa();
    private final ControladorSemaforos controladorSemaforos = new ControladorSemaforos();
    private final Lista<Veiculo> veiculos = new Lista<>();
    private final Estatisticas estatisticas = new Estatisticas();
    private final Random random = new Random();
    private GeradorVeiculos geradorVeiculos;
    private Timer timer;
    private int tempoSimulacao;
    private int intervaloGeracao = 1;
    private boolean pausado;

    // Constantes de configuração da simulação
    private static final int LIMITE_VEICULOS = 1000;
    private static final int INICIAL_VEICULOS = 1000;
    private static final int INCREMENTO_VEICULOS = 5;

    /**
     * Inicia a simulação carregando o mapa, configurando semáforos,
     * gerando veículos iniciais e disparando o timer de execução.
     */
    public void iniciar(String arquivoMapa, int duracaoMinutos, ModoOperacao modoOperacao) {
        mapa.carregarMapa(arquivoMapa);
        inicializarSemaforos(mapa.getGrafo(), modoOperacao);
        this.geradorVeiculos = new GeradorVeiculos(mapa.getGrafo());
        this.tempoSimulacao = 0;

        gerarVeiculosIniciais();
        logInicioSimulacao(modoOperacao, duracaoMinutos);
        iniciarTimer(duracaoMinutos);
    }

    /**
     * Configura os semáforos para as interseções com probabilidade de 70%.
     */
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

    /**
     * Gera o conjunto inicial de veículos e define as rotas com Dijkstra.
     */
    private void gerarVeiculosIniciais() {
        Lista<Veiculo> iniciais = geradorVeiculos.gerarVeiculos(INICIAL_VEICULOS);
        for (int i = 0; i < iniciais.tamanho(); i++) {
            Veiculo v = iniciais.obter(i);
            veiculos.adicionar(v);
            estatisticas.registrarVeiculoCriado();
            definirRotaDijkstra(v);
            v.getOrigem().adicionarVeiculo(v);
            logVeiculoGerado(v);
        }
    }

    /**
     * Inicia o timer responsável por executar cada ciclo da simulação.
     */
    private void iniciarTimer(int duracaoMinutos) {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (!pausado) {
                    passoSimulacao();
                    tempoSimulacao++;
                    if (tempoSimulacao >= duracaoMinutos) encerrar();
                }
            }
        }, 0, 1000);
    }

    /**
     * Executa um ciclo da simulação: gera novos veículos, atualiza
     * os existentes, aplica controle de semáforos e atualiza métricas.
     */
    private void passoSimulacao() {
        gerarVeiculosIncrementais();
        atualizarVeiculos();
        controladorSemaforos.atualizarSemaforos();
        estatisticas.setTempoSimulacao(tempoSimulacao);
        estatisticas.atualizarRuasCongestionadas(mapa.getGrafo().getArestas());
        if (tempoSimulacao % 10 == 0) logSemaforosCongestionados();
    }

    /**
     * Gera veículos durante a simulação até o limite predefinido.
     */
    private void gerarVeiculosIncrementais() {
        if (tempoSimulacao % intervaloGeracao == 0 && veiculos.tamanho() < LIMITE_VEICULOS) {
            int faltantes = LIMITE_VEICULOS - veiculos.tamanho();
            int gerarAgora = Math.min(INCREMENTO_VEICULOS, faltantes);
            Lista<Veiculo> novos = geradorVeiculos.gerarVeiculos(gerarAgora);

            for (int i = 0; i < novos.tamanho(); i++) {
                Veiculo v = novos.obter(i);
                veiculos.adicionar(v);
                estatisticas.registrarVeiculoCriado();
                definirRotaDijkstra(v);
                v.getOrigem().adicionarVeiculo(v);
                logVeiculoGerado(v);
            }
        }
    }

    /**
     * Atualiza a posição de todos os veículos e remove os que chegaram ao destino.
     */
    private void atualizarVeiculos() {
        Lista<Veiculo> remover = new Lista<>();
        for (int i = 0; i < veiculos.tamanho(); i++) {
            Veiculo v = veiculos.obter(i);
            if (v.isChegou()) {
                estatisticas.registrarVeiculoChegou(v);
                remover.adicionar(v);
            } else {
                v.atualizar();
            }
        }

        for (int i = 0; i < remover.tamanho(); i++) {
            veiculos.remover(remover.obter(i));
        }
    }

    /**
     * Define a rota ótima de um veículo usando o algoritmo de Dijkstra.
     */
    private void definirRotaDijkstra(Veiculo veiculo) {
        Fila<Intersecao> caminho = Dijkstra.encontrarMenorCaminho(mapa.getGrafo(), veiculo.getOrigem(), veiculo.getDestino());
        Lista<Rua> rota = new Lista<>();
        Intersecao anterior = caminho.desenfileirar();

        while (!caminho.vazia()) {
            Intersecao atual = caminho.desenfileirar();
            Rua rua = mapa.getGrafo().getAresta(anterior, atual);
            if (rua != null) rota.adicionar(rua);
            anterior = atual;
        }

        veiculo.setRota(rota);
    }

    /**
     * Exibe os semáforos com maiores filas de veículos.
     */
    private void logSemaforosCongestionados() {
        Lista<Semaforo> semaforos = ordenarSemaforosPorFila(controladorSemaforos.getSemaforos());
        System.out.println("\n[SEMAFOROS] Top congestionados:");
        for (int i = 0; i < Math.min(3, semaforos.tamanho()); i++) {
            Semaforo s = semaforos.obter(i);
            String status = s.estaVerde() ? "VERDE" : s.estaAmarelo() ? "AMARELO" : "VERMELHO";
            System.out.printf("- %s (Fila: %d veículos) | Status: %s | Tempo restante: %ds | Fila: %d\n",
                    s.getIntersecao().getNome(),
                    s.getIntersecao().getFilaVeiculos().tamanho(),
                    status,
                    s.getTempoRestante(),
                    s.getIntersecao().getFilaVeiculos().tamanho());
        }
    }

    /**
     * Ordena os semáforos por tamanho da fila de veículos de forma decrescente.
     */
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

    /**
     * Exibe no console as informações de um veículo recém-gerado.
     */
    private void logVeiculoGerado(Veiculo v) {
        System.out.printf("[GERADO] Veículo %s - Origem: %s | Destino: %s\n",
                v.getId(), v.getOrigem().getNome(), v.getDestino().getNome());
    }

    /**
     * Exibe os dados iniciais da simulação no console.
     */
    private void logInicioSimulacao(ModoOperacao modo, int duracao) {
        System.out.println("Simulação iniciada - Modo: " + modo);
        System.out.println("Duração: " + duracao + " minutos simulados");
    }

    /**
     * Pausa a simulação.
     */
    public void pausar() {
        pausado = true;
    }

    /**
     * Retoma a execução da simulação.
     */
    public void continuar() {
        pausado = false;
    }

    /**
     * Finaliza a simulação e imprime estatísticas acumuladas.
     */
    public void encerrar() {
        if (timer != null) timer.cancel();
        estatisticas.imprimirEstatisticas();
        System.out.println("\nSimulação encerrada após " + tempoSimulacao + " minutos.");
    }
}
