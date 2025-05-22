package heuristica;

import cidade.Intersecao;
import estruturas.Lista;
import semaforo.Semaforo;

public class HeuristicaTempoEspera implements HeuristicaControle {
    @Override
    public void aplicar(Lista<Semaforo> semaforos) {
        for (int i = 0; i < semaforos.tamanho(); i++) {
            Semaforo semaforo = semaforos.obter(i);
            Intersecao intersecao = semaforo.getIntersecao();
            int fila = intersecao.getFilaVeiculos().tamanho();

            int verde = Math.min(60, 15 + fila * 5); // mínimo 15s, máximo 60s
            int amarelo = 5;
            int vermelho = 30;

            semaforo.ajustarTempos(verde, amarelo, vermelho);

            System.out.printf("[HEURÍSTICA - TEMPO_ESPERA] Semáforo %s | Fila: %d | Verde: %ds\n",
                    intersecao.getNome(), fila, verde);
        }
    }
}
