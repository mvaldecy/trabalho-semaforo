package heuristica;

import cidade.Intersecao;
import estruturas.Lista;
import semaforo.Semaforo;

public class HeuristicaConsumoEnergia implements HeuristicaControle {
    @Override
    public void aplicar(Lista<Semaforo> semaforos) {
        for (int i = 0; i < semaforos.tamanho(); i++) {
            Semaforo semaforo = semaforos.obter(i);
            Intersecao intersecao = semaforo.getIntersecao();
            int fila = intersecao.getFilaVeiculos().tamanho();

            int verde = 30;
            int amarelo = 5;
            int vermelho = Math.max(30, 60 - fila * 2); // mínimo 30s

            semaforo.ajustarTempos(verde, amarelo, vermelho);

            System.out.printf("[HEURÍSTICA - CONSUMO_ENERGIA] Semáforo %s | Fila: %d | Vermelho: %ds\n",
                    intersecao.getNome(), fila, vermelho);
        }
    }
}