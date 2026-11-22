package com.cafeteria.gui;

import javax.swing.SwingUtilities;

/**
 * Arranque de la interfaz gráfica Swing.
 *
 * @author Samuel Chaparro, Cristian Daniel Blanco, Juan Diego Afanador
 */
public final class AplicacionGui {

    private AplicacionGui() {
    }

    public static void iniciar() {
        SwingUtilities.invokeLater(() -> {
            Estilo.instalar();
            new VentanaPrincipal().setVisible(true);
        });
    }
}
