package com.cafeteria.gui;

import com.cafeteria.modelo.CafeteriaException;
import com.cafeteria.servicio.CafeteriaService;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.FlowLayout;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * Ventana emergente con el ticket de un pedido y un selector de formato.
 *
 * @author Samuel Chaparro, Cristian Daniel Blanco, Juan Diego Afanador
 */
final class DialogoTicket {

    private DialogoTicket() {
    }

    static void mostrar(Component padre, CafeteriaService servicio, int pedidoId) {
        List<String> formatos = servicio.formatosTicket();
        JComboBox<String> selector = new JComboBox<>(formatos.toArray(new String[0]));
        JTextArea texto = new JTextArea(14, 48);
        texto.setEditable(false);
        texto.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        Runnable actualizar = () -> {
            try {
                texto.setText(servicio.generarTicket(pedidoId, (String) selector.getSelectedItem()));
            } catch (CafeteriaException e) {
                texto.setText(e.getMessage());
            }
            texto.setCaretPosition(0);
        };
        selector.addActionListener(e -> actualizar.run());
        actualizar.run();

        JPanel arriba = new JPanel(new FlowLayout(FlowLayout.LEFT));
        arriba.add(new JLabel("Formato:"));
        arriba.add(selector);
        JPanel contenido = new JPanel(new BorderLayout(4, 4));
        contenido.add(arriba, BorderLayout.NORTH);
        contenido.add(new JScrollPane(texto), BorderLayout.CENTER);
        JOptionPane.showMessageDialog(padre, contenido, "Ticket del pedido #" + pedidoId, JOptionPane.PLAIN_MESSAGE);
    }
}
