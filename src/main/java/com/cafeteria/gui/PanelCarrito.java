package com.cafeteria.gui;

import com.cafeteria.modelo.Moneda;
import com.cafeteria.pedido.LineaPedido;
import com.cafeteria.pedido.Pedido;
import com.cafeteria.servicio.CafeteriaService;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;

/**
 * Pedido en curso: cliente, carrito, descuento, pago, estado y ticket.
 * Cada botón llama a una operación de la fachada; los cálculos los hace el núcleo.
 *
 * @author Samuel Chaparro, Cristian Daniel Blanco, Juan Diego Afanador
 */
class PanelCarrito extends JPanel {

    private static final String[] DESCUENTOS = {
        "Sin descuento", "Cliente frecuente 10%", "Combo 15%", "Código promocional"
    };
    private static final String[] METODOS = {"Efectivo", "Tarjeta", "Billetera digital"};
    private static final String[] ETIQUETAS_PAGO = {"Monto recibido:", "N.º tarjeta:", "Identificador:"};

    private final SesionGui sesion;
    private final JTextField cliente = new JTextField(12);
    private final JCheckBox frecuente = new JCheckBox("Frecuente");
    private final JLabel encabezado = new JLabel("Sin pedido. Cree uno o agregue un producto.");
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
    private final JComboBox<String> tipoDescuento = new JComboBox<>(DESCUENTOS);
    private final JTextField codigoPromo = new JTextField(8);
    private final JComboBox<String> metodoPago = new JComboBox<>(METODOS);
    private final JLabel etiquetaPago = new JLabel(ETIQUETAS_PAGO[0]);
    private final JTextField datoPago = new JTextField(10);
    private final JLabel estado = new JLabel("Estado: -");

    PanelCarrito(SesionGui sesion) {
        super(new BorderLayout(6, 6));
        this.sesion = sesion;
        setBorder(BorderFactory.createTitledBorder("2. Pedido en curso"));
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        total.setFont(total.getFont().deriveFont(Font.BOLD, 16f));

        JPanel norte = new JPanel(new GridLayout(0, 1));
        JPanel nuevo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        nuevo.add(new JLabel("Cliente:"));
        nuevo.add(cliente);
        nuevo.add(frecuente);
        JButton botonNuevo = new JButton("Nuevo pedido");
        botonNuevo.addActionListener(e -> nuevoPedido());
        nuevo.add(botonNuevo);
        norte.add(nuevo);
        norte.add(encabezado);
        add(norte, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
        add(armarControles(), BorderLayout.SOUTH);

        metodoPago.addActionListener(e -> etiquetaPago.setText(ETIQUETAS_PAGO[metodoPago.getSelectedIndex()]));
        sesion.alCambiar(this::refrescar);
        refrescar();
    }

    private JPanel armarControles() {
        JPanel items = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton quitar = new JButton("Quitar ítem");
        quitar.addActionListener(e -> quitarItem());
        JButton cantidad = new JButton("Cambiar cantidad");
        cantidad.addActionListener(e -> cambiarCantidad());
        items.add(quitar);
        items.add(cantidad);

        JPanel totales = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 2));
        totales.add(subtotal);
        totales.add(descuento);
        totales.add(total);

        JPanel descuentos = new JPanel(new FlowLayout(FlowLayout.LEFT));
        descuentos.add(new JLabel("Descuento:"));
        descuentos.add(tipoDescuento);
        descuentos.add(new JLabel("Código:"));
        descuentos.add(codigoPromo);
        JButton aplicar = new JButton("Aplicar");
        aplicar.addActionListener(e -> aplicarDescuento());
        descuentos.add(aplicar);

        JPanel pago = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pago.add(new JLabel("Pago:"));
        pago.add(metodoPago);
        pago.add(etiquetaPago);
        pago.add(datoPago);
        JButton pagar = new JButton("Pagar");
        pagar.addActionListener(e -> pagar());
        pago.add(pagar);

        JPanel ciclo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        ciclo.add(estado);
        JButton avanzar = new JButton("Avanzar estado");
        avanzar.addActionListener(e -> conPedido(id -> sesion.servicio().avanzarEstado(id)));
        JButton cancelar = new JButton("Cancelar pedido");
        cancelar.addActionListener(e -> cancelarPedido());
        JButton ticket = new JButton("Ver ticket");
        ticket.addActionListener(e -> {
            Pedido pedido = sesion.pedidoActual();
            if (pedido != null) {
                DialogoTicket.mostrar(this, sesion.servicio(), pedido.id());
            }
        });
        ciclo.add(avanzar);
        ciclo.add(cancelar);
        ciclo.add(ticket);

        JPanel sur = new JPanel(new GridLayout(0, 1));
        sur.add(items);
        sur.add(totales);
        sur.add(descuentos);
        sur.add(pago);
        sur.add(ciclo);
        return sur;
    }

    private void refrescar() {
        modelo.setRowCount(0);
        Pedido pedido = sesion.pedidoActual();
        if (pedido == null) {
            encabezado.setText("Sin pedido. Cree uno o agregue un producto.");
            subtotal.setText("Subtotal: $0");
            descuento.setText("Descuento: $0");
            total.setText("TOTAL: $0");
            estado.setText("Estado: -");
            return;
        }
        int numero = 1;
        for (LineaPedido l : pedido.lineas()) {
            modelo.addRow(new Object[] {numero++, l.producto().descripcion(), l.cantidad(),
                Moneda.formatear(l.producto().precio()), Moneda.formatear(l.subtotal())});
        }
        encabezado.setText("Pedido #" + pedido.id() + " — " + pedido.cliente().nombreVisible()
                + " — " + (pedido.estaPagado() ? "Pagado" : "Sin pagar"));
        subtotal.setText("Subtotal: " + Moneda.formatear(pedido.subtotal()));
        descuento.setText("Descuento: " + Moneda.formatear(pedido.descuento())
                + " (" + pedido.politicaDescuento().nombre() + ")");
        total.setText("TOTAL: " + Moneda.formatear(pedido.total()));
        estado.setText("Estado: " + pedido.tipoEstado());
    }

    private interface AccionSobrePedido {
        void ejecutar(int pedidoId);
    }

    /** Ejecuta la acción sobre el pedido en curso, o avisa si todavía no hay uno. */
    private void conPedido(AccionSobrePedido accion) {
        Pedido pedido = sesion.pedidoActual();
        if (pedido == null) {
            JOptionPane.showMessageDialog(this, "Primero cree un pedido o agregue un producto.",
                    "Sin pedido", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        sesion.ejecutar(this, () -> accion.ejecutar(pedido.id()));
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
        conPedido(id -> sesion.servicio().quitarItem(id, fila));
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
            conPedido(id -> sesion.servicio().cambiarCantidad(id, fila, (Integer) selector.getValue()));
        }
    }

    private void aplicarDescuento() {
        CafeteriaService servicio = sesion.servicio();
        switch (tipoDescuento.getSelectedIndex()) {
            case 0 -> conPedido(servicio::quitarDescuento);
            case 1 -> conPedido(servicio::aplicarDescuentoClienteFrecuente);
            case 2 -> conPedido(servicio::aplicarDescuentoCombo);
            default -> conPedido(id -> servicio.aplicarCodigoPromocional(id, codigoPromo.getText()));
        }
    }

    private void pagar() {
        CafeteriaService servicio = sesion.servicio();
        conPedido(id -> {
            String dato = datoPago.getText();
            var resultado = switch (metodoPago.getSelectedIndex()) {
                case 0 -> servicio.pagarEfectivo(id, montoDe(dato));
                case 1 -> servicio.pagarTarjeta(id, dato);
                default -> servicio.pagarBilletera(id, dato);
            };
            datoPago.setText("");
            JOptionPane.showMessageDialog(this, "Pago aprobado: " + resultado.detalle(), "Pago",
                    JOptionPane.INFORMATION_MESSAGE);
        });
    }

    /** Convierte el texto del monto; si no es un número lo trata como 0 para que el pago se rechace. */
    private static long montoDe(String texto) {
        try {
            return Long.parseLong(texto.trim().replace(".", "").replace("$", ""));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void cancelarPedido() {
        Pedido pedido = sesion.pedidoActual();
        if (pedido == null) {
            return;
        }
        int respuesta = JOptionPane.showConfirmDialog(this, "¿Cancelar el pedido #" + pedido.id() + "?",
                "Cancelar pedido", JOptionPane.YES_NO_OPTION);
        if (respuesta == JOptionPane.YES_OPTION) {
            conPedido(id -> sesion.servicio().cancelarPedido(id));
        }
    }
}
