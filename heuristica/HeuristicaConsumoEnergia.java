package heuristica;

import semaforo.Semaforo;
import cidade.Intersecao;
import estruturas.Lista;

public class HeuristicaConsumoEnergia implements HeuristicaControle {
    @Override
    public void aplicar(Lista<Semaforo> semaforos) {
        // Prioriza semáforos com menos veículos parados para reduzir consumo de energia
        for (int i = 0; i < semaforos.tamanho(); i++) {
            Semaforo semaforo = semaforos.obter(i);
            Intersecao intersecao = semaforo.getIntersecao();
            
            int tamanhoFila = intersecao.getFilaVeiculos().tamanho();
            
            // Reduz o tempo de vermelho se houver poucos veículos esperando
            int novoVerde = 30; // Tempo mínimo de verde
            int novoAmarelo = 5;
            int novoVermelho = Math.max(30, 60 - tamanhoFila * 2);
            
            semaforo.ajustarTempos(novoVerde, novoAmarelo, novoVermelho);
        }
    }
}