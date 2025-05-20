package cidade;

import estruturas.Lista;

public class GrafoListaAdjacencia extends Grafo {
    private Lista<Intersecao> vertices;
    private Lista<Rua> arestas;
    private Lista<Lista<Rua>> listaAdjacencia;

    public GrafoListaAdjacencia() {
        vertices = new Lista<>();
        arestas = new Lista<>();
        listaAdjacencia = new Lista<>();
    }

    @Override
    public void adicionarVertice(Intersecao intersecao) {
        if (!vertices.contem(intersecao)) {
            vertices.adicionar(intersecao);
            listaAdjacencia.adicionar(new Lista<>());
        }
    }

    @Override
    public void adicionarAresta(Rua rua) {
        int indiceOrigem = vertices.indiceDe(rua.getOrigem());
        if (indiceOrigem == -1) {
            adicionarVertice(rua.getOrigem());
            indiceOrigem = vertices.tamanho() - 1;
        }
        
        int indiceDestino = vertices.indiceDe(rua.getDestino());
        if (indiceDestino == -1) {
            adicionarVertice(rua.getDestino());
            indiceDestino = vertices.tamanho() - 1;
        }
        
        listaAdjacencia.obter(indiceOrigem).adicionar(rua);
        arestas.adicionar(rua);
    }

    @Override
    public Lista<Intersecao> getVertices() {
        return vertices;
    }

    @Override
    public Lista<Rua> getArestas() {
        return arestas;
    }

    @Override
    public Lista<Rua> getArestasAdjacentes(Intersecao intersecao) {
        int indice = vertices.indiceDe(intersecao);
        if (indice == -1) return new Lista<>();
        return listaAdjacencia.obter(indice);
    }

    @Override
    public Rua getAresta(Intersecao origem, Intersecao destino) {
        Lista<Rua> adjacentes = getArestasAdjacentes(origem);
        for (int i = 0; i < adjacentes.tamanho(); i++) {
            Rua rua = adjacentes.obter(i);
            if (rua.getDestino().equals(destino)) {
                return rua;
            }
        }
        return null;
    }

    @Override
    public int getNumeroVertices() {
        return vertices.tamanho();
    }

    @Override
    public int getNumeroArestas() {
        return arestas.tamanho();
    }
}