package cidade;

import estruturas.Lista;

public abstract class Grafo {
    public abstract void adicionarAresta(Rua rua);
    public abstract void adicionarVertice(Intersecao intersecao);
    public abstract Lista<Intersecao> getVertices();
    public abstract Lista<Rua> getArestas();
    public abstract Lista<Rua> getArestasAdjacentes(Intersecao intersecao);
    public abstract Rua getAresta(Intersecao origem, Intersecao destino);
    public abstract int getNumeroVertices();
    public abstract int getNumeroArestas();
}