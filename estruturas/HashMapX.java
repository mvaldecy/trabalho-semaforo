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
    private int capacidade;
    private int tamanho;
    private final double fatorCargaMaximo = 0.75;

    public HashMapX() {
        this.capacidade = 100;
        this.tamanho = 0;
        inicializarTabela(capacidade);
    }

    private void inicializarTabela(int capacidade) {
        tabela = new Lista<>();
        for (int i = 0; i < capacidade; i++) {
            tabela.adicionar(new Lista<>());
        }
    }

    private int hash(K chave) {
        return Math.abs(chave.hashCode() % capacidade);
    }

    public void colocar(K chave, V valor) {
        if ((double) tamanho / capacidade >= fatorCargaMaximo) {
            redimensionar(capacidade * 2);
        }

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
        tamanho++;
    }

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

    public boolean contemChave(K chave) {
        return obter(chave) != null;
    }

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

    public int tamanho() {
        return tamanho;
    }

    private void redimensionar(int novaCapacidade) {
        Lista<Entrada<K, V>> todasEntradas = new Lista<>();
        for (int i = 0; i < capacidade; i++) {
            Lista<Entrada<K, V>> balde = tabela.obter(i);
            for (int j = 0; j < balde.tamanho(); j++) {
                todasEntradas.adicionar(balde.obter(j));
            }
        }

        this.capacidade = novaCapacidade;
        this.tamanho = 0;
        inicializarTabela(novaCapacidade);

        for (int i = 0; i < todasEntradas.tamanho(); i++) {
            Entrada<K, V> entrada = todasEntradas.obter(i);
            colocar(entrada.chave, entrada.valor);
        }
    }
}
