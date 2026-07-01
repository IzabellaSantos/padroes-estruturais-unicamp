package br.unicamp.padroesestruturais.legacy.service;

import br.unicamp.padroesestruturais.legacy.adapter.GatewayCobrancaAdapter;
import br.unicamp.padroesestruturais.legacy.adapter.GatewayInternoAdapter;
import br.unicamp.padroesestruturais.legacy.adapter.PaySecureAdapter;
import br.unicamp.padroesestruturais.legacy.adapter.WalletPayAdapter;
import br.unicamp.padroesestruturais.legacy.decorator.*;
import br.unicamp.padroesestruturais.legacy.domain.FormaPagamento;
import br.unicamp.padroesestruturais.legacy.domain.Pedido;
import br.unicamp.padroesestruturais.legacy.domain.ResultadoCobranca;

import java.util.ArrayList;
import java.util.List;

public class CobrancaService {
    public ResultadoCobranca cobrar(Pedido pedido, FormaPagamento forma, AjusteValor calculoDecorado) {
        double valorFinal = calculoDecorado.aplicar(pedido.getValorBase());
        
        GatewayCobrancaAdapter gateway = resolverGateway(forma);
        return gateway.cobrar(pedido, valorFinal);
    }

    public List<ResultadoCobranca> cobrarEmLote(List<Pedido> pedidos, FormaPagamento forma, AjusteValor calculoDecorado) {
        GatewayCobrancaAdapter gateway = resolverGateway(forma);
        List<ResultadoCobranca> resultados = new ArrayList<>();

        for (Pedido pedido : pedidos) {
            double valorFinal = calculoDecorado.aplicar(pedido.getValorBase());
            resultados.add(gateway.cobrar(pedido, valorFinal));
        }

        return resultados;
    }

    public ResultadoCobranca cobrar(Pedido pedido, FormaPagamento forma,
                                     boolean aplicarDescontoFidelidade,
                                     boolean aplicarJurosParcelamento,
                                     boolean aplicarTaxaInternacional,
                                     boolean aplicarSeguro) {
        
        AjusteValor cadeia = new ValorBasePedido();

        if (aplicarDescontoFidelidade) cadeia = new DescontoFidelidade(cadeia);
        if (aplicarJurosParcelamento)  cadeia = new JurosParcelamento(cadeia);
        if (aplicarTaxaInternacional)  cadeia = new TaxaInternacional(cadeia);
        if (aplicarSeguro)             cadeia = new Seguro(cadeia);

        return cobrar(pedido, forma, cadeia);
    }

    public List<ResultadoCobranca> cobrarEmLote(List<Pedido> pedidos, FormaPagamento forma,
                                                 boolean aplicarDescontoFidelidade,
                                                 boolean aplicarJurosParcelamento,
                                                 boolean aplicarTaxaInternacional,
                                                 boolean aplicarSeguro) {
        AjusteValor cadeia = new ValorBasePedido();

        if (aplicarDescontoFidelidade) cadeia = new DescontoFidelidade(cadeia);
        if (aplicarJurosParcelamento)  cadeia = new JurosParcelamento(cadeia);
        if (aplicarTaxaInternacional)  cadeia = new TaxaInternacional(cadeia);
        if (aplicarSeguro)             cadeia = new Seguro(cadeia);

        return cobrarEmLote(pedidos, forma, cadeia);
    }

    public double calcularValorFinal(double valorBase,
                                      boolean aplicarDescontoFidelidade,
                                      boolean aplicarJurosParcelamento,
                                      boolean aplicarTaxaInternacional,
                                      boolean aplicarSeguro) {
        AjusteValor cadeia = new ValorBasePedido();

        if (aplicarDescontoFidelidade) cadeia = new DescontoFidelidade(cadeia);
        if (aplicarJurosParcelamento)  cadeia = new JurosParcelamento(cadeia);
        if (aplicarTaxaInternacional)  cadeia = new TaxaInternacional(cadeia);
        if (aplicarSeguro)             cadeia = new Seguro(cadeia);

        return cadeia.aplicar(valorBase);
    }

    private GatewayCobrancaAdapter resolverGateway(FormaPagamento forma) {
        if (forma == FormaPagamento.BOLETO || forma == FormaPagamento.PIX) {
            return new GatewayInternoAdapter(forma);
        } else if (forma == FormaPagamento.CARTAO_CREDITO) {
            return new PaySecureAdapter();
        } else if (forma == FormaPagamento.CARTEIRA_DIGITAL) {
            return new WalletPayAdapter();
        }
        throw new IllegalArgumentException("Forma de pagamento nao suportada: " + forma);
    }
}
