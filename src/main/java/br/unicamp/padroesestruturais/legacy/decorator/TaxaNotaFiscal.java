package br.unicamp.padroesestruturais.legacy.decorator;

public class TaxaNotaFiscal implements AjusteValor {

    private final AjusteValor proximo;
    private static final double VALOR_FIXO = 2.50;

    public TaxaNotaFiscal(AjusteValor proximo) {
        this.proximo = proximo;
    }

    @Override
    public double aplicar(double valor) {
        double valorAcumulado = proximo.aplicar(valor);
        return valorAcumulado + VALOR_FIXO;
    }
}