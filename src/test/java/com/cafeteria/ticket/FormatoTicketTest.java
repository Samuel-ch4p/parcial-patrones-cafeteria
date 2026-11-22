package com.cafeteria.ticket;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.cafeteria.descuento.DescuentoClienteFrecuente;
import com.cafeteria.extras.Canela;
import com.cafeteria.extras.TamanoGrande;
import com.cafeteria.modelo.Cliente;
import com.cafeteria.modelo.Moneda;
import com.cafeteria.notificacion.Notificador;
import com.cafeteria.pago.PagoEfectivo;
import com.cafeteria.pedido.Pedido;
import com.cafeteria.producto.BrownieChocolate;
import com.cafeteria.producto.Latte;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Patrón Strategy aplicado a los formatos de ticket. */
class FormatoTicketTest {

    private Pedido pedido;

    @BeforeEach
    void preparar() {
        pedido = new Pedido(4, Cliente.frecuente("Ana"), new Notificador());
        pedido.agregar(new Canela(new TamanoGrande(new Latte())), 2);
        pedido.agregar(new BrownieChocolate(), 1);
        pedido.aplicarDescuento(new DescuentoClienteFrecuente());
    }

    @Test
    void textoPlanoIncluyeItemsExtrasDescuentoTotalYPago() {
        pedido.pagar(new PagoEfectivo(50000));
        String ticket = new TicketTextoPlano().generar(pedido);
        assertTrue(ticket.contains("Pedido #4"));
        assertTrue(ticket.contains("2 x Latte"));
        assertTrue(ticket.contains("+ Grande"));
        assertTrue(ticket.contains("+ Canela"));
        assertTrue(ticket.contains("1 x Brownie de chocolate"));
        assertTrue(ticket.contains("Descuento: Cliente frecuente 10%"));
        assertTrue(ticket.contains("TOTAL"));
        assertTrue(ticket.contains("Pago: Efectivo (recibido $50.000"));
    }

    @Test
    void textoPlanoMuestraPagoPendienteYOmiteDescuentoSiNoHay() {
        Pedido simple = new Pedido(5, Cliente.anonimo(), new Notificador());
        simple.agregar(new Latte(), 1);
        String ticket = new TicketTextoPlano().generar(simple);
        assertTrue(ticket.contains("Pago: pendiente"));
        assertFalse(ticket.contains("Descuento"));
        assertTrue(ticket.contains("Cliente anónimo"));
    }

    @Test
    void resumidoOcupaDosLineasConLosDatosClave() {
        pedido.pagar(new PagoEfectivo(50000));
        String ticket = new TicketResumido().generar(pedido);
        assertEquals(2, ticket.strip().split("\n").length);
        assertTrue(ticket.contains("Pedido #4"));
        assertTrue(ticket.contains("3 unidades"));
        assertTrue(ticket.contains("Descuento -$"));
        assertTrue(ticket.contains("TOTAL $"));
        assertTrue(ticket.contains("Pago: Efectivo"));
    }

    @Test
    void losDosFormatosTienenNombresDistintos() {
        assertEquals("Texto plano", new TicketTextoPlano().nombre());
        assertEquals("Resumido", new TicketResumido().nombre());
    }

    @Test
    void ambosFormatosMuestranElMismoTotal() {
        String total = Moneda.formatear(pedido.total());
        assertTrue(new TicketTextoPlano().generar(pedido).contains(total));
        assertTrue(new TicketResumido().generar(pedido).contains(total));
    }
}
