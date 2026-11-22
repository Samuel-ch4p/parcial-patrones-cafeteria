package com.cafeteria.gui;

import com.cafeteria.pedido.Pedido;
import java.awt.FlowLayout;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * Fila con el estado del pedido y los botones para avanzarlo, cancelarlo o ver su ticket.
 *
 * @author Samuel Chaparro, Cristian Daniel Blanco, Juan Diego Afanador
 */
class PanelEstadoPedido extends JPanel {

    private final SesionGui sesion;
    private final JLabel estado = new JLabel("Estado: -");

    PanelEstadoPedido(SesionGui sesion) {
        super(new FlowLayout(FlowLayout.LEFT, 8, 0));
        this.sesion = sesion;
        setOpaque(false);
        estado.setFont(Estilo.fuente(Font.BOLD, 13f));
        estado.setForeground(Estilo.ACENTO);

        JButton avanzar = Estilo.boton("Avanzar estado", Estilo.Tipo.PRIMARIO);
        avanzar.addActionListener(e -> sesion.conPedido(this, id -> sesion.servicio().avanzarEstado(id)));
        JButton cancelar = Estilo.boton("Cancelar pedido", Estilo.Tipo.PELIGRO);
        cancelar.addActionListener(e -> cancelarPedido());
        JButton ticket = Estilo.boton("Ver ticket", Estilo.Tipo.SECUNDARIO);
        ticket.addActionListener(e -> verTicket());
        add(estado);
        add(avanzar);
        add(cancelar);
        add(ticket);

        sesion.alCambiar(this::refrescar);
        refrescar();
    }

    /** Muestra el estado del pedido en curso. */
    private void refrescar() {
        Pedido pedido = sesion.pedidoActual();
        estado.setText(pedido == null ? "Estado: -" : "Estado: " + pedido.tipoEstado());
    }

    private void cancelarPedido() {
        Pedido pedido = sesion.pedidoActual();
        if (pedido == null) {
            return;
        }
        int respuesta = JOptionPane.showConfirmDialog(this, "¿Cancelar el pedido #" + pedido.id() + "?",
                "Cancelar pedido", JOptionPane.YES_NO_OPTION);
        if (respuesta == JOptionPane.YES_OPTION) {
            sesion.conPedido(this, id -> sesion.servicio().cancelarPedido(id));
        }
    }

    private void verTicket() {
        Pedido pedido = sesion.pedidoActual();
        if (pedido != null) {
            DialogoTicket.mostrar(this, sesion.servicio(), pedido.id());
        }
    }
}
