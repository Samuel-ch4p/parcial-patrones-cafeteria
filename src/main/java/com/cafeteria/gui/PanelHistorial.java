package com.cafeteria.gui;

import com.cafeteria.modelo.Moneda;
import com.cafeteria.modelo.TipoEstado;
import com.cafeteria.pedido.Pedido;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

/**
 * Historial de pedidos de la sesión con filtro por estado, ticket y acceso al pedido.
 *
 * @author Samuel Chaparro, Cristian Daniel Blanco, Juan Diego Afanador
 */
class PanelHistorial extends JPanel {

    private static final String TODOS = "Todos";

    private final SesionGui sesion;
    private final Runnable irAPedido;
    private final JComboBox<Object> filtro = new JComboBox<>();
    private final DefaultTableModel modelo = new DefaultTableModel(
            new Object[] {"Pedido", "Cliente", "Estado", "Pago", "Total"}, 0) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;
        }
    };
    private final JTable tabla = new JTable(modelo);
    private List<Pedido> mostrados = new ArrayList<>();

    PanelHistorial(SesionGui sesion, Runnable irAPedido) {
        super(new BorderLayout(6, 6));
        this.sesion = sesion;
        this.irAPedido = irAPedido;
        setBorder(BorderFactory.createTitledBorder("Historial de pedidos"));
        filtro.addItem(TODOS);
        for (TipoEstado estado : TipoEstado.values()) {
            filtro.addItem(estado);
        }
        filtro.addActionListener(e -> refrescar());
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JPanel arriba = new JPanel(new FlowLayout(FlowLayout.LEFT));
        arriba.add(new JLabel("Filtrar por estado:"));
        arriba.add(filtro);
        JPanel abajo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton ticket = new JButton("Ver ticket");
        ticket.addActionListener(e -> conSeleccion(p -> DialogoTicket.mostrar(this, sesion.servicio(), p.id())));
        JButton trabajar = new JButton("Trabajar con este pedido");
        trabajar.addActionListener(e -> conSeleccion(p -> {
            sesion.seleccionar(p.id());
            irAPedido.run();
        }));
        abajo.add(ticket);
        abajo.add(trabajar);

        add(arriba, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
        add(abajo, BorderLayout.SOUTH);
        sesion.alCambiar(this::refrescar);
        refrescar();
    }

    private void refrescar() {
        Object elegido = filtro.getSelectedItem();
        mostrados = elegido instanceof TipoEstado estado
                ? sesion.servicio().listarPedidos(estado)
                : sesion.servicio().listarPedidos();
        modelo.setRowCount(0);
        for (Pedido p : mostrados) {
            modelo.addRow(new Object[] {"#" + p.id(), p.cliente().nombreVisible(), p.tipoEstado(),
                p.estaPagado() ? "Pagado" : "Sin pagar", Moneda.formatear(p.total())});
        }
    }

    private void conSeleccion(java.util.function.Consumer<Pedido> accion) {
        int fila = tabla.getSelectedRow();
        if (fila < 0 || fila >= mostrados.size()) {
            JOptionPane.showMessageDialog(this, "Seleccione un pedido de la tabla.", "Historial",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        accion.accept(mostrados.get(fila));
    }
}
