package com.cafeteria.gui;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

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
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                // Si falla, se usa el aspecto por defecto de Swing.
            }
            new VentanaPrincipal().setVisible(true);
        });
    }
}
