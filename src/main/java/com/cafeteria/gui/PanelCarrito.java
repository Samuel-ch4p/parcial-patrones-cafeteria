package com.cafeteria.gui;

import com.cafeteria.modelo.Moneda;
import com.cafeteria.pedido.LineaPedido;
import com.cafeteria.pedido.Pedido;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

/**
 * Pedido en curso: cliente, lista de ítems y totales. Debajo reúne los paneles de
 * descuento, pago y estado, cada uno en su propia clase.
 *
 * @author Samuel Chaparro, Cristian Daniel Blanco, Juan Diego Afanador
 */
class PanelCarrito extends JPanel {

    private static final String SIN_PEDIDO = "Sin pedido. Cree uno o agregue un producto.";

    private final SesionGui sesion;
    private final JTextField cliente = new JTextField(12);
    private final JCheckBox frecuente = new JCheckBox("Cliente frecuente");
    private final JLabel encabezado = new JLabel(SIN_PEDIDO);
    private final DefaultTableModel modelo = new DefaultTableModel(
            new Object[] {"#", "Producto", "Cant.", "Precio c/u", "Subtotal"}, 0) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;
        }
    };
    private final JTable tabla = new JTable(modelo);
    private final JLabel subtotal = new JLabel("Subtotal: $0");
    private final JLabel descuento = new JLabel("Descuento: $0");
    private final JLabel total = new JLabel("TOTAL: $0");

    PanelCarrito(SesionGui sesion) {
        super(new BorderLayout());
        this.sesion = sesion;
        setOpaque(false);
        darEstilo();

        JPanel tarjeta = Estilo.tarjeta();
        tarjeta.add(armarParteSuperior(), BorderLayout.NORTH);
        JComponent listado = Estilo.desplazable(tabla);
        listado.setPreferredSize(new Dimension(400, 150)); // altura mínima para que se vea la lista
        tarjeta.add(listado, BorderLayout.CENTER);
        tarjeta.add(armarParteInferior(), BorderLayout.SOUTH);
        add(tarjeta, BorderLayout.CENTER);

        sesion.alCambiar(this::refrescar);
        refrescar();
    }

    /** Colores, fuentes y anchos de las piezas de este panel. */
    private void darEstilo() {
        Estilo.tabla(tabla, 3, 4);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getColumnModel().getColumn(0).setMaxWidth(40);
        tabla.getColumnModel().getColumn(2).setMaxWidth(60);
        Estilo.campo(cliente);
        frecuente.setOpaque(false);
        encabezado.setFont(Estilo.fuente(Font.BOLD, 13f));
        encabezado.setForeground(Estilo.TEXTO_SUAVE);
        subtotal.setForeground(Estilo.TEXTO_SUAVE);
        descuento.setForeground(Estilo.TEXTO_SUAVE);
        total.setFont(Estilo.fuente(Font.BOLD, 26f));
        total.setForeground(Estilo.ACENTO);
    }

    /** Título, datos del cliente y el botón para crear un pedido nuevo. */
    private JPanel armarParteSuperior() {
        JButton botonNuevo = Estilo.boton("Nuevo pedido", Estilo.Tipo.SECUNDARIO);
        botonNuevo.addActionListener(e -> nuevoPedido());
        JPanel fila = Estilo.transparente(new FlowLayout(FlowLayout.LEFT, 8, 0));
        fila.add(cliente);
        fila.add(frecuente);
        fila.add(botonNuevo);

        JPanel superior = Estilo.transparente(null);
        superior.setLayout(new BoxLayout(superior, BoxLayout.Y_AXIS));
        agregarFila(superior, Estilo.titulo("Tu pedido"));
        agregarFila(superior, Estilo.conEtiqueta("Cliente (opcional)", fila));
        agregarFila(superior, encabezado);
        return superior;
    }

    /** Botones de ítems, totales y las filas de descuento, pago y estado. */
    private JPanel armarParteInferior() {
        JPanel inferior = Estilo.transparente(null);
        inferior.setLayout(new BoxLayout(inferior, BoxLayout.Y_AXIS));
        agregarFila(inferior, armarItemsYTotales());
        agregarFila(inferior, Estilo.conEtiqueta("Descuento", new PanelDescuento(sesion)));
        agregarFila(inferior, Estilo.conEtiqueta("Pago", new PanelPago(sesion)));
        agregarFila(inferior, Estilo.conEtiqueta("Estado del pedido", new PanelEstadoPedido(sesion)));
        return inferior;
    }

    private JPanel armarItemsYTotales() {
        JButton quitar = Estilo.boton("Quitar ítem", Estilo.Tipo.SECUNDARIO);
        quitar.addActionListener(e -> quitarItem());
        JButton cambiar = Estilo.boton("Cambiar cantidad", Estilo.Tipo.SECUNDARIO);
        cambiar.addActionListener(e -> cambiarCantidad());
        JPanel botones = Estilo.transparente(new FlowLayout(FlowLayout.LEFT, 8, 0));
        botones.add(quitar);
        botones.add(cambiar);

        JPanel totales = Estilo.transparente(new GridLayout(0, 1, 0, 2));
        for (JLabel etiqueta : new JLabel[] {subtotal, descuento, total}) {
            etiqueta.setHorizontalAlignment(SwingConstants.RIGHT);
            totales.add(etiqueta);
        }

        JPanel fila = Estilo.transparente(new BorderLayout(10, 0));
        fila.add(botones, BorderLayout.WEST);
        fila.add(totales, BorderLayout.EAST);
        return fila;
    }

    /** Agrega una fila al bloque inferior, con un poco de aire arriba. */
    private static void agregarFila(JPanel contenedor, JComponent fila) {
        fila.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0));
        fila.setAlignmentX(LEFT_ALIGNMENT);
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, fila.getPreferredSize().height));
        contenedor.add(fila);
    }

    /** Vuelve a mostrar el pedido en curso: la tabla, el encabezado y los totales. */
    private void refrescar() {
        modelo.setRowCount(0);
        Pedido pedido = sesion.pedidoActual();
        if (pedido == null) {
            encabezado.setText(SIN_PEDIDO);
            subtotal.setText("Subtotal: $0");
            descuento.setText("Descuento: $0");
            total.setText("TOTAL: $0");
            return;
        }
        int numero = 1;
        for (LineaPedido linea : pedido.lineas()) {
            modelo.addRow(new Object[] {numero, linea.producto().descripcion(), linea.cantidad(),
                Moneda.formatear(linea.producto().precio()), Moneda.formatear(linea.subtotal())});
            numero++;
        }
        encabezado.setText("Pedido #" + pedido.id() + " — " + pedido.cliente().nombreVisible()
                + " — " + (pedido.estaPagado() ? "Pagado" : "Sin pagar"));
        subtotal.setText("Subtotal: " + Moneda.formatear(pedido.subtotal()));
        descuento.setText("Descuento: " + Moneda.formatear(pedido.descuento())
                + " (" + pedido.politicaDescuento().nombre() + ")");
        total.setText("TOTAL: " + Moneda.formatear(pedido.total()));
    }

    private void nuevoPedido() {
        sesion.ejecutar(this, () -> {
            Pedido pedido = sesion.servicio().crearPedido(cliente.getText(), frecuente.isSelected());
            sesion.seleccionar(pedido.id());
            cliente.setText("");
            frecuente.setSelected(false);
        });
    }

    private void quitarItem() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un ítem de la tabla.", "Quitar ítem",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        sesion.conPedido(this, id -> sesion.servicio().quitarItem(id, fila));
    }

    private void cambiarCantidad() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un ítem de la tabla.", "Cambiar cantidad",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        JSpinner selector = new JSpinner(new SpinnerNumberModel((int) modelo.getValueAt(fila, 2), 1, 99, 1));
        int respuesta = JOptionPane.showConfirmDialog(this, selector, "Nueva cantidad",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (respuesta == JOptionPane.OK_OPTION) {
            int nueva = (Integer) selector.getValue();
            sesion.conPedido(this, id -> sesion.servicio().cambiarCantidad(id, fila, nueva));
        }
    }
}
