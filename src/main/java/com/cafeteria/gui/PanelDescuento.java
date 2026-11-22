package com.cafeteria.gui;

import com.cafeteria.servicio.CafeteriaService;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Fila para elegir y aplicar un descuento al pedido en curso.
 * Solo llama a la fachada: quien calcula el descuento es el núcleo.
 *
 * @author Samuel Chaparro, Cristian Daniel Blanco, Juan Diego Afanador
 */
class PanelDescuento extends JPanel {

    private static final String[] OPCIONES = {
        "Sin descuento", "Cliente frecuente 10%", "Combo 15%", "Código promocional"
    };

    private final SesionGui sesion;
    private final JComboBox<String> tipo = new JComboBox<>(OPCIONES);
    private final JTextField codigo = new JTextField(8);

    PanelDescuento(SesionGui sesion) {
        super(new FlowLayout(FlowLayout.LEFT, 8, 0));
        this.sesion = sesion;
        setOpaque(false);
        Estilo.campo(codigo);

        JButton aplicar = Estilo.boton("Aplicar", Estilo.Tipo.SECUNDARIO);
        aplicar.addActionListener(e -> aplicar());
        add(tipo);
        add(codigo);
        add(aplicar);
    }

    /** Aplica el descuento que esté elegido en la lista. */
    private void aplicar() {
        CafeteriaService servicio = sesion.servicio();
        int opcion = tipo.getSelectedIndex();
        if (opcion == 0) {
            sesion.conPedido(this, id -> servicio.quitarDescuento(id));
        } else if (opcion == 1) {
            sesion.conPedido(this, id -> servicio.aplicarDescuentoClienteFrecuente(id));
        } else if (opcion == 2) {
            sesion.conPedido(this, id -> servicio.aplicarDescuentoCombo(id));
        } else {
            sesion.conPedido(this, id -> servicio.aplicarCodigoPromocional(id, codigo.getText()));
        }
    }
}
