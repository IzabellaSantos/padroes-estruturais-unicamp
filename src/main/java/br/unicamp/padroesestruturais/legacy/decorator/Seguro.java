package br.unicamp.padroesestruturais.legacy.decorator;

public class Seguro implements AjusteValor {

    private final AjusteValor proximo;
    private static final double VALOR_FIXO = 4.90;

    public Seguro(AjusteValor proximo) {
        this.proximo = proximo;
    }

    @Override
    public double aplicar(double valor) {
        double valorAcumulado = proximo.aplicar(valor);
        return valorAcumulado + VALOR_FIXO;
    }
}