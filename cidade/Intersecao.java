package cidade;

import estruturas.Fila;
import semaforo.Semaforo;
import trafego.Veiculo;

public class Intersecao {
    private final String nome;
    private final double latitude, longitude;
    private Semaforo semaforo;
    private final Fila<Veiculo> filaVeiculos;

    public Intersecao(String nome, double latitude, double longitude) {
        this.nome = nome;
        this.latitude = latitude;
        this.longitude = longitude;
        this.filaVeiculos = new Fila<>();
    }

    public String getNome() {
        return nome;
    }

    

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public Semaforo getSemaforo() {
        return semaforo;
    }

    public void setSemaforo(Semaforo semaforo) {
        this.semaforo = semaforo;
    }

    public Fila<Veiculo> getFilaVeiculos() {
        return filaVeiculos;
    }

    public void adicionarVeiculo(Veiculo veiculo) {
        filaVeiculos.enfileirar(veiculo);
    }

    public Veiculo removerVeiculo() {
        return filaVeiculos.desenfileirar();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Intersecao that = (Intersecao) obj;
        return nome.equals(that.nome);
    }

    @Override
    public int hashCode() {
        return nome.hashCode();
    }

    @Override
    public String toString() {
        return nome;
    }
}