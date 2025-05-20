package semaforo;

import estruturas.Lista;
import heuristica.HeuristicaCicloFixo;
import heuristica.HeuristicaConsumoEnergia;
import heuristica.HeuristicaControle;
import heuristica.HeuristicaTempoEspera;

public class ControladorSemaforos {
    private final Lista<Semaforo> semaforos;
    private HeuristicaControle heuristica;
    private int tempoSimulacao;  // Variável adicionada para resolver o erro

    public ControladorSemaforos() {
        this.semaforos = new Lista<>();
        this.tempoSimulacao = 0;  // Inicializa a variável
    }

    public void adicionarSemaforo(Semaforo semaforo) {
        semaforos.adicionar(semaforo);
    }

    public void configurarModoOperacao(ModoOperacao modo) {
        for (int i = 0; i < semaforos.tamanho(); i++) {
            Semaforo semaforo = semaforos.obter(i);
            semaforo.setModoOperacao(modo);
        }
        
        switch (modo) {
            case CICLO_FIXO -> heuristica = new HeuristicaCicloFixo();
            case TEMPO_ESPERA -> heuristica = new HeuristicaTempoEspera();
            case CONSUMO_ENERGIA -> heuristica = new HeuristicaConsumoEnergia();
        }
    }

    public void atualizarSemaforos() {
        // Alternar semáforos periodicamente
        if (tempoSimulacao % 10 == 0) {  // A cada 10 minutos
            for (int i = 0; i < semaforos.tamanho(); i++) {
                Semaforo semaforo = semaforos.obter(i);
                // Alternar entre verde e vermelho
                if (semaforo.estaVerde()) {
                    semaforo.mudarEstado();
                } else {
                    semaforo.mudarEstado();
                }
            }
        }
        
        // Aplicar heurísticas
        if (heuristica != null) {
            heuristica.aplicar(semaforos);
        }
        
        tempoSimulacao++;  // Incrementa o tempo de simulação
    }

    public Lista<Semaforo> getSemaforos() {
        return semaforos;
    }
}