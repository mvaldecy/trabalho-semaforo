package trafego;

import cidade.Rua;
import cidade.Intersecao;
import estruturas.Lista;

public class Estatisticas {
    private int totalVeiculos;
    private int veiculosChegaram;
    private int tempoTotalViagem;
    private int tempoTotalEspera;
    private Lista<Rua> ruasMaisCongestionadas;
    private int tempoSimulacao;

    public Estatisticas() {
        this.totalVeiculos = 0;
        this.veiculosChegaram = 0;
        this.tempoTotalViagem = 0;
        this.tempoTotalEspera = 0;
        this.ruasMaisCongestionadas = new Lista<>();
    }

    public void setTempoSimulacao(int tempo) {
        this.tempoSimulacao = tempo;
    }

    public void registrarVeiculoCriado() {
        totalVeiculos++;
    }

    public void registrarVeiculoChegou(Veiculo veiculo) {
        veiculosChegaram++;
        tempoTotalViagem += veiculo.getTempoViagem();
        tempoTotalEspera += veiculo.getTempoEspera();
    }

    public void atualizarRuasCongestionadas(Lista<Rua> ruas) {
        Lista<Rua> ordenadas = ordenarRuasPorOcupacao(ruas);
        ruasMaisCongestionadas = new Lista<>();
        for (int i = 0; i < Math.min(5, ordenadas.tamanho()); i++) {
            if (ordenadas.obter(i).getTaxaOcupacao() > 0) {
                ruasMaisCongestionadas.adicionar(ordenadas.obter(i));
            }
        }
    }

    private Lista<Rua> ordenarRuasPorOcupacao(Lista<Rua> ruas) {
        Lista<Rua> ordenadas = new Lista<>();
        for (int i = 0; i < ruas.tamanho(); i++) {
            Rua atual = ruas.obter(i);
            int j = 0;
            while (j < ordenadas.tamanho() && atual.getTaxaOcupacao() < ordenadas.obter(j).getTaxaOcupacao()) {
                j++;
            }
            ordenadas.inserir(j, atual); // Inserção ordenada decrescente
        }
        return ordenadas;
    }

    public double getTempoMedioViagem() {
        return veiculosChegaram > 0 ? (double) tempoTotalViagem / veiculosChegaram : 0;
    }

    public double getTempoMedioEspera() {
        return veiculosChegaram > 0 ? (double) tempoTotalEspera / veiculosChegaram : 0;
    }

    public double getTaxaConclusao() {
        return totalVeiculos > 0 ? (double) veiculosChegaram / totalVeiculos * 100 : 0;
    }

    public Lista<Rua> getRuasMaisCongestionadas() {
        return ruasMaisCongestionadas;
    }

    public void imprimirEstatisticas() {
        System.out.println("\n===== ESTATÍSTICAS DA SIMULAÇÃO =====");
        System.out.printf("Veículos criados: %d\n", totalVeiculos);
        System.out.printf("Veículos que chegaram: %d (%.1f%%)\n", veiculosChegaram, getTaxaConclusao());
        System.out.printf("Tempo médio de viagem: %.1f minutos\n", getTempoMedioViagem());
        System.out.printf("Tempo médio de espera: %.1f minutos\n", getTempoMedioEspera());

        if (tempoSimulacao > 0) {
            System.out.printf("Fluxo médio: %.2f veículos/minuto\n", (double) veiculosChegaram / tempoSimulacao);
        }

        System.out.println("\nRuas mais congestionadas:");
        double ocupTotal = 0;
        for (int i = 0; i < ruasMaisCongestionadas.tamanho(); i++) {
            Rua rua = ruasMaisCongestionadas.obter(i);
            double ocup = rua.getTaxaOcupacao();
            ocupTotal += ocup;

            Intersecao destino = rua.getDestino();
            int tamanhoFila = destino.getFilaVeiculos().tamanho();

            System.out.printf("- %s -> %s (%.0f%% ocupação) | Fila na interseção destino: %d veículos\n",
                    rua.getOrigem(), destino, ocup * 100, tamanhoFila);
        }

        if (ruasMaisCongestionadas.tamanho() > 0) {
            System.out.printf("Ocupação média (top ruas): %.1f%%\n",
                    (ocupTotal / ruasMaisCongestionadas.tamanho()) * 100);
        }
    }
}
