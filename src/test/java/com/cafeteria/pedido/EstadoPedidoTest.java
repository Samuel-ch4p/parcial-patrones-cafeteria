package com.cafeteria.pedido;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.cafeteria.modelo.Cliente;
import com.cafeteria.modelo.TipoEstado;
import com.cafeteria.notificacion.Notificador;
import com.cafeteria.producto.Espresso;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Patrón State: ciclo de vida del pedido. */
class EstadoPedidoTest {

    private Pedido pedido;

    @BeforeEach
    void preparar() {
        pedido = new Pedido(1, Cliente.anonimo(), new Notificador());
    }

    @Test
    void empiezaRecibido() {
        assertEquals(TipoEstado.RECIBIDO, pedido.tipoEstado());
    }

    @Test
    void recorreElFlujoNormalHastaEntregado() {
        pedido.avanzar();
        assertEquals(TipoEstado.EN_PREPARACION, pedido.tipoEstado());
        pedido.avanzar();
        assertEquals(TipoEstado.LISTO, pedido.tipoEstado());
        pedido.avanzar();
        assertEquals(TipoEstado.ENTREGADO, pedido.tipoEstado());
    }

    @Test
    void entregadoNoAvanzaNiSeCancela() {
        pedido.avanzar();
        pedido.avanzar();
        pedido.avanzar();
        TransicionInvalidaException e = assertThrows(TransicionInvalidaException.class, pedido::avanzar);
        assertTrue(e.getMessage().contains("Entregado"));
        assertThrows(TransicionInvalidaException.class, pedido::cancelar);
        assertEquals(TipoEstado.ENTREGADO, pedido.tipoEstado());
    }

    @Test
    void sePuedeCancelarDesdeRecibidoYEnPreparacion() {
        pedido.cancelar();
        assertEquals(TipoEstado.CANCELADO, pedido.tipoEstado());

        Pedido otro = new Pedido(2, Cliente.anonimo(), new Notificador());
        otro.avanzar();
        otro.cancelar();
        assertEquals(TipoEstado.CANCELADO, otro.tipoEstado());
    }

    @Test
    void noSeCancelaUnPedidoListo() {
        pedido.avanzar();
        pedido.avanzar();
        assertThrows(TransicionInvalidaException.class, pedido::cancelar);
        assertEquals(TipoEstado.LISTO, pedido.tipoEstado());
    }

    @Test
    void canceladoEsFinal() {
        pedido.cancelar();
        assertThrows(TransicionInvalidaException.class, pedido::avanzar);
        assertThrows(TransicionInvalidaException.class, pedido::cancelar);
    }

    @Test
    void soloSeModificaEnRecibido() {
        pedido.agregar(new Espresso(), 1);
        pedido.avanzar();
        assertThrows(PedidoException.class, () -> pedido.agregar(new Espresso(), 1));
        assertThrows(PedidoException.class, () -> pedido.quitar(0));
    }

    @Test
    void cadaEstadoDeclaraSuTipo() {
        assertEquals(TipoEstado.RECIBIDO, new Recibido().tipo());
        assertEquals(TipoEstado.EN_PREPARACION, new EnPreparacion().tipo());
        assertEquals(TipoEstado.LISTO, new Listo().tipo());
        assertEquals(TipoEstado.ENTREGADO, new Entregado().tipo());
        assertEquals(TipoEstado.CANCELADO, new Cancelado().tipo());
    }
}
