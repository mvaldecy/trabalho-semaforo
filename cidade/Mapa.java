package cidade;

import java.io.FileReader;
import estruturas.Lista;
import estruturas.HashMapX;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Mapa {
    private Grafo grafo;
    private Lista<Intersecao> intersecoes;
    private HashMapX<String, Intersecao> mapaIntersecoes;

    public Mapa() {
        this.grafo = new GrafoListaAdjacencia();
        this.intersecoes = new Lista<>();
        this.mapaIntersecoes = new HashMapX<>();
    }

    public void carregarMapa(String caminhoArquivo) {
        try {
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(new FileReader(caminhoArquivo));

            JSONArray nodes = (JSONArray) json.get("nodes");
            for (Object obj : nodes) {
                JSONObject intersecaoJson = (JSONObject) obj;
                String nome = (String) intersecaoJson.get("id");
                double latitude = (double) intersecaoJson.get("latitude");
                double longitude = (double) intersecaoJson.get("longitude");

                Intersecao intersecao = new Intersecao(nome, latitude, longitude);
                intersecoes.adicionar(intersecao);
                mapaIntersecoes.colocar(nome, intersecao); // otimização
                grafo.adicionarVertice(intersecao);
            }

            JSONArray edges = (JSONArray) json.get("edges");
            for (Object obj : edges) {
                JSONObject ruaJson = (JSONObject) obj;

                String id = (String) ruaJson.get("id");
                String origemId = (String) ruaJson.get("source");
                String destinoId = (String) ruaJson.get("target");
                double comprimento = (double) ruaJson.get("length");
                double velocidadeMax = (double) ruaJson.get("maxspeed");
                int tempo = (int) Math.ceil(comprimento / (velocidadeMax / 3.6));
                int capacidade = 40;
                boolean isOneWay = (Boolean) ruaJson.get("oneway");
                boolean maoDupla = !isOneWay;

                Intersecao origem = buscarIntersecaoPorNome(origemId);
                Intersecao destino = buscarIntersecaoPorNome(destinoId);

                if (origem == null || destino == null) {
                    System.err.println("Intersecao não encontrada para rua " + id);
                    continue;
                }

                Rua rua = new Rua(id, origem, destino, tempo, capacidade, maoDupla, comprimento, velocidadeMax);
                grafo.adicionarAresta(rua);

                if (maoDupla) {
                    Rua ruaInvertida = new Rua(id + "_INV", destino, origem, tempo, capacidade, true, comprimento, velocidadeMax);
                    grafo.adicionarAresta(ruaInvertida);
                }
            }

            System.out.println("Mapa carregado com " + grafo.getNumeroVertices() + " interseções e " +
                               grafo.getNumeroArestas() + " ruas.");
        } catch (Exception e) {
            System.err.println("Erro ao carregar mapa: " + e.getMessage());
        }
    }

    private Intersecao buscarIntersecaoPorNome(String nome) {
        return mapaIntersecoes.obter(nome); // busca otimizada
    }

    public Grafo getGrafo() {
        return grafo;
    }
}
