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
            int tamanhoFila = intersecao.getFilaVeiculos().tamanho();
            
            // Ajusta o tempo de verde proporcional ao tamanho da fila
            int novoVerde = Math.min(60, 15 + tamanhoFila * 5);  // Mínimo 15s, máximo 60s
            semaforo.ajustarTempos(novoVerde, 5, 30);  // verde, amarelo, vermelho
            
            System.out.println("Semaforo " + intersecao.getNome() + 
                             " - Fila: " + tamanhoFila + 
                             " - Verde: " + novoVerde + "s");
        }
    }
}