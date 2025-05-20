package trafego;

import cidade.Grafo;
import cidade.Intersecao;
import estruturas.Lista;
import java.util.Random;

public class GeradorVeiculos {
    private Grafo grafo;
    private Random random;
    private int contadorVeiculos;

    public GeradorVeiculos(Grafo grafo) {
        this.grafo = grafo;
        this.random = new Random();
        this.contadorVeiculos = 0;
    }

    public Veiculo gerarVeiculo() {
        Lista<Intersecao> intersecoes = grafo.getVertices();
        if (intersecoes.vazia()) return null;

        int origemIndex = random.nextInt(intersecoes.tamanho());
        int destinoIndex;
        do {
            destinoIndex = random.nextInt(intersecoes.tamanho());
        } while (destinoIndex == origemIndex);

        Intersecao origem = intersecoes.obter(origemIndex);
        Intersecao destino = intersecoes.obter(destinoIndex);

        contadorVeiculos++;
        return new Veiculo("V" + contadorVeiculos, origem, destino);
    }

    public Lista<Veiculo> gerarVeiculos(int quantidade) {
        Lista<Veiculo> veiculos = new Lista<>();
        for (int i = 0; i < quantidade; i++) {
            Veiculo veiculo = gerarVeiculo();
            if (veiculo != null) {
                veiculos.adicionar(veiculo);
            }
        }
        return veiculos;
    }
}
