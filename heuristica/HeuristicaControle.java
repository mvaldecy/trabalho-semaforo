package heuristica;

import estruturas.Lista;
import semaforo.Semaforo;

public interface HeuristicaControle {
    void aplicar(Lista<Semaforo> semaforos);
}