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

    private static final int LIMITE_VEICULOS = 3500;
    private static final int INICIAL_VEICULOS = 2000;
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

        // Gera os 1000 veículos iniciais
        Lista<Veiculo> iniciais = geradorVeiculos.gerarVeiculos(INICIAL_VEICULOS);
        for (int i = 0; i < iniciais.tamanho(); i++) {
            Veiculo veiculo = iniciais.obter(i);
            veiculos.adicionar(veiculo);
            estatisticas.registrarVeiculoCriado();
            definirRotaSimplificada(veiculo);
            veiculo.getOrigem().adicionarVeiculo(veiculo);
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
            if (random.nextDouble() < 0.3) {
                Intersecao intersecao = intersecoes.obter(i);
                int verde, amarelo, vermelho;

                switch (modo) {
                    case CICLO_FIXO -> {
                        verde = 5;
                        amarelo = 2;
                        vermelho = 5;
                    }
                    case TEMPO_ESPERA -> {
                        verde = 5;
                        amarelo = 2;
                        vermelho = 10;
                    }
                    case CONSUMO_ENERGIA -> {
                        verde = 10;
                        amarelo = 2;
                        vermelho = 10;
                    }
                    default -> {
                        verde = 10;
                        amarelo = 2;
                        vermelho = 10;
                    }
                }

                Semaforo semaforo = new Semaforo(intersecao, verde, amarelo, vermelho, modo);
                controladorSemaforos.adicionarSemaforo(semaforo);
                intersecao.setSemaforo(semaforo);
            }
        }

        controladorSemaforos.configurarModoOperacao(modo);
        System.out.println("Semáforos configurados: " + controladorSemaforos.getSemaforos().tamanho());
    }

    private void passoSimulacao() {
        // Gera novos veículos até atingir o limite
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
            }
        }

        controladorSemaforos.atualizarSemaforos();

        Lista<Veiculo> veiculosRemover = new Lista<>();
        for (int i = 0; i < veiculos.tamanho(); i++) {
            Veiculo veiculo = veiculos.obter(i);

            if (veiculo.isChegou()) {
                estatisticas.registrarVeiculoChegou(veiculo);
                veiculosRemover.adicionar(veiculo);
            } else {
                if (veiculo.getRuaAtual() == null && !veiculo.getRota().vazia()) {
                    Rua proximaRua = veiculo.getRota().obter(0);
                    Intersecao intersecaoAtual = veiculo.getOrigem();

                    if (intersecaoAtual.getSemaforo() != null && !intersecaoAtual.getSemaforo().estaVerde()) {
                        veiculo.incrementarTempoEspera();
                    } else {
                        if (proximaRua.adicionarVeiculo()) {
                            veiculo.avancarNaRota();
                            veiculo.getOrigem().removerVeiculo();
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

        if (tempoSimulacao % 10 == 0) {
            System.out.println("\nMinuto " + tempoSimulacao);
            System.out.println("Veículos em trânsito: " + veiculos.tamanho());

            Lista<Semaforo> semaforos = controladorSemaforos.getSemaforos();
            for (int i = 0; i < 5; i++) {
                System.out.println(semaforos.obter(i));
            }
        }
    }

    private void definirRotaSimplificada(Veiculo veiculo) {
        Lista<Rua> rota = new Lista<>();
        Grafo grafo = mapa.getGrafo();

        Lista<Rua> arestas = grafo.getArestasAdjacentes(veiculo.getOrigem());
        if (!arestas.vazia()) {
            rota.adicionar(arestas.obter(0));
            for (int i = 1; i < 3 && i < arestas.tamanho(); i++) {
                rota.adicionar(arestas.obter(i));
            }
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
        System.out.println("Veículo " + veiculo.getId() +
                " na " + veiculo.getIntersecaoAtual().getNome() +
                " espera: " + veiculo.getTempoEspera() +
                " semáforo: " + (veiculo.getIntersecaoAtual().getSemaforo() != null ?
                (veiculo.getIntersecaoAtual().getSemaforo().estaVerde() ? "VERDE" : "VERMELHO") : "SEM SEMÁFORO"));
    }
}
