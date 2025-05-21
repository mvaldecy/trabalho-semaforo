package estruturas;


public class HashSetX<T> {
    private Lista<Lista<T>> tabela;
    private int capacidade = 100;

    public HashSetX() {
        tabela = new Lista<>();
        for (int i = 0; i < capacidade; i++) {
            tabela.adicionar(new Lista<>());
        }
    }

    private int hash(T elemento) {
        return Math.abs(elemento.hashCode() % capacidade);
    }

    public void adicionar(T elemento) {
        int indice = hash(elemento);
        Lista<T> balde = tabela.obter(indice);

        for (int i = 0; i < balde.tamanho(); i++) {
            if (balde.obter(i).equals(elemento)) {
                return; // Já existe
            }
        }

        balde.adicionar(elemento);
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
        int total = 0;
        for (int i = 0; i < capacidade; i++) {
            total += tabela.obter(i).tamanho();
        }
        return total;
    }
}
