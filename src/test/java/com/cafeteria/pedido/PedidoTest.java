package com.cafeteria.pedido;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.cafeteria.descuento.DescuentoClienteFrecuente;
import com.cafeteria.descuento.DescuentoCodigoPromocional;
import com.cafeteria.descuento.DescuentoCombo;
import com.cafeteria.descuento.DescuentoInvalidoException;
import com.cafeteria.descuento.SinDescuento;
import com.cafeteria.extras.Canela;
import com.cafeteria.extras.TamanoGrande;
import com.cafeteria.modelo.Cliente;
import com.cafeteria.notificacion.Notificador;
import com.cafeteria.pago.PagoEfectivo;
import com.cafeteria.pago.PagoInvalidoException;
import com.cafeteria.producto.Bebida;
import com.cafeteria.producto.BrownieChocolate;
import com.cafeteria.producto.Categoria;
import com.cafeteria.producto.Latte;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Cálculo de subtotal, descuento y total, y reglas de pago del pedido. */
class PedidoTest {

    private Pedido pedido;

    @BeforeEach
    void preparar() {
        pedido = new Pedido(1, Cliente.frecuente("Ana"), new Notificador());
    }

    @Test
    void subtotalSeActualizaTrasCadaCambio() {
        pedido.agregar(new Latte(), 2);
        assertEquals(16000, pedido.subtotal());
        pedido.agregar(new BrownieChocolate(), 1);
        assertEquals(21500, pedido.subtotal());
        pedido.cambiarCantidad(0, 1);
        assertEquals(13500, pedido.subtotal());
        pedido.quitar(1);
        assertEquals(8000, pedido.subtotal());
    }

    @Test
    void elSubtotalIncluyeLasPersonalizaciones() {
        pedido.agregar(new Canela(new TamanoGrande(new Latte())), 2);
        assertEquals(2 * (8000 + 2000 + 500), pedido.subtotal());
    }

    @Test
    void agregarElMismoItemSumaLaCantidad() {
        pedido.agregar(new Latte(), 1);
        pedido.agregar(new Latte(), 2);
        assertEquals(1, pedido.lineas().size());
        assertEquals(3, pedido.lineas().get(0).cantidad());
    }

    @Test
    void sumarMasDelMaximoExplicaCuantoSePuedeAgregar() {
        pedido.agregar(new Latte(), 60);
        PedidoException error = assertThrows(PedidoException.class, () -> pedido.agregar(new Latte(), 50));
        assertTrue(error.getMessage().contains("Ya hay 60 de Latte"), error.getMessage());
        assertTrue(error.getMessage().contains("puede agregar hasta 39"), error.getMessage());
        assertEquals(60, pedido.lineas().get(0).cantidad());
        pedido.agregar(new Latte(), 39);
        assertEquals(99, pedido.lineas().get(0).cantidad());
    }

    @Test
    void unItemNoPuedeSuperarElValorMaximo() {
        class CafeDeLujo extends Bebida {
            CafeDeLujo() {
                super("lujo", "Café de lujo", Categoria.CAFE, 2_000_000, "");
            }
        }
        pedido.agregar(new CafeDeLujo(), 50);
        assertEquals(100_000_000, pedido.subtotal());
        assertThrows(PedidoException.class, () -> pedido.agregar(new CafeDeLujo(), 1));
        assertThrows(PedidoException.class, () -> pedido.cambiarCantidad(0, 51));
        assertEquals(100_000_000, pedido.subtotal());
    }

    @Test
    void unPrecioGiganteNoProduceTotalesNegativos() {
        class Imposible extends Bebida {
            Imposible() {
                super("imposible", "Imposible", Categoria.CAFE, Long.MAX_VALUE / 50, "");
            }
        }
        assertThrows(PedidoException.class, () -> pedido.agregar(new Imposible(), 60));
        assertEquals(0, pedido.subtotal());
    }

    @Test
    void mismoProductoConOtraPersonalizacionEsOtroItem() {
        pedido.agregar(new Latte(), 1);
        pedido.agregar(new Canela(new Latte()), 1);
        assertEquals(2, pedido.lineas().size());
    }

    @Test
    void validaCantidadesEIndices() {
        assertThrows(PedidoException.class, () -> pedido.agregar(new Latte(), 0));
        assertThrows(PedidoException.class, () -> pedido.agregar(new Latte(), 100));
        pedido.agregar(new Latte(), 1);
        assertThrows(PedidoException.class, () -> pedido.cambiarCantidad(0, 0));
        assertThrows(PedidoException.class, () -> pedido.quitar(5));
        assertEquals(1, pedido.lineas().get(0).cantidad());
    }

    @Test
    void descuentoSeRestaDelTotal() {
        pedido.agregar(new Latte(), 1);
        pedido.agregar(new BrownieChocolate(), 1);
        pedido.aplicarDescuento(new DescuentoCombo());
        assertEquals(13500 * 15 / 100, pedido.descuento());
        assertEquals(13500 - pedido.descuento(), pedido.total());
        pedido.aplicarDescuento(new DescuentoClienteFrecuente());
        assertEquals(1350, pedido.descuento());
    }

    @Test
    void descuentoSeRecalculaSiCambianLosItems() {
        pedido.agregar(new Latte(), 1);
        pedido.aplicarDescuento(new DescuentoClienteFrecuente());
        assertEquals(800, pedido.descuento());
        pedido.cambiarCantidad(0, 2);
        assertEquals(1600, pedido.descuento());
    }

    @Test
    void codigoInvalidoNoCambiaElPedido() {
        pedido.agregar(new Latte(), 1);
        pedido.aplicarDescuento(new DescuentoClienteFrecuente());
        long totalAntes = pedido.total();
        assertThrows(DescuentoInvalidoException.class,
                () -> pedido.aplicarDescuento(new DescuentoCodigoPromocional("MALO")));
        assertEquals(totalAntes, pedido.total());
        assertEquals("Cliente frecuente 10%", pedido.politicaDescuento().nombre());
    }

    @Test
    void comboQueNoAplicaSeRechazaYConservaLaPolitica() {
        pedido.agregar(new Latte(), 1);
        assertThrows(DescuentoInvalidoException.class, () -> pedido.aplicarDescuento(new DescuentoCombo()));
        assertEquals("Sin descuento", pedido.politicaDescuento().nombre());
    }

    @Test
    void noSeAplicaDescuentoAUnPedidoVacio() {
        assertThrows(DescuentoInvalidoException.class,
                () -> pedido.aplicarDescuento(new DescuentoCodigoPromocional("CAFE10")));
        assertEquals("Sin descuento", pedido.politicaDescuento().nombre());
    }

    @Test
    void sePuedeQuitarElDescuentoAunqueElPedidoQuedeVacio() {
        pedido.agregar(new Latte(), 1);
        pedido.aplicarDescuento(new DescuentoClienteFrecuente());
        pedido.quitar(0);
        pedido.aplicarDescuento(new SinDescuento());
        assertEquals("Sin descuento", pedido.politicaDescuento().nombre());
    }

    @Test
    void noSePagaUnPedidoVacio() {
        PedidoException e = assertThrows(PedidoException.class, () -> pedido.pagar(new PagoEfectivo(1000)));
        assertTrue(e.getMessage().contains("vacío"));
        assertFalse(pedido.estaPagado());
    }

    @Test
    void noSePagaDosVecesNiSeModificaTrasPagar() {
        pedido.agregar(new Latte(), 1);
        pedido.pagar(new PagoEfectivo(10000));
        assertTrue(pedido.estaPagado());
        assertThrows(PedidoException.class, () -> pedido.pagar(new PagoEfectivo(10000)));
        assertThrows(PedidoException.class, () -> pedido.agregar(new Latte(), 1));
        assertThrows(PedidoException.class, () -> pedido.aplicarDescuento(new DescuentoClienteFrecuente()));
    }

    @Test
    void pagoRechazadoDejaElPedidoSinPagar() {
        pedido.agregar(new Latte(), 1);
        assertThrows(PagoInvalidoException.class, () -> pedido.pagar(new PagoEfectivo(100)));
        assertFalse(pedido.estaPagado());
        assertTrue(pedido.pago().isEmpty());
    }

    @Test
    void cobraElTotalConDescuento() {
        pedido.agregar(new Latte(), 1);
        pedido.aplicarDescuento(new DescuentoClienteFrecuente());
        assertEquals(7200, pedido.pagar(new PagoEfectivo(10000)).monto());
        assertEquals(2800, pedido.pago().orElseThrow().cambio());
    }

    @Test
    void noSePagaUnPedidoCancelado() {
        pedido.agregar(new Latte(), 1);
        pedido.cancelar();
        assertThrows(PedidoException.class, () -> pedido.pagar(new PagoEfectivo(10000)));
    }
}
