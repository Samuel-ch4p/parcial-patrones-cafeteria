package com.cafeteria.gui;

import com.cafeteria.modelo.CafeteriaException;
import com.cafeteria.pedido.Pedido;
import com.cafeteria.servicio.CafeteriaService;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 * Estado compartido de la ventana: la fachada, el pedido en curso y los paneles
 * que deben refrescarse cuando algo cambia. No contiene reglas de negocio.
 *
 * @author Samuel Chaparro, Cristian Daniel Blanco, Juan Diego Afanador
 */
class SesionGui {

    private final CafeteriaService servicio;
    private final List<Runnable> oyentes = new ArrayList<>();
    private Integer pedidoActualId;

    SesionGui(CafeteriaService servicio) {
        this.servicio = servicio;
    }

    CafeteriaService servicio() {
        return servicio;
    }

    void alCambiar(Runnable oyente) {
        oyentes.add(oyente);
    }

    void notificarCambio() {
        oyentes.forEach(Runnable::run);
    }

    /** Pedido en curso, o {@code null} si todavía no se ha creado ninguno. */
    Pedido pedidoActual() {
        return pedidoActualId == null ? null : servicio.obtenerPedido(pedidoActualId);
    }

    void seleccionar(int pedidoId) {
        pedidoActualId = pedidoId;
        notificarCambio();
    }

    /** Devuelve el pedido en curso y, si no hay, crea uno anónimo. */
    Pedido pedidoOCrear() {
        Pedido pedido = pedidoActual();
        if (pedido == null) {
            pedido = servicio.crearPedidoAnonimo();
            seleccionar(pedido.id());
        }
        return pedido;
    }

    /** Ejecuta una acción y muestra el mensaje de error de negocio, si lo hay. */
    void ejecutar(Component padre, Runnable accion) {
        try {
            accion.run();
        } catch (CafeteriaException e) {
            JOptionPane.showMessageDialog(padre, e.getMessage(), "No se pudo completar", JOptionPane.WARNING_MESSAGE);
        }
        notificarCambio();
    }
}
