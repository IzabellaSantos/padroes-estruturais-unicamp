package br.unicamp.padroesestruturais.legacy.adapter;

import br.unicamp.padroesestruturais.legacy.domain.Pedido;
import br.unicamp.padroesestruturais.legacy.domain.ResultadoCobranca;

public interface GatewayCobrancaAdapter {
    ResultadoCobranca cobrar(Pedido pedido, double valorFinal);
}
