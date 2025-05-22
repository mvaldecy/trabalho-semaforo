package estruturas;

public class HashSetX<T> {

    private Lista<Lista<T>> tabela;
    private int capacidade;
    private int tamanho;
    private final double fatorCargaMaximo = 0.75;

    public HashSetX() {
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

    private int hash(T elemento) {
        return Math.abs(elemento.hashCode() % capacidade);
    }

    public void adicionar(T elemento) {
        if (contem(elemento)) return;

        if ((double) tamanho / capacidade >= fatorCargaMaximo) {
            redimensionar(capacidade * 2);
        }

        int indice = hash(elemento);
        tabela.obter(indice).adicionar(elemento);
        tamanho++;
    }

    public boolean contem(T elemento) {
        int indice = hash(elemento);
        Lista<T> balde = tabela.obter(indice);

        for (int i = 0; i < balde.tamanho(); i++) {
            if (balde.obter(i).equals(elemento)) {
                return true;
            }
        }

        return false;
    }

    public int tamanho() {
        return tamanho;
    }

    private void redimensionar(int novaCapacidade) {
        Lista<T> todos = new Lista<>();
        for (int i = 0; i < capacidade; i++) {
            Lista<T> balde = tabela.obter(i);
            for (int j = 0; j < balde.tamanho(); j++) {
                todos.adicionar(balde.obter(j));
            }
        }

        this.capacidade = novaCapacidade;
        this.tamanho = 0;
        inicializarTabela(novaCapacidade);

        for (int i = 0; i < todos.tamanho(); i++) {
            adicionar(todos.obter(i));
        }
    }
}
