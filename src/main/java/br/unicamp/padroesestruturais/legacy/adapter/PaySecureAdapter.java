package br.unicamp.padroesestruturais.legacy.adapter;

import br.unicamp.padroesestruturais.legacy.domain.FormaPagamento;
import br.unicamp.padroesestruturais.legacy.domain.Pedido;
import br.unicamp.padroesestruturais.legacy.domain.ResultadoCobranca;
import br.unicamp.padroesestruturais.legacy.externo.GatewayIndisponivelException;
import br.unicamp.padroesestruturais.legacy.externo.PaySecureGateway;
import br.unicamp.padroesestruturais.legacy.externo.TransacaoExterna;

import java.util.HashMap;
import java.util.Map;

public class PaySecureAdapter implements GatewayCobrancaAdapter {

    private final PaySecureGateway gateway;

    public PaySecureAdapter() {
        this.gateway = new PaySecureGateway();
    }

    @Override
    public ResultadoCobranca cobrar(Pedido pedido, double valorFinal) {
        Map<String, Object> dadosTransacao = new HashMap<>();
        dadosTransacao.put("orderId", pedido.getId());
        dadosTransacao.put("customerName", pedido.getCliente());
        dadosTransacao.put("amount", valorFinal);
        dadosTransacao.put("currency", "BRL");

        try {
            TransacaoExterna transacao = gateway.processarTransacao(dadosTransacao);
            String status = transacao.getCodigoStatus() == 200 ? "APROVADA" : "RECUSADA";
            return new ResultadoCobranca(pedido.getId(), valorFinal, status, transacao.getReferenciaExterna(), FormaPagamento.CARTAO_CREDITO);
        } catch (GatewayIndisponivelException e) {
            return new ResultadoCobranca(pedido.getId(), valorFinal, "RECUSADA", null, FormaPagamento.CARTAO_CREDITO);
        }
    }
}
