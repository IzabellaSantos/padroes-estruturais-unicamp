package br.unicamp.padroesestruturais.legacy;

import br.unicamp.padroesestruturais.legacy.domain.FormaPagamento;
import br.unicamp.padroesestruturais.legacy.domain.Pedido;
import br.unicamp.padroesestruturais.legacy.domain.ResultadoCobranca;
import br.unicamp.padroesestruturais.legacy.service.CobrancaService;
import br.unicamp.padroesestruturais.legacy.decorator.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CobrancaServiceTest {

    private CobrancaService service;
    private Pedido pedido;

    @BeforeEach
    void setUp() {
        service = new CobrancaService();
        pedido = new Pedido("PED-001", "Joao Silva", "Notebook Dell XPS 15", 1000.0);
    }

    @Test
    void deveCobrarViaBoletoSemAjustes() {
        AjusteValor calculo = new ValorBasePedido();
        ResultadoCobranca resultado = service.cobrar(pedido, FormaPagamento.BOLETO, calculo);

        assertEquals("APROVADA", resultado.getStatus());
        assertEquals(1000.0, resultado.getValorCobrado(), 0.001);
        assertEquals(FormaPagamento.BOLETO, resultado.getFormaPagamento());
    }

    @Test
    void deveCobrarViaCartaoAplicandoDescontoFidelidade() {
        AjusteValor calculo = new DescontoFidelidade(new ValorBasePedido());
        ResultadoCobranca resultado = service.cobrar(pedido, FormaPagamento.CARTAO_CREDITO, calculo);

        assertEquals("APROVADA", resultado.getStatus());
        assertEquals(950.0, resultado.getValorCobrado(), 0.001);
    }

    @Test
    void deveCobrarViaCarteiraDigitalComMultiplosAjustes() {
        AjusteValor calculo = new Seguro(
                                new TaxaInternacional(
                                    new JurosParcelamento(
                                        new DescontoFidelidade(
                                            new ValorBasePedido()
                                        )
                                    )
                                )
                             );

        ResultadoCobranca resultado = service.cobrar(pedido, FormaPagamento.CARTEIRA_DIGITAL, calculo);

        double esperado = 1000.0;
        esperado = esperado - (esperado * 0.05);
        esperado = esperado + (esperado * 0.0299);
        esperado = esperado + (esperado * 0.05);
        esperado = esperado + 4.90;

        assertEquals("APROVADA", resultado.getStatus());
        assertEquals(esperado, resultado.getValorCobrado(), 0.001);
    }

    @Test
    void deveTestarNovosAjustesExigidosPeloTrabalho() {
        AjusteValor calculo = new TaxaNotaFiscal(
                                new TaxaAntecipacao(
                                    new ValorBasePedido()
                                )
                             );

        ResultadoCobranca resultado = service.cobrar(pedido, FormaPagamento.PIX, calculo);

        double esperado = 1000.0;
        esperado = esperado + (esperado * 0.015);
        esperado = esperado + 2.50;

        assertEquals("APROVADA", resultado.getStatus());
        assertEquals(esperado, resultado.getValorCobrado(), 0.001);
    }

    @Test
    void deveCobrarEmLoteParaTodosPedidos() {
        List<Pedido> pedidos = Arrays.asList(
                new Pedido("PED-001", "Joao Silva", "Notebook", 1000.0),
                new Pedido("PED-002", "Maria Santos", "Cadeira", 500.0)
        );

        AjusteValor calculo = new ValorBasePedido();
        List<ResultadoCobranca> resultados = service.cobrarEmLote(pedidos, FormaPagamento.PIX, calculo);

        assertEquals(2, resultados.size());
        for (ResultadoCobranca resultado : resultados) {
            assertEquals("APROVADA", resultado.getStatus());
        }
    }

    @Test
    void cobrancaEmLoteDeveAplicarAjustesATodosPedidos() {
        List<Pedido> pedidos = Arrays.asList(
                new Pedido("PED-001", "Joao Silva", "Notebook", 1000.0),
                new Pedido("PED-002", "Maria Santos", "Cadeira", 500.0)
        );

        AjusteValor calculo = new DescontoFidelidade(new ValorBasePedido());
        List<ResultadoCobranca> resultados = service.cobrarEmLote(pedidos, FormaPagamento.PIX, calculo);

        assertEquals(950.0, resultados.get(0).getValorCobrado(), 0.001);
        assertEquals(475.0, resultados.get(1).getValorCobrado(), 0.001);
    }
}
