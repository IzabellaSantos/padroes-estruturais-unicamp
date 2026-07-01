package br.unicamp.padroesestruturais.legacy.decorator;

public class JurosParcelamento implements AjusteValor {

    private final AjusteValor proximo;
    private static final double TAXA = 0.0299;

    public JurosParcelamento(AjusteValor proximo) {
        this.proximo = proximo;
    }

    @Override
    public double aplicar(double valor) {
        double valorAcumulado = proximo.aplicar(valor);
        return valorAcumulado + (valorAcumulado * TAXA);
    }
}