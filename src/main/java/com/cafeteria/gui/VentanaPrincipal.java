package com.cafeteria.gui;

import com.cafeteria.servicio.CafeteriaService;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Toolkit;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

/**
 * Ventana principal. Arriba el título y la navegación (Pedido / Historial), en el centro
 * la pantalla elegida y, siempre visible abajo, el panel de notificaciones.
 *
 * @author Samuel Chaparro, Cristian Daniel Blanco, Juan Diego Afanador
 */
public class VentanaPrincipal extends JFrame {

    private static final String PEDIDO = "pedido";
    private static final String HISTORIAL = "historial";

    private final CardLayout pantallas = new CardLayout();
    private final JPanel centro = new JPanel(pantallas);
    private final JButton botonPedido = Estilo.boton("Pedido", Estilo.Tipo.NAVEGACION);
    private final JButton botonHistorial = Estilo.boton("Historial", Estilo.Tipo.NAVEGACION);

    public VentanaPrincipal() {
        super("Sistema de Cafetería");

        // 1) El panel de notificaciones se crea primero, porque la fachada le envía los avisos.
        // 2) Con él se crean la fachada y la sesión.
        // 3) Al final se conecta el panel con la sesión.
        PanelNotificaciones notificaciones = new PanelNotificaciones();
        SesionGui sesion = new SesionGui(new CafeteriaService(notificaciones::agregar));
        notificaciones.conectar(sesion);

        centro.setOpaque(false);
        centro.add(armarPantallaPedido(sesion), PEDIDO);
        centro.add(new PanelHistorial(sesion, () -> mostrar(PEDIDO)), HISTORIAL);
        botonPedido.addActionListener(e -> mostrar(PEDIDO));
        botonHistorial.addActionListener(e -> mostrar(HISTORIAL));
        mostrar(PEDIDO);

        JPanel contenido = new JPanel(new BorderLayout(0, 14));
        contenido.setBackground(Estilo.FONDO);
        contenido.setBorder(new EmptyBorder(14, 18, 18, 18));
        contenido.add(armarEncabezado(), BorderLayout.NORTH);
        contenido.add(centro, BorderLayout.CENTER);
        contenido.add(notificaciones, BorderLayout.SOUTH);
        setContentPane(contenido);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        // Alta para que quepa la lista del pedido, pero sin pasarse del tamaño de la pantalla.
        int altoPantalla = Toolkit.getDefaultToolkit().getScreenSize().height;
        setMinimumSize(new Dimension(1000, 640));
        setPreferredSize(new Dimension(1240, Math.min(920, altoPantalla - 60)));
        pack();
        setLocationRelativeTo(null);
    }

    private JPanel armarEncabezado() {
        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setOpaque(false);
        JPanel marca = Estilo.transparente(new BorderLayout(0, 2));
        JLabel nombre = Estilo.titulo("Cafetería");
        nombre.setFont(Estilo.fuente(Font.BOLD, 24f));
        nombre.setForeground(Estilo.ACENTO);
        marca.add(nombre, BorderLayout.NORTH);
        marca.add(Estilo.suave("Pedidos, pagos y notificaciones"), BorderLayout.SOUTH);
        JPanel navegacion = Estilo.transparente(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        navegacion.add(botonPedido);
        navegacion.add(botonHistorial);
        encabezado.add(marca, BorderLayout.WEST);
        encabezado.add(navegacion, BorderLayout.EAST);
        return encabezado;
    }

    /** Catálogo a la izquierda y pedido en curso a la derecha, con un poco más de espacio para el pedido. */
    private JPanel armarPantallaPedido(SesionGui sesion) {
        JPanel pantalla = Estilo.transparente(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 1;
        c.gridy = 0;
        c.gridx = 0;
        c.weightx = 0.44;
        c.insets = new Insets(0, 0, 0, 7);
        pantalla.add(new PanelCatalogo(sesion), c);
        c.gridx = 1;
        c.weightx = 0.56;
        c.insets = new Insets(0, 7, 0, 0);
        pantalla.add(new PanelCarrito(sesion), c);
        return pantalla;
    }

    /** Muestra una pantalla y marca su botón de navegación como activo. */
    private void mostrar(String nombre) {
        pantallas.show(centro, nombre);
        botonPedido.setSelected(PEDIDO.equals(nombre));
        botonHistorial.setSelected(HISTORIAL.equals(nombre));
        botonPedido.repaint();
        botonHistorial.repaint();
    }
}
