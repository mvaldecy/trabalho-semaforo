package cidade;

public class Rua {
    private String id;
    private Intersecao origem;
    private Intersecao destino;
    private int tempoTravessia;
    private int capacidade;
    private boolean maoDupla;
    private double comprimento;
    private double velocidadeMax;
    private int veiculosNaVia;

    public Rua(
        String id,
        Intersecao origem,
        Intersecao destino,
        int tempoTravessia,
        int capacidade,
        boolean maoDupla,
        double comprimento,
        double velocidadeMax
        ) {
        this.id = id;
        this.origem = origem;
        this.destino = destino;
        this.tempoTravessia = tempoTravessia;
        this.capacidade = capacidade;
        this.maoDupla = maoDupla;
        this.comprimento = comprimento;
        this.velocidadeMax = velocidadeMax;
        this.veiculosNaVia = 0;
    }

    public Intersecao getOrigem() {
        return origem;
    }

    

    public Intersecao getDestino() {
        return destino;
    }

    public int getTempoTravessia() {
        return tempoTravessia;
    }

    public int getCapacidade() {
        return capacidade;
    }

    public boolean isMaoDupla() {
        return maoDupla;
    }

    public int getVeiculosNaVia() {
        return veiculosNaVia;
    }

    public boolean adicionarVeiculo() {
        if (veiculosNaVia < capacidade) {
            veiculosNaVia++;
            return true;
        }
        return false;
    }

    public void removerVeiculo() {
        if (veiculosNaVia > 0) {
            veiculosNaVia--;
        }
    }

    public double getTaxaOcupacao() {
        return (double) veiculosNaVia / capacidade;
    }

    @Override
    public String toString() {
        return origem + " -> " + destino + " (" + tempoTravessia + " min)";
    }

    /**
     * @return String return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @param origem the origem to set
     */
    public void setOrigem(Intersecao origem) {
        this.origem = origem;
    }

    /**
     * @param destino the destino to set
     */
    public void setDestino(Intersecao destino) {
        this.destino = destino;
    }

    /**
     * @param tempoTravessia the tempoTravessia to set
     */
    public void setTempoTravessia(int tempoTravessia) {
        this.tempoTravessia = tempoTravessia;
    }

    /**
     * @param capacidade the capacidade to set
     */
    public void setCapacidade(int capacidade) {
        this.capacidade = capacidade;
    }

    /**
     * @return double return the comprimento
     */
    public double getComprimento() {
        return comprimento;
    }

    /**
     * @param comprimento the comprimento to set
     */
    public void setComprimento(double comprimento) {
        this.comprimento = comprimento;
    }

    /**
     * @param maoDupla the maoDupla to set
     */
    public void setMaoDupla(boolean maoDupla) {
        this.maoDupla = maoDupla;
    }

    /**
     * @return double return the velocidadeMax
     */
    public double getVelocidadeMax() {
        return velocidadeMax;
    }

    /**
     * @param velocidadeMax the velocidadeMax to set
     */
    public void setVelocidadeMax(double velocidadeMax) {
        this.velocidadeMax = velocidadeMax;
    }

    /**
     * @param veiculosNaVia the veiculosNaVia to set
     */
    public void setVeiculosNaVia(int veiculosNaVia) {
        this.veiculosNaVia = veiculosNaVia;
    }

}