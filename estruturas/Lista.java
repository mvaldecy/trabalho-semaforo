package estruturas;

public class Lista<T> {
    private No<T> inicio;
    private int tamanho;

    public Lista() {
        inicio = null;
        tamanho = 0;
    }

    public void adicionar(T elemento) {
        No<T> novoNo = new No<>(elemento);
        if (inicio == null) {
            inicio = novoNo;
        } else {
            No<T> atual = inicio;
            while (atual.proximo != null) {
                atual = atual.proximo;
            }
            atual.proximo = novoNo;
        }
        tamanho++;
    }

    public void inserir(int indice, T elemento) {
        if (indice < 0 || indice > tamanho) {
            throw new IndexOutOfBoundsException("Índice inválido");
        }

        No<T> novoNo = new No<>(elemento);

        if (indice == 0) {
            novoNo.proximo = inicio;
            inicio = novoNo;
        } else {
            No<T> atual = inicio;
            for (int i = 0; i < indice - 1; i++) {
                atual = atual.proximo;
            }
            novoNo.proximo = atual.proximo;
            atual.proximo = novoNo;
        }

        tamanho++;
    }

    public T obter(int indice) {
        if (indice < 0 || indice >= tamanho) {
            throw new IndexOutOfBoundsException("Índice inválido");
        }
        No<T> atual = inicio;
        for (int i = 0; i < indice; i++) {
            atual = atual.proximo;
        }
        return atual.dado;
    }

    public void remover(T elemento) {
        if (inicio == null) return;

        if (inicio.dado.equals(elemento)) {
            inicio = inicio.proximo;
            tamanho--;
            return;
        }

        No<T> atual = inicio;
        while (atual.proximo != null && !atual.proximo.dado.equals(elemento)) {
            atual = atual.proximo;
        }

        if (atual.proximo != null) {
            atual.proximo = atual.proximo.proximo;
            tamanho--;
        }
    }

    public void remover(int indice) {
        if (indice < 0 || indice >= tamanho) {
            throw new IndexOutOfBoundsException("Índice inválido");
        }

        if (indice == 0) {
            inicio = inicio.proximo;
        } else {
            No<T> atual = inicio;
            for (int i = 0; i < indice - 1; i++) {
                atual = atual.proximo;
            }
            atual.proximo = atual.proximo.proximo;
        }
        tamanho--;
    }

    public boolean contem(T elemento) {
        No<T> atual = inicio;
        while (atual != null) {
            if (atual.dado.equals(elemento)) {
                return true;
            }
            atual = atual.proximo;
        }
        return false;
    }

    public int indiceDe(T elemento) {
        No<T> atual = inicio;
        int indice = 0;
        while (atual != null) {
            if (atual.dado.equals(elemento)) {
                return indice;
            }
            atual = atual.proximo;
            indice++;
        }
        return -1;
    }

    public int tamanho() {
        return tamanho;
    }

    public boolean vazia() {
        return tamanho == 0;
    }
}
