package heuristica;

import semaforo.Semaforo;
import estruturas.Lista;

public class HeuristicaCicloFixo implements HeuristicaControle {
    @Override
    public void aplicar(Lista<Semaforo> semaforos) {
        // No ciclo fixo, não fazemos ajustes dinâmicos
    }
}