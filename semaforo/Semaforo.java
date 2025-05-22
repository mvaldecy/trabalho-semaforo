package semaforo;

import cidade.Intersecao;

public class Semaforo {
    private final Intersecao intersecao;
    private EstadoSemaforo estado;
    private int tempoVerde;
    private int tempoAmarelo;
    private int tempoVermelho;
    private int tempoRestante;
    private ModoOperacao modoOperacao;

    public Semaforo(Intersecao intersecao, int tempoVerde, int tempoAmarelo, int tempoVermelho, ModoOperacao modo) {
        this.intersecao = intersecao;
        this.tempoVerde = tempoVerde;
        this.tempoAmarelo = tempoAmarelo;
        this.tempoVermelho = tempoVermelho;
        this.modoOperacao = modo;

        // Estado inicial aleatório
        int sorteio = (int)(Math.random() * 3);
        switch (sorteio) {
            case 0 -> {
                estado = EstadoSemaforo.VERDE;
                tempoRestante = tempoVerde;
            }
            case 1 -> {
                estado = EstadoSemaforo.AMARELO;
                tempoRestante = tempoAmarelo;
            }
            default -> {
                estado = EstadoSemaforo.VERMELHO;
                tempoRestante = tempoVermelho;
            }
        }
    }

    public void atualizar() {
        tempoRestante--;
        if (tempoRestante <= 0) {
            mudarEstado();
        }
    }

    public void mudarEstado() {
        estado = switch (estado) {
            case VERDE -> {
                tempoRestante = tempoAmarelo;
                yield EstadoSemaforo.AMARELO;
            }
            case AMARELO -> {
                tempoRestante = tempoVermelho;
                yield EstadoSemaforo.VERMELHO;
            }
            case VERMELHO -> {
                tempoRestante = tempoVerde;
                yield EstadoSemaforo.VERDE;
            }
        };
    }

    public boolean estaVerde() {
        return estado == EstadoSemaforo.VERDE;
    }

    public boolean estaAmarelo() {
        return estado == EstadoSemaforo.AMARELO;
    }

    public boolean estaVermelho() {
        return estado == EstadoSemaforo.VERMELHO;
    }

    public Intersecao getIntersecao() {
        return intersecao;
    }

    public int getTempoRestante() {
        return tempoRestante;
    }

    public void ajustarTempos(int verde, int amarelo, int vermelho) {
        this.tempoVerde = verde;
        this.tempoAmarelo = amarelo;
        this.tempoVermelho = vermelho;

        tempoRestante = switch (estado) {
            case VERDE -> (int) ((double) tempoRestante / tempoVerde * verde);
            case AMARELO -> (int) ((double) tempoRestante / tempoAmarelo * amarelo);
            case VERMELHO -> (int) ((double) tempoRestante / tempoVermelho * vermelho);
        };
    }

    public void setModoOperacao(ModoOperacao modo) {
        this.modoOperacao = modo;
    }

    public ModoOperacao getModoOperacao() {
        return modoOperacao;
    }

    @Override
    public String toString() {
        return "Semáforo " + intersecao.getNome() + ": " + estado + " (" + tempoRestante + "s)";
    }
}

enum EstadoSemaforo {
    VERDE, AMARELO, VERMELHO
}
