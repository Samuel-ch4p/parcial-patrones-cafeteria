package com.cafeteria.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

/**
 * Colores, fuentes y piezas visuales reutilizables de la interfaz gráfica.
 * Todo el aspecto de la ventana se cambia desde aquí: si se modifica un color,
 * se actualizan todos los paneles. No contiene reglas de negocio.
 *
 * @author Samuel Chaparro, Cristian Daniel Blanco, Juan Diego Afanador
 */
final class Estilo {

    // Paleta: neutros cálidos y un solo color de acento (café).
    static final Color FONDO = new Color(0xF5F3F0);
    static final Color TARJETA = Color.WHITE;
    static final Color BORDE = new Color(0xE3DFD9);
    static final Color TEXTO = new Color(0x2A2420);
    static final Color TEXTO_SUAVE = new Color(0x6E655D);
    static final Color ACENTO = new Color(0x6B4A33);
    static final Color ACENTO_OSCURO = new Color(0x4E3524);
    static final Color ACENTO_SUAVE = new Color(0xF0E7DF);
    static final Color PELIGRO = new Color(0xB3402F);
    static final Color PELIGRO_SUAVE = new Color(0xFBEDEA);

    /** Tipos de botón: el principal llama la atención, el secundario acompaña. */
    enum Tipo { PRIMARIO, SECUNDARIO, PELIGRO, NAVEGACION }

    private Estilo() {
    }

    /** Elige una fuente moderna si está instalada; si no, la genérica de Java. */
    private static String familia() {
        Set<String> instaladas = Set.of(
                GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames());
        for (String candidata : new String[] {"Segoe UI", "Inter", "Roboto"}) {
            if (instaladas.contains(candidata)) {
                return candidata;
            }
        }
        return Font.SANS_SERIF;
    }

    static Font fuente(int estilo, float tamano) {
        return new Font(familia(), estilo, 13).deriveFont(estilo, tamano);
    }

    /** Se llama una sola vez, antes de crear la ventana. */
    static void instalar() {
        // Estas claves de Nimbus se fijan antes de activarlo para que tomen efecto.
        UIManager.put("control", FONDO);
        UIManager.put("nimbusBase", ACENTO);
        UIManager.put("nimbusBlueGrey", new Color(0xCFC8C0));
        UIManager.put("nimbusFocus", ACENTO);
        UIManager.put("nimbusSelectionBackground", ACENTO);
        UIManager.put("defaultFont", fuente(Font.PLAIN, 13f));
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                | UnsupportedLookAndFeelException e) {
            // Si falla, se usa el aspecto por defecto de Swing.
        }
    }

    // ---------- Etiquetas ----------

    static JLabel titulo(String texto) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setFont(fuente(Font.BOLD, 16f));
        etiqueta.setForeground(TEXTO);
        return etiqueta;
    }

    static JLabel suave(String texto) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setFont(fuente(Font.PLAIN, 12f));
        etiqueta.setForeground(TEXTO_SUAVE);
        return etiqueta;
    }

    // ---------- Contenedores ----------

    /** Panel blanco con borde fino y espacio interior, como una tarjeta. */
    static JPanel tarjeta() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(TARJETA);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDE, 1, true), new EmptyBorder(14, 16, 14, 16)));
        return panel;
    }

    /** Una etiqueta pequeña encima de un componente (por ejemplo, de un combo). */
    static JPanel conEtiqueta(String texto, JComponent componente) {
        JPanel panel = new JPanel(new BorderLayout(0, 4));
        panel.setOpaque(false);
        panel.add(suave(texto), BorderLayout.NORTH);
        panel.add(componente, BorderLayout.CENTER);
        return panel;
    }

    static JPanel transparente(java.awt.LayoutManager disposicion) {
        JPanel panel = new JPanel(disposicion);
        panel.setOpaque(false);
        return panel;
    }

    // ---------- Campos y tablas ----------

    static void campo(JTextField campo) {
        campo.setFont(fuente(Font.PLAIN, 13f));
        campo.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDE, 1, true), new EmptyBorder(5, 8, 5, 8)));
    }

    static JScrollPane desplazable(JComponent contenido) {
        JScrollPane scroll = new JScrollPane(contenido);
        scroll.setBorder(new LineBorder(BORDE, 1, true));
        scroll.getViewport().setBackground(TARJETA);
        return scroll;
    }

    /**
     * Da a una tabla el aspecto del resto de la ventana.
     *
     * @param columnasADerecha índices de las columnas que se alinean a la derecha (precios, totales)
     */
    static void tabla(JTable tabla, int... columnasADerecha) {
        tabla.setRowHeight(30);
        tabla.setShowGrid(false);
        tabla.setShowHorizontalLines(true);
        tabla.setGridColor(new Color(0xEFECE8));
        tabla.setIntercellSpacing(new Dimension(0, 1));
        tabla.setFillsViewportHeight(true);
        tabla.setBackground(TARJETA);
        tabla.setForeground(TEXTO);
        tabla.setSelectionBackground(ACENTO_SUAVE);
        tabla.setSelectionForeground(TEXTO);
        tabla.setFont(fuente(Font.PLAIN, 13f));

        DefaultTableCellRenderer celdas = new DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable t, Object valor,
                    boolean seleccionada, boolean enfoque, int fila, int columna) {
                super.getTableCellRendererComponent(t, valor, seleccionada, false, fila, columna);
                setBorder(new EmptyBorder(0, 10, 0, 10));
                setBackground(seleccionada ? ACENTO_SUAVE : TARJETA);
                setForeground(TEXTO);
                setHorizontalAlignment(esDerecha(columna, columnasADerecha)
                        ? SwingConstants.RIGHT : SwingConstants.LEFT);
                return this;
            }
        };
        tabla.setDefaultRenderer(Object.class, celdas);
        tabla.setDefaultRenderer(Number.class, celdas);

        JTableHeader encabezado = tabla.getTableHeader();
        encabezado.setReorderingAllowed(false);
        encabezado.setPreferredSize(new Dimension(0, 32));
        encabezado.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable t, Object valor,
                    boolean seleccionada, boolean enfoque, int fila, int columna) {
                super.getTableCellRendererComponent(t, valor, false, false, fila, columna);
                setFont(fuente(Font.BOLD, 12f));
                setForeground(TEXTO_SUAVE);
                setBackground(FONDO);
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, BORDE), new EmptyBorder(0, 10, 0, 10)));
                setHorizontalAlignment(esDerecha(columna, columnasADerecha)
                        ? SwingConstants.RIGHT : SwingConstants.LEFT);
                return this;
            }
        });
    }

    private static boolean esDerecha(int columna, int[] columnasADerecha) {
        for (int c : columnasADerecha) {
            if (c == columna) {
                return true;
            }
        }
        return false;
    }

    // ---------- Botones ----------

    static JButton boton(String texto, Tipo tipo) {
        return new BotonEstilo(texto, tipo);
    }

    /** Botón plano con esquinas redondeadas que cambia de color al pasar el mouse. */
    private static final class BotonEstilo extends JButton {

        private final Tipo tipo;

        BotonEstilo(String texto, Tipo tipo) {
            super(texto);
            this.tipo = tipo;
            setContentAreaFilled(false);
            setFocusPainted(false);
            setOpaque(false);
            setBorder(new EmptyBorder(8, 16, 8, 16));
            setFont(fuente(Font.BOLD, 13f));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            boolean encima = getModel().isRollover() || getModel().isPressed();
            Color relleno;
            Color borde;
            Color texto;
            switch (tipo) {
                case PRIMARIO -> {
                    relleno = encima ? ACENTO_OSCURO : ACENTO;
                    borde = relleno;
                    texto = Color.WHITE;
                }
                case PELIGRO -> {
                    relleno = encima ? PELIGRO_SUAVE : TARJETA;
                    borde = PELIGRO;
                    texto = PELIGRO;
                }
                case NAVEGACION -> {
                    relleno = encima ? ACENTO_SUAVE : TARJETA;
                    borde = relleno;
                    texto = isSelected() ? ACENTO : TEXTO_SUAVE;
                }
                default -> {
                    relleno = encima ? ACENTO_SUAVE : TARJETA;
                    borde = BORDE;
                    texto = TEXTO;
                }
            }
            if (!isEnabled()) {
                relleno = new Color(0xEDEAE6);
                borde = relleno;
                texto = new Color(0xA39B93);
            }
            g2.setColor(relleno);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
            g2.setColor(borde);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
            g2.dispose();
            setForeground(texto);
            super.paintComponent(g);
        }
    }
}
