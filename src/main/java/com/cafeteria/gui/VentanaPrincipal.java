package com.cafeteria.gui;

import com.cafeteria.servicio.CafeteriaService;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;

/**
 * Ventana principal. Pestaña "Pedido" (catálogo + carrito), pestaña "Historial" y,
 * siempre visible abajo, el panel de notificaciones.
 *
 * @author Samuel Chaparro, Cristian Daniel Blanco, Juan Diego Afanador
 */
public class VentanaPrincipal extends JFrame {

    public VentanaPrincipal() {
        super("Sistema de Cafetería");

        // La fachada necesita una salida para las notificaciones y el panel necesita la
        // sesión (que usa la fachada): la referencia rompe ese círculo de creación.
        AtomicReference<PanelNotificaciones> panel = new AtomicReference<>();
        Consumer<String> salida = mensaje -> {
            PanelNotificaciones actual = panel.get();
            if (actual != null) {
                actual.agregar(mensaje);
            }
        };
        SesionGui sesion = new SesionGui(new CafeteriaService(salida));
        PanelNotificaciones notificaciones = new PanelNotificaciones(sesion);
        panel.set(notificaciones);

        JTabbedPane pestanas = new JTabbedPane();
        JSplitPane pedido = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new PanelCatalogo(sesion), new PanelCarrito(sesion));
        pedido.setResizeWeight(0.42);
        pestanas.addTab("Pedido", pedido);
        pestanas.addTab("Historial", new PanelHistorial(sesion, () -> pestanas.setSelectedIndex(0)));

        JPanel contenido = new JPanel(new BorderLayout(6, 6));
        contenido.add(pestanas, BorderLayout.CENTER);
        contenido.add(notificaciones, BorderLayout.SOUTH);
        setContentPane(contenido);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(1280, 760));
        pack();
        setLocationRelativeTo(null);
    }
}
