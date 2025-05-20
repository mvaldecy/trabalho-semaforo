package estruturas;

public class Pilha<T> {
    private No<T> topo;
    private int tamanho;

    public Pilha() {
        topo = null;
        tamanho = 0;
    }

    public void empilhar(T elemento) {
        No<T> novoNo = new No<>(elemento);
        novoNo.proximo = topo;
        topo = novoNo;
        tamanho++;
    }

    public T desempilhar() {
        if (topo == null) return null;
        
        T elemento = topo.dado;
        topo = topo.proximo;
        tamanho--;
        return elemento;
    }

    public T topo() {
        if (topo == null) return null;
        return topo.dado;
    }

    public int tamanho() {
        return tamanho;
    }

    public boolean vazia() {
        return tamanho == 0;
    }
}