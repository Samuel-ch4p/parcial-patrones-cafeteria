package com.cafeteria.notificacion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.cafeteria.modelo.CafeteriaException;
import com.cafeteria.modelo.Cliente;
import com.cafeteria.modelo.TipoEstado;
import com.cafeteria.pedido.Pedido;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Patrón Observer: alta y baja de receptores en ejecución. */
class NotificadorTest {

    private Notificador notificador;
    private List<String> salida;
    private PantallaCocina cocina;
    private NotificacionCliente cliente;
    private RegistroActividad registro;

    @BeforeEach
    void preparar() {
        notificador = new Notificador();
        salida = new ArrayList<>();
        cocina = new PantallaCocina(salida::add);
        cliente = new NotificacionCliente(salida::add);
        registro = new RegistroActividad(salida::add);
        notificador.agregar(cocina);
        notificador.agregar(cliente);
        notificador.agregar(registro);
    }

    @Test
    void todosLosReceptoresRecibenCadaCambio() {
        Pedido pedido = new Pedido(7, Cliente.frecuente("Ana"), notificador);
        pedido.avanzar();
        // creación + un cambio, por tres receptores
        assertEquals(6, salida.size());
        assertEquals(2, cocina.mensajes().size());
        assertEquals(2, cliente.mensajes().size());
        assertEquals(2, registro.mensajes().size());
        assertTrue(cocina.mensajes().get(1).contains("Recibido -> En preparación"));
        assertTrue(cliente.mensajes().get(1).contains("Ana (frecuente)"));
        assertTrue(registro.mensajes().get(1).contains("#7"));
    }

    @Test
    void unReceptorQuitadoDejaDeRecibir() {
        Pedido pedido = new Pedido(1, Cliente.anonimo(), notificador);
        notificador.quitar("cliente");
        pedido.avanzar();
        assertEquals(1, cliente.mensajes().size());
        assertEquals(2, cocina.mensajes().size());
        assertEquals(List.of("cocina", "registro"), notificador.nombres());
    }

    @Test
    void unReceptorAgregadoDespuesRecibeLosCambiosSiguientes() {
        notificador.quitar("cocina");
        Pedido pedido = new Pedido(1, Cliente.anonimo(), notificador);
        notificador.agregar(cocina);
        pedido.avanzar();
        assertEquals(1, cocina.mensajes().size());
        assertTrue(cocina.mensajes().get(0).contains("Recibido -> En preparación"));
    }

    @Test
    void rechazaNombresDuplicadosYQuitarUnoInexistente() {
        assertThrows(CafeteriaException.class, () -> notificador.agregar(new PantallaCocina(null)));
        assertThrows(CafeteriaException.class, () -> notificador.quitar("inexistente"));
    }

    @Test
    void sinReceptoresElPedidoSigueFuncionando() {
        notificador.quitar("cocina");
        notificador.quitar("cliente");
        notificador.quitar("registro");
        Pedido pedido = new Pedido(1, Cliente.anonimo(), notificador);
        pedido.avanzar();
        assertTrue(notificador.nombres().isEmpty());
        assertEquals(TipoEstado.EN_PREPARACION, pedido.tipoEstado());
    }

    @Test
    void unReceptorPropioSeAgregaSinTocarElPedido() {
        List<CambioEstado> recibidos = new ArrayList<>();
        notificador.agregar(new ReceptorEstado() {
            @Override
            public String nombre() {
                return "pruebas";
            }

            @Override
            public void actualizar(CambioEstado cambio) {
                recibidos.add(cambio);
            }
        });
        new Pedido(3, Cliente.anonimo(), notificador).cancelar();
        assertEquals(2, recibidos.size());
        assertEquals(TipoEstado.CANCELADO, recibidos.get(1).nuevo());
    }

    @Test
    void unReceptorQueFallaNoDejaSinAvisoALosDemas() {
        notificador.quitar("registro");
        notificador.agregar(new ReceptorEstado() {
            @Override
            public String nombre() {
                return "pantalla-rota";
            }

            @Override
            public void actualizar(CambioEstado cambio) {
                throw new IllegalStateException("pantalla desconectada");
            }
        });
        notificador.agregar(registro);
        Pedido pedido = new Pedido(8, Cliente.anonimo(), notificador);
        pedido.avanzar();
        assertEquals(TipoEstado.EN_PREPARACION, pedido.tipoEstado());
        assertEquals(2, cocina.mensajes().size());
        assertEquals(2, registro.mensajes().size());
    }
}
