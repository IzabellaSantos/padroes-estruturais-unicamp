package br.unicamp.padroesestruturais.legacy.decorator;

public class ValorBasePedido implements AjusteValor {
    @Override
    public double aplicar(double valor) {
        return valor;
    }
}