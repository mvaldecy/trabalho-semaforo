import cidade.Grafo;
import cidade.Intersecao;
import cidade.Rua;
import estruturas.*;

public class Dijkstra {

    public static Fila<Intersecao> encontrarMenorCaminho(Grafo grafo, Intersecao origem, Intersecao destino) {
        HashMapX<Intersecao, Integer> distancias = new HashMapX<>();
        HashMapX<Intersecao, Intersecao> anteriores = new HashMapX<>();
        HashSetX<Intersecao> visitados = new HashSetX<>();

        Lista<Intersecao> todosVertices = grafo.getVertices();
        for (int i = 0; i < todosVertices.tamanho(); i++) {
            Intersecao v = todosVertices.obter(i);
            distancias.colocar(v, Integer.MAX_VALUE);
            anteriores.colocar(v, null);
        }
        distancias.colocar(origem, 0);

        while (visitados.tamanho() < todosVertices.tamanho()) {
            Intersecao atual = encontrarMenorVertice(distancias, visitados, todosVertices);
            if (atual == null) break;
            visitados.adicionar(atual);

            Lista<Rua> adjacentes = grafo.getArestasAdjacentes(atual);
            for (int i = 0; i < adjacentes.tamanho(); i++) {
                Rua rua = adjacentes.obter(i);
                Intersecao vizinho = rua.getDestino();
                if (!visitados.contem(vizinho)) {
                    int novaDist = distancias.obter(atual) + rua.getTempoTravessia();
                    if (novaDist < distancias.obter(vizinho)) {
                        distancias.colocar(vizinho, novaDist);
                        anteriores.colocar(vizinho, atual);
                    }
                }
            }
        }

        return construirCaminho(anteriores, origem, destino);
    }

    private static Intersecao encontrarMenorVertice(HashMapX<Intersecao, Integer> distancias,
                                                    HashSetX<Intersecao> visitados,
                                                    Lista<Intersecao> vertices) {
        Intersecao menor = null;
        int menorDistancia = Integer.MAX_VALUE;
        for (int i = 0; i < vertices.tamanho(); i++) {
            Intersecao v = vertices.obter(i);
            if (!visitados.contem(v)) {
                int dist = distancias.obter(v);
                if (dist < menorDistancia) {
                    menorDistancia = dist;
                    menor = v;
                }
            }
        }
        return menor;
    }

    private static Fila<Intersecao> construirCaminho(HashMapX<Intersecao, Intersecao> anteriores,
                                                     Intersecao origem, Intersecao destino) {
        Pilha<Intersecao> pilha = new Pilha<>();
        Intersecao atual = destino;

        while (atual != null) {
            pilha.empilhar(atual);
            atual = anteriores.obter(atual);
        }

        Fila<Intersecao> caminho = new Fila<>();
        while (!pilha.vazia()) {
            caminho.enfileirar(pilha.desempilhar());
        }

        return caminho;
    }
}
