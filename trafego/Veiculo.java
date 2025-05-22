package trafego;

import cidade.Intersecao;
import cidade.Rua;
import estruturas.Lista;

public class Veiculo {
    private final String id;
    private final Intersecao origem;
    private final Intersecao destino;
    private Lista<Rua> rota;
    private int tempoViagem;
    private int tempoEspera;
    private Rua ruaAtual;
    private int tempoNaRua;
    private boolean chegou;

    public Veiculo(String id, Intersecao origem, Intersecao destino) {
        this.id = id;
        this.origem = origem;
        this.destino = destino;
        this.tempoViagem = 0;
        this.tempoEspera = 0;
        this.tempoNaRua = 0;
        this.chegou = false;
    }

    public String getId() {
        return id;
    }

    public Intersecao getOrigem() {
        return origem;
    }

    public Intersecao getDestino() {
        return destino;
    }

    public void setRuaAtual(Rua rua) {
        this.ruaAtual = rua;
    }

    public Lista<Rua> getRota() {
        return rota;
    }

    public void setRota(Lista<Rua> rota) {
        this.rota = rota;
        if (!rota.vazia()) {
            this.ruaAtual = rota.obter(0);
        }
    }

    public int getTempoViagem() {
        return tempoViagem;
    }

    public int getTempoEspera() {
        return tempoEspera;
    }

    public Rua getRuaAtual() {
        return ruaAtual;
    }

    public boolean isChegou() {
        return chegou;
    }

    public void avancarNaRota() {
        if (!rota.vazia()) {
            rota.remover(rota.obter(0));
        }
    }

    public void atualizar() {
        if (chegou) return;

        tempoViagem++;

        if (ruaAtual == null) {
            entrarNaProximaRua();
            return;
        }

        tempoNaRua++;

        if (tempoNaRua >= ruaAtual.getTempoTravessia()) {
            ruaAtual.removerVeiculo();
            ruaAtual = null;
            tempoNaRua = 0;
        }
    }

    private void entrarNaProximaRua() {
        if (!rota.vazia()) {
            Rua proximaRua = rota.obter(0);
            Intersecao atual = getIntersecaoAtual();

            if (podeAvancar(proximaRua, atual)) {
                rota.remover(0);
                if (atual.getSemaforo() != null) {
                    atual.removerVeiculo();
                }
                ruaAtual = proximaRua;
                tempoNaRua = 0;
            } else {
                tempoEspera++;
            }
        } else {
            chegou = true;
        }
    }

    private boolean podeAvancar(Rua proximaRua, Intersecao atual) {
        return (atual.getSemaforo() == null || atual.getSemaforo().estaVerde()) && proximaRua.adicionarVeiculo();
    }

    public Intersecao getIntersecaoAtual() {
        if (ruaAtual != null) {
            return ruaAtual.getDestino();
        }
        return origem;
    }

    public void incrementarTempoEspera() {
        tempoEspera++;
    }

    @Override
    public String toString() {
        return "Veiculo " + id + " (" + origem + " -> " + destino + ")";
    }
}
