package com.cafeteria.gui;

import com.cafeteria.servicio.CafeteriaService;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

/**
 * Panel siempre visible con las notificaciones de estado y las casillas que
 * activan o desactivan cada receptor en ejecución.
 *
 * @author Samuel Chaparro, Cristian Daniel Blanco, Juan Diego Afanador
 */
class PanelNotificaciones extends JPanel {

    private final SesionGui sesion;
    private final JTextArea texto = new JTextArea(6, 60);
    private final JPanel receptores = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
    private final Map<String, JCheckBox> casillas = new LinkedHashMap<>();
    private boolean actualizando;

    PanelNotificaciones(SesionGui sesion) {
        super(new BorderLayout(4, 4));
        this.sesion = sesion;
        setBorder(BorderFactory.createTitledBorder("Notificaciones"));
        texto.setEditable(false);
        texto.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        add(receptores, BorderLayout.NORTH);
        add(new JScrollPane(texto), BorderLayout.CENTER);
        sesion.alCambiar(this::refrescarReceptores);
        refrescarReceptores();
    }

    /** Agrega un mensaje al panel; se puede llamar desde cualquier hilo. */
    void agregar(String mensaje) {
        SwingUtilities.invokeLater(() -> {
            texto.append(mensaje + "\n");
            texto.setCaretPosition(texto.getDocument().getLength());
        });
    }

    private void refrescarReceptores() {
        CafeteriaService servicio = sesion.servicio();
        List<String> activos = servicio.receptoresActivos();
        List<String> inactivos = servicio.receptoresInactivos();
        actualizando = true;
        if (casillas.isEmpty()) {
            receptores.add(new javax.swing.JLabel("Receptores:"));
            List<String> todos = new java.util.ArrayList<>(activos);
            todos.addAll(inactivos);
            for (String nombre : todos) {
                JCheckBox casilla = new JCheckBox(nombre);
                casilla.addActionListener(e -> alternar(nombre, casilla));
                casillas.put(nombre, casilla);
                receptores.add(casilla);
            }
        }
        casillas.forEach((nombre, casilla) -> casilla.setSelected(activos.contains(nombre)));
        actualizando = false;
        receptores.revalidate();
    }

    private void alternar(String nombre, JCheckBox casilla) {
        if (actualizando) {
            return;
        }
        sesion.ejecutar(this, () -> {
            if (casilla.isSelected()) {
                sesion.servicio().agregarReceptor(nombre);
            } else {
                sesion.servicio().quitarReceptor(nombre);
            }
        });
    }
}
