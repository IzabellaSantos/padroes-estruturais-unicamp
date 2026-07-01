package br.unicamp.padroesestruturais.legacy.decorator;

public class TaxaInternacional implements AjusteValor {

    private final AjusteValor proximo;
    private static final double TAXA = 0.05;

    public TaxaInternacional(AjusteValor proximo) {
        this.proximo = proximo;
    }

    @Override
    public double aplicar(double valor) {
        double valorAcumulado = proximo.aplicar(valor);
        return valorAcumulado + (valorAcumulado * TAXA);
    }
}