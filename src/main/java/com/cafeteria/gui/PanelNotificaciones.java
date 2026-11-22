package com.cafeteria.gui;

import com.cafeteria.servicio.CafeteriaService;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

/**
 * Panel siempre visible con las notificaciones de estado y las casillas que
 * activan o desactivan cada receptor en ejecución.
 *
 * @author Samuel Chaparro, Cristian Daniel Blanco, Juan Diego Afanador
 */
class PanelNotificaciones extends JPanel {

    private SesionGui sesion;
    private final JTextArea texto = new JTextArea(4, 60);
    private final JPanel receptores = Estilo.transparente(new FlowLayout(FlowLayout.RIGHT, 10, 0));
    private final Map<String, JCheckBox> casillas = new LinkedHashMap<>();
    private boolean actualizando;

    /**
     * Crea el panel vacío. Hay que llamar a {@link #conectar(SesionGui)} cuando la sesión exista:
     * el panel se crea primero porque la fachada necesita su método {@code agregar} para enviarle avisos.
     */
    PanelNotificaciones() {
        super(new BorderLayout());
        setOpaque(false);

        texto.setEditable(false);
        texto.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        texto.setForeground(Estilo.TEXTO);
        texto.setBackground(Estilo.TARJETA);
        texto.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));

        JPanel encabezado = Estilo.transparente(new BorderLayout());
        encabezado.add(Estilo.titulo("Notificaciones"), BorderLayout.WEST);
        encabezado.add(receptores, BorderLayout.EAST);

        JPanel tarjeta = Estilo.tarjeta();
        tarjeta.add(encabezado, BorderLayout.NORTH);
        tarjeta.add(Estilo.desplazable(texto), BorderLayout.CENTER);
        add(tarjeta, BorderLayout.CENTER);
    }

    /** Une el panel con la sesión para mostrar y activar los receptores de notificaciones. */
    void conectar(SesionGui sesion) {
        this.sesion = sesion;
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
            receptores.add(Estilo.suave("Receptores:"));
            List<String> todos = new ArrayList<>(activos);
            todos.addAll(inactivos);
            for (String nombre : todos) {
                JCheckBox casilla = new JCheckBox(nombre);
                casilla.setOpaque(false);
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
