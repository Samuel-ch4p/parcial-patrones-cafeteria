package com.cafeteria.gui;

import com.cafeteria.extras.OpcionPersonalizacion;
import com.cafeteria.extras.TipoOpcion;
import com.cafeteria.modelo.CafeteriaException;
import com.cafeteria.modelo.Moneda;
import com.cafeteria.producto.Producto;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
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
    private final JPanel personalizacion = Estilo.transparente(new GridLayout(0, 1, 0, 8));
    private final JButton agregar = Estilo.boton("Agregar al pedido", Estilo.Tipo.PRIMARIO);

    PanelCatalogo(SesionGui sesion) {
        super(new BorderLayout());
        this.sesion = sesion;
        this.productos = sesion.servicio().catalogo();
        setOpaque(false);

        JPanel tarjeta = Estilo.tarjeta();
        tarjeta.add(Estilo.titulo("Menú"), BorderLayout.NORTH);

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
        Estilo.tabla(tabla, 2);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                alSeleccionar();
            }
        });

        armarPersonalizacion();
        detalle.setFont(Estilo.fuente(Font.PLAIN, 13f));
        detalle.setForeground(Estilo.TEXTO);
        vistaPrevia.setFont(Estilo.fuente(Font.BOLD, 13f));
        vistaPrevia.setForeground(Estilo.ACENTO);

        JPanel pie = Estilo.transparente(new BorderLayout(10, 0));
        JPanel cantidadConEtiqueta = Estilo.conEtiqueta("Cantidad", cantidad);
        pie.add(cantidadConEtiqueta, BorderLayout.WEST);
        pie.add(agregar, BorderLayout.EAST);

        JPanel abajo = Estilo.transparente(new BorderLayout(0, 10));
        JPanel informacion = Estilo.transparente(new GridLayout(0, 1, 0, 4));
        informacion.add(detalle);
        informacion.add(vistaPrevia);
        abajo.add(informacion, BorderLayout.NORTH);
        abajo.add(personalizacion, BorderLayout.CENTER);
        abajo.add(pie, BorderLayout.SOUTH);

        tarjeta.add(Estilo.desplazable(tabla), BorderLayout.CENTER);
        tarjeta.add(abajo, BorderLayout.SOUTH);
        add(tarjeta, BorderLayout.CENTER);

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
        JPanel filaCombos = Estilo.transparente(new GridLayout(1, 2, 10, 0));
        filaCombos.add(Estilo.conEtiqueta("Tamaño", tamano));
        filaCombos.add(Estilo.conEtiqueta("Leche", leche));

        JPanel casillas = Estilo.transparente(new GridLayout(0, 2, 10, 2));
        for (OpcionPersonalizacion o : sesion.servicio().opciones(TipoOpcion.EXTRA)) {
            JCheckBox casilla = new JCheckBox(o.nombre() + " (+" + Moneda.formatear(o.recargo()) + ")");
            casilla.setOpaque(false);
            casilla.addActionListener(e -> actualizarVistaPrevia());
            extras.add(casilla);
            codigosExtras.add(o.codigo());
            casillas.add(casilla);
        }
        personalizacion.add(filaCombos);
        personalizacion.add(Estilo.conEtiqueta("Extras", casillas));
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
        detalle.setText("<html><body style='width:330px'><b>" + p.nombre() + "</b>: " + p.detalle() + "</body></html>");
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

    /** Deja tamaño, leche y extras sin elegir para que no pasen al siguiente producto. */
    private void reiniciarPersonalizacion() {
        tamano.setSelectedIndex(0);
        leche.setSelectedIndex(0);
        extras.forEach(c -> c.setSelected(false));
        actualizarVistaPrevia();
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
            vistaPrevia.setText("<html><body style='width:330px'>Quedará: " + resultado.descripcion()
                    + " — " + Moneda.formatear(resultado.precio()) + " c/u</body></html>");
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
            reiniciarPersonalizacion();
        });
    }
}
