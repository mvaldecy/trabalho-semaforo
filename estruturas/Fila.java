package estruturas;

public class Fila<T> {
    private No<T> inicio;
    private No<T> fim;
    private int tamanho;

    public Fila() {
        inicio = null;
        fim = null;
        tamanho = 0;
    }

    public void enfileirar(T elemento) {
        No<T> novoNo = new No<>(elemento);
        if (fim == null) {
            inicio = novoNo;
            fim = novoNo;
        } else {
            fim.proximo = novoNo;
            fim = novoNo;
        }
        tamanho++;
    }

    public T desenfileirar() {
        if (inicio == null) return null;
        
        T elemento = inicio.dado;
        inicio = inicio.proximo;
        if (inicio == null) {
            fim = null;
        }
        tamanho--;
        return elemento;
    }

    public T primeiro() {
        if (inicio == null) return null;
        return inicio.dado;
    }

    public int tamanho() {
        return tamanho;
    }

    public boolean vazia() {
        return tamanho == 0;
    }
}