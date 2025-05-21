import semaforo.ModoOperacao;

public class Main {
    public static void main(String[] args) {
        Simulador simulador = new Simulador();
        simulador.iniciar("MoradadoSolTeresinaPiauíBrazil.json", 100, ModoOperacao.CONSUMO_ENERGIA);
    }
}