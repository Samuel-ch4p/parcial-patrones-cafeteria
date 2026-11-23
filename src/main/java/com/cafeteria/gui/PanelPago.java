package com.cafeteria.gui;

import com.cafeteria.modelo.Moneda;
import com.cafeteria.pago.ResultadoPago;
import com.cafeteria.servicio.CafeteriaService;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Fila para pagar el pedido en curso con efectivo, tarjeta o billetera digital.
 * La validación de cada método la hace el núcleo; aquí solo se recoge el dato y se muestra el resultado.
 *
 * @author Samuel Chaparro, Cristian Daniel Blanco, Juan Diego Afanador
 */
class PanelPago extends JPanel {

    private static final String[] METODOS = {"Efectivo", "Tarjeta", "Billetera digital"};
    /** Lo que se le pide al usuario según el método elegido (mismo orden que METODOS). */
    private static final String[] DATOS_PEDIDOS = {"Monto recibido", "N.º tarjeta", "Identificador"};

    private final SesionGui sesion;
    private final JComboBox<String> metodo = new JComboBox<>(METODOS);
    private final JLabel etiquetaDato = Estilo.suave(DATOS_PEDIDOS[0]);
    private final JTextField dato = new JTextField(10);

    PanelPago(SesionGui sesion) {
        super(new FlowLayout(FlowLayout.LEFT, 8, 0));
        this.sesion = sesion;
        setOpaque(false);
        Estilo.campo(dato);

        JButton pagar = Estilo.boton("Pagar", Estilo.Tipo.PRIMARIO);
        pagar.addActionListener(e -> pagar());
        metodo.addActionListener(e -> etiquetaDato.setText(DATOS_PEDIDOS[metodo.getSelectedIndex()]));
        add(metodo);
        add(etiquetaDato);
        add(dato);
        add(pagar);
    }

    private void pagar() {
        CafeteriaService servicio = sesion.servicio();
        sesion.conPedido(this, id -> {
            String texto = dato.getText();
            int elegido = metodo.getSelectedIndex();
            ResultadoPago resultado;
            if (elegido == 0) {
                resultado = servicio.pagarEfectivo(id, montoDe(texto));
            } else if (elegido == 1) {
                resultado = servicio.pagarTarjeta(id, texto);
            } else {
                resultado = servicio.pagarBilletera(id, texto);
            }
            dato.setText("");
            JOptionPane.showMessageDialog(this, "Pago aprobado: " + resultado.detalle(), "Pago",
                    JOptionPane.INFORMATION_MESSAGE);
        });
    }

    /** Convierte el texto del monto con la misma regla que la consola; si es inválido, la sesión muestra el error. */
    private static long montoDe(String texto) {
        return Moneda.parsear(texto);
    }
}
