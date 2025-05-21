package estruturas;

public class HashMapX<K, V> {

    private static class Entrada<K, V> {
        K chave;
        V valor;

        Entrada(K chave, V valor) {
            this.chave = chave;
            this.valor = valor;
        }
    }

    private Lista<Lista<Entrada<K, V>>> tabela;
    private int capacidade = 100;

    public HashMapX() {
        tabela = new Lista<>();
        for (int i = 0; i < capacidade; i++) {
            tabela.adicionar(new Lista<>());
        }
    }

    private int hash(K chave) {
        return Math.abs(chave.hashCode() % capacidade);
    }

    // Método esperado pela Dijkstra: colocar
    public void colocar(K chave, V valor) {
        int indice = hash(chave);
        Lista<Entrada<K, V>> balde = tabela.obter(indice);

        for (int i = 0; i < balde.tamanho(); i++) {
            Entrada<K, V> entrada = balde.obter(i);
            if (entrada.chave.equals(chave)) {
                entrada.valor = valor;
                return;
            }
        }

        balde.adicionar(new Entrada<>(chave, valor));
    }

    // Método esperado pela Dijkstra: obter
    public V obter(K chave) {
        int indice = hash(chave);
        Lista<Entrada<K, V>> balde = tabela.obter(indice);

        for (int i = 0; i < balde.tamanho(); i++) {
            Entrada<K, V> entrada = balde.obter(i);
            if (entrada.chave.equals(chave)) {
                return entrada.valor;
            }
        }

        return null;
    }

    // Método esperado pela Dijkstra: contemChave
    public boolean contemChave(K chave) {
        return obter(chave) != null;
    }

    // Método esperado pela Dijkstra: chaves
    public Lista<K> chaves() {
        Lista<K> chaves = new Lista<>();
        for (int i = 0; i < capacidade; i++) {
            Lista<Entrada<K, V>> balde = tabela.obter(i);
            for (int j = 0; j < balde.tamanho(); j++) {
                chaves.adicionar(balde.obter(j).chave);
            }
        }
        return chaves;
    }
}
