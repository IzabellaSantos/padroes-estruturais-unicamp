package br.unicamp.padroesestruturais.legacy.adapter;

import br.unicamp.padroesestruturais.legacy.domain.FormaPagamento;
import br.unicamp.padroesestruturais.legacy.domain.Pedido;
import br.unicamp.padroesestruturais.legacy.domain.ResultadoCobranca;
import br.unicamp.padroesestruturais.legacy.gateway.GatewayPagamentoInterno;

public class GatewayInternoAdapter implements GatewayCobrancaAdapter {

    private final GatewayPagamentoInterno gateway;
    private final FormaPagamento forma;

    public GatewayInternoAdapter(FormaPagamento forma) {
        this.gateway = new GatewayPagamentoInterno();
        this.forma = forma;
    }

    @Override
    public ResultadoCobranca cobrar(Pedido pedido, double valorFinal) {
        return gateway.cobrar(pedido.getId(), pedido.getCliente(), valorFinal, forma);
    }
}
