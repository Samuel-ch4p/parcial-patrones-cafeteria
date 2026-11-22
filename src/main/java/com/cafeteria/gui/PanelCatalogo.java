package com.cafeteria.gui;

import com.cafeteria.extras.OpcionPersonalizacion;
import com.cafeteria.extras.TipoOpcion;
import com.cafeteria.modelo.CafeteriaException;
import com.cafeteria.modelo.Moneda;
import com.cafeteria.producto.Producto;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;

/**
 * Catálogo con detalle, personalización de bebidas y botón para agregar al pedido.
 *
 * @author Samuel Chaparro, Cristian Daniel Blanco, Juan Diego Afanador
 */
class PanelCatalogo extends JPanel {

    /** Opción de un combo: el código que entiende la fachada y el texto visible. */
    private record Item(String codigo, String texto) {
        @Override
        public String toString() {
            return texto;
        }
    }

    private final SesionGui sesion;
    private final List<Producto> productos;
    private final JTable tabla;
    private final JLabel detalle = new JLabel(" ");
    private final JComboBox<Item> tamano = new JComboBox<>();
    private final JComboBox<Item> leche = new JComboBox<>();
    private final List<JCheckBox> extras = new ArrayList<>();
    private final List<String> codigosExtras = new ArrayList<>();
    private final JSpinner cantidad = new JSpinner(new SpinnerNumberModel(1, 1, 99, 1));
    private final JLabel vistaPrevia = new JLabel(" ");
    private final JPanel personalizacion = new JPanel(new GridLayout(0, 1, 2, 2));
    private final JButton agregar = new JButton("Agregar al pedido");

    PanelCatalogo(SesionGui sesion) {
        super(new BorderLayout(6, 6));
        this.sesion = sesion;
        this.productos = sesion.servicio().catalogo();
        setBorder(BorderFactory.createTitledBorder("1. Catálogo y personalización"));

        DefaultTableModel modelo = new DefaultTableModel(new Object[] {"Producto", "Categoría", "Precio base"}, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        for (Producto p : productos) {
            modelo.addRow(new Object[] {p.nombre(), p.categoria().etiqueta(), Moneda.formatear(p.precioBase())});
        }
        tabla = new JTable(modelo);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                alSeleccionar();
            }
        });

        armarPersonalizacion();
        JPanel abajo = new JPanel(new BorderLayout(4, 4));
        abajo.add(detalle, BorderLayout.NORTH);
        abajo.add(personalizacion, BorderLayout.CENTER);
        JPanel pie = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pie.add(new JLabel("Cantidad:"));
        pie.add(cantidad);
        pie.add(agregar);
        JPanel sur = new JPanel(new GridLayout(0, 1));
        sur.add(vistaPrevia);
        sur.add(pie);
        abajo.add(sur, BorderLayout.SOUTH);

        add(new JScrollPane(tabla), BorderLayout.CENTER);
        add(abajo, BorderLayout.SOUTH);

        agregar.addActionListener(e -> agregarAlPedido());
        agregar.setEnabled(false);
        tamano.addActionListener(e -> actualizarVistaPrevia());
        leche.addActionListener(e -> actualizarVistaPrevia());
        setPersonalizable(false);
    }

    private void armarPersonalizacion() {
        tamano.addItem(new Item(null, "Sin cambio de tamaño"));
        for (OpcionPersonalizacion o : sesion.servicio().opciones(TipoOpcion.TAMANO)) {
            tamano.addItem(new Item(o.codigo(), o.nombre() + " (+" + Moneda.formatear(o.recargo()) + ")"));
        }
        leche.addItem(new Item(null, "Sin cambio de leche"));
        for (OpcionPersonalizacion o : sesion.servicio().opciones(TipoOpcion.LECHE)) {
            leche.addItem(new Item(o.codigo(), o.nombre() + " (+" + Moneda.formatear(o.recargo()) + ")"));
        }
        JPanel filaTamano = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filaTamano.add(new JLabel("Tamaño:"));
        filaTamano.add(tamano);
        filaTamano.add(new JLabel("Leche:"));
        filaTamano.add(leche);
        JPanel filaExtras = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filaExtras.add(new JLabel("Extras:"));
        for (OpcionPersonalizacion o : sesion.servicio().opciones(TipoOpcion.EXTRA)) {
            JCheckBox casilla = new JCheckBox(o.nombre() + " (+" + Moneda.formatear(o.recargo()) + ")");
            casilla.addActionListener(e -> actualizarVistaPrevia());
            extras.add(casilla);
            codigosExtras.add(o.codigo());
            filaExtras.add(casilla);
        }
        personalizacion.add(filaTamano);
        personalizacion.add(filaExtras);
    }

    private Producto seleccionado() {
        int fila = tabla.getSelectedRow();
        return fila < 0 ? null : productos.get(fila);
    }

    private void alSeleccionar() {
        Producto p = seleccionado();
        agregar.setEnabled(p != null);
        if (p == null) {
            detalle.setText(" ");
            return;
        }
        detalle.setText("<html><b>" + p.nombre() + "</b>: " + p.detalle() + "</html>");
        setPersonalizable(p.categoria().esBebida());
        actualizarVistaPrevia();
    }

    private void setPersonalizable(boolean activo) {
        tamano.setEnabled(activo);
        leche.setEnabled(activo);
        extras.forEach(c -> c.setEnabled(activo));
        if (!activo) {
            tamano.setSelectedIndex(0);
            leche.setSelectedIndex(0);
            extras.forEach(c -> c.setSelected(false));
        }
    }

    private List<String> extrasElegidos() {
        List<String> elegidos = new ArrayList<>();
        for (int i = 0; i < extras.size(); i++) {
            if (extras.get(i).isSelected()) {
                elegidos.add(codigosExtras.get(i));
            }
        }
        return elegidos;
    }

    private String codigo(JComboBox<Item> combo) {
        return ((Item) combo.getSelectedItem()).codigo();
    }

    private void actualizarVistaPrevia() {
        Producto p = seleccionado();
        if (p == null) {
            vistaPrevia.setText(" ");
            return;
        }
        try {
            Producto resultado = sesion.servicio().previsualizar(p.codigo(), codigo(tamano), codigo(leche), extrasElegidos());
            vistaPrevia.setText("Quedará: " + resultado.descripcion() + " — " + Moneda.formatear(resultado.precio()) + " c/u");
        } catch (CafeteriaException e) {
            vistaPrevia.setText(e.getMessage());
        }
    }

    private void agregarAlPedido() {
        Producto p = seleccionado();
        if (p == null) {
            return;
        }
        sesion.ejecutar(this, () -> {
            int pedidoId = sesion.pedidoOCrear().id();
            sesion.servicio().agregarItem(pedidoId, p.codigo(), (Integer) cantidad.getValue(),
                    codigo(tamano), codigo(leche), extrasElegidos());
            cantidad.setValue(1);
        });
    }
}
