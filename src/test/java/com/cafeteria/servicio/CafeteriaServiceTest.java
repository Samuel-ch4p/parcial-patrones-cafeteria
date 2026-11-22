package com.cafeteria.servicio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.cafeteria.descuento.DescuentoInvalidoException;
import com.cafeteria.extras.TipoOpcion;
import com.cafeteria.modelo.CafeteriaException;
import com.cafeteria.modelo.TipoEstado;
import com.cafeteria.pago.PagoInvalidoException;
import com.cafeteria.pedido.Pedido;
import com.cafeteria.pedido.PedidoException;
import com.cafeteria.producto.Producto;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Patrón Facade: el servicio coordina todo el flujo sin que quien llama conozca el núcleo. */
class CafeteriaServiceTest {

    private CafeteriaService servicio;
    private List<String> notificaciones;

    @BeforeEach
    void preparar() {
        notificaciones = new ArrayList<>();
        servicio = new CafeteriaService(notificaciones::add);
    }

    @Test
    void catalogoYDetalle() {
        assertTrue(servicio.catalogoPorCategoria().size() >= 4);
        Producto p = servicio.obtenerProducto("espresso");
        assertEquals("Espresso", p.nombre());
        assertThrows(CafeteriaException.class, () -> servicio.obtenerProducto("nada"));
    }

    @Test
    void previsualizaUnaBebidaPersonalizadaSinCrearPedido() {
        Producto p = servicio.previsualizar("latte", "grande", "avena", List.of("shot"));
        assertEquals(8000 + 2000 + 1500 + 2000, p.precio());
        assertTrue(servicio.listarPedidos().isEmpty());
        assertEquals(3, servicio.opciones(TipoOpcion.TAMANO).size());
    }

    @Test
    void flujoCompletoDePedido() {
        Pedido pedido = servicio.crearPedido("Ana", true);
        int id = pedido.id();
        servicio.agregarItem(id, "capuchino", 2, "grande", "almendras", List.of("canela"));
        servicio.agregarItem(id, "croissant", 1);
        assertEquals(2 * (7500 + 2000 + 1500 + 500) + 4500, servicio.subtotal(id));

        servicio.aplicarDescuentoCombo(id);
        assertEquals(servicio.subtotal(id) - servicio.subtotal(id) * 15 / 100, pedido.total());

        assertThrows(PagoInvalidoException.class, () -> servicio.pagarTarjeta(id, "123"));
        servicio.pagarTarjeta(id, "4111111111111111");
        assertThrows(PedidoException.class, () -> servicio.pagarEfectivo(id, 100000));

        servicio.avanzarEstado(id);
        servicio.avanzarEstado(id);
        servicio.avanzarEstado(id);
        assertEquals(TipoEstado.ENTREGADO, pedido.tipoEstado());

        // creación + 3 cambios, por 3 receptores
        assertEquals(12, notificaciones.size());
        assertEquals(4, servicio.registroActividad().size());

        assertTrue(servicio.generarTicket(id, "texto plano").contains("Tarjeta terminada en 1111"));
        assertTrue(servicio.generarTicket(id, "RESUMIDO").contains("Pedido #" + id));
        assertThrows(CafeteriaException.class, () -> servicio.generarTicket(id, "pdf"));
    }

    @Test
    void codigoPromocionalInvalidoDejaElPedidoIgual() {
        int id = servicio.crearPedidoAnonimo().id();
        servicio.agregarItem(id, "latte", 1);
        long antes = servicio.obtenerPedido(id).total();
        assertThrows(DescuentoInvalidoException.class, () -> servicio.aplicarCodigoPromocional(id, "XXXX"));
        assertEquals(antes, servicio.obtenerPedido(id).total());
        servicio.aplicarCodigoPromocional(id, "cafe10");
        assertEquals(antes - antes / 10, servicio.obtenerPedido(id).total());
        servicio.quitarDescuento(id);
        assertEquals(antes, servicio.obtenerPedido(id).total());
    }

    @Test
    void descuentoFrecuenteSoloParaClientesFrecuentes() {
        int id = servicio.crearPedidoAnonimo().id();
        servicio.agregarItem(id, "latte", 1);
        assertThrows(DescuentoInvalidoException.class, () -> servicio.aplicarDescuentoClienteFrecuente(id));
    }

    @Test
    void unAlimentoNoSePersonaliza() {
        int id = servicio.crearPedidoAnonimo().id();
        assertThrows(CafeteriaException.class,
                () -> servicio.agregarItem(id, "croissant", 1, "grande", null, List.of()));
        assertTrue(servicio.obtenerPedido(id).lineas().isEmpty());
    }

    @Test
    void pedidoVacioNoSePaga() {
        int id = servicio.crearPedidoAnonimo().id();
        assertThrows(PedidoException.class, () -> servicio.pagarEfectivo(id, 1000));
    }

    @Test
    void pedidoInexistente() {
        assertThrows(PedidoException.class, () -> servicio.obtenerPedido(99));
    }

    @Test
    void historialFiltraPorEstado() {
        int a = servicio.crearPedidoAnonimo().id();
        int b = servicio.crearPedidoAnonimo().id();
        servicio.crearPedidoAnonimo();
        servicio.avanzarEstado(b);
        servicio.cancelarPedido(a);
        assertEquals(3, servicio.listarPedidos().size());
        assertEquals(1, servicio.listarPedidos(TipoEstado.CANCELADO).size());
        assertEquals(1, servicio.listarPedidos(TipoEstado.EN_PREPARACION).size());
        assertEquals(1, servicio.listarPedidos(TipoEstado.RECIBIDO).size());
        assertTrue(servicio.listarPedidos(TipoEstado.LISTO).isEmpty());
    }

    @Test
    void receptoresSeAgreganYQuitanEnEjecucion() {
        assertEquals(List.of("cocina", "cliente", "registro"), servicio.receptoresActivos());
        servicio.quitarReceptor("cliente");
        assertEquals(List.of("cliente"), servicio.receptoresInactivos());
        int id = servicio.crearPedidoAnonimo().id();
        notificaciones.clear();
        servicio.avanzarEstado(id);
        assertEquals(2, notificaciones.size());
        assertTrue(notificaciones.stream().noneMatch(m -> m.startsWith("[CLIENTE]")));
        servicio.agregarReceptor("cliente");
        notificaciones.clear();
        servicio.avanzarEstado(id);
        assertEquals(3, notificaciones.size());
        assertThrows(CafeteriaException.class, () -> servicio.quitarReceptor("nadie"));
        assertThrows(CafeteriaException.class, () -> servicio.agregarReceptor("nadie"));
    }
}
