package com.cafeteria.consola;

import com.cafeteria.extras.OpcionPersonalizacion;
import com.cafeteria.extras.TipoOpcion;
import com.cafeteria.modelo.CafeteriaException;
import com.cafeteria.modelo.Moneda;
import com.cafeteria.pago.ResultadoPago;
import com.cafeteria.pedido.Pedido;
import com.cafeteria.producto.Producto;
import com.cafeteria.servicio.CafeteriaService;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Menú de trabajo sobre un pedido: ítems, descuento, pago, estado y ticket.
 * Solo invoca operaciones de {@link CafeteriaService}.
 *
 * @author Samuel Chaparro, Cristian Daniel Blanco, Juan Diego Afanador
 */
class MenuPedido {

    private final CafeteriaService servicio;
    private final LectorEntrada lector;
    private final PrintStream salida;

    MenuPedido(CafeteriaService servicio, LectorEntrada lector, PrintStream salida) {
        this.servicio = servicio;
        this.lector = lector;
        this.salida = salida;
    }

    void mostrar(int pedidoId) {
        boolean volver = false;
        while (!volver) {
            salida.println();
            salida.println(Presentador.pedido(servicio.obtenerPedido(pedidoId)));
            salida.println("--- PEDIDO #" + pedidoId + " ---");
            salida.println("1. Agregar producto");
            salida.println("2. Quitar ítem");
            salida.println("3. Cambiar cantidad");
            salida.println("4. Aplicar descuento");
            salida.println("5. Pagar");
            salida.println("6. Avanzar de estado");
            salida.println("7. Cancelar pedido");
            salida.println("8. Ver ticket");
            salida.println("0. Volver al menú principal");
            int opcion = lector.leerEntero("Elija una opción:", 0, 8);
            try {
                switch (opcion) {
                    case 1 -> agregarProducto(pedidoId);
                    case 2 -> quitarItem(pedidoId);
                    case 3 -> cambiarCantidad(pedidoId);
                    case 4 -> descuento(pedidoId);
                    case 5 -> pagar(pedidoId);
                    case 6 -> {
                        servicio.avanzarEstado(pedidoId);
                        salida.println("  Estado actual: " + servicio.obtenerPedido(pedidoId).tipoEstado());
                    }
                    case 7 -> cancelar(pedidoId);
                    case 8 -> ticket(pedidoId);
                    default -> volver = true;
                }
            } catch (CafeteriaException e) {
                salida.println("  Error: " + e.getMessage());
            }
        }
    }

    /** Muestra los productos numerados y devuelve el elegido, o {@code null} si se cancela. */
    static Producto elegirProducto(List<Producto> productos, LectorEntrada lector, PrintStream salida) {
        for (int i = 0; i < productos.size(); i++) {
            Producto p = productos.get(i);
            salida.println(String.format("  %2d. %-28s %-12s %9s", i + 1, p.nombre(),
                    p.categoria().etiqueta(), Moneda.formatear(p.precioBase())));
        }
        int numero = lector.leerEntero("Número de producto (0 para cancelar):", 0, productos.size());
        return numero == 0 ? null : productos.get(numero - 1);
    }

    private void agregarProducto(int pedidoId) {
        Producto producto = elegirProducto(servicio.catalogo(), lector, salida);
        if (producto == null) {
            return;
        }
        String tamano = null;
        String leche = null;
        List<String> extras = new ArrayList<>();
        if (producto.categoria().esBebida() && lector.confirmar("¿Personalizar la bebida?")) {
            tamano = elegirOpcion(TipoOpcion.TAMANO);
            leche = elegirOpcion(TipoOpcion.LECHE);
            extras = elegirExtras();
            Producto resultado = servicio.previsualizar(producto.codigo(), tamano, leche, extras);
            salida.println("  Quedará: " + resultado.descripcion() + " - " + Moneda.formatear(resultado.precio()));
        }
        int cantidad = lector.leerEntero("Cantidad (1-99):", 1, 99);
        servicio.agregarItem(pedidoId, producto.codigo(), cantidad, tamano, leche, extras);
        salida.println("  Agregado. Subtotal: " + Moneda.formatear(servicio.subtotal(pedidoId)));
    }

    private String elegirOpcion(TipoOpcion tipo) {
        List<OpcionPersonalizacion> opciones = servicio.opciones(tipo);
        salida.println("  " + tipo.etiqueta() + ":");
        salida.println("    0. Ninguno");
        for (int i = 0; i < opciones.size(); i++) {
            OpcionPersonalizacion o = opciones.get(i);
            salida.println("    " + (i + 1) + ". " + o.nombre() + " (+" + Moneda.formatear(o.recargo()) + ")");
        }
        int numero = lector.leerEntero("  Elija " + tipo.etiqueta().toLowerCase() + ":", 0, opciones.size());
        return numero == 0 ? null : opciones.get(numero - 1).codigo();
    }

    private List<String> elegirExtras() {
        List<OpcionPersonalizacion> opciones = servicio.opciones(TipoOpcion.EXTRA);
        List<String> elegidos = new ArrayList<>();
        while (true) {
            salida.println("  Extras (0 para terminar):");
            for (int i = 0; i < opciones.size(); i++) {
                OpcionPersonalizacion o = opciones.get(i);
                salida.println("    " + (i + 1) + ". " + o.nombre() + " (+" + Moneda.formatear(o.recargo()) + ")");
            }
            int numero = lector.leerEntero("  Agregar extra:", 0, opciones.size());
            if (numero == 0) {
                return elegidos;
            }
            elegidos.add(opciones.get(numero - 1).codigo());
            salida.println("    Extra agregado: " + opciones.get(numero - 1).nombre());
        }
    }

    private void quitarItem(int pedidoId) {
        int total = servicio.obtenerPedido(pedidoId).lineas().size();
        if (total == 0) {
            salida.println("  El pedido no tiene ítems.");
            return;
        }
        int numero = lector.leerEntero("Número de ítem a quitar (0 para cancelar):", 0, total);
        if (numero != 0) {
            servicio.quitarItem(pedidoId, numero - 1);
            salida.println("  Ítem quitado. Subtotal: " + Moneda.formatear(servicio.subtotal(pedidoId)));
        }
    }

    private void cambiarCantidad(int pedidoId) {
        int total = servicio.obtenerPedido(pedidoId).lineas().size();
        if (total == 0) {
            salida.println("  El pedido no tiene ítems.");
            return;
        }
        int numero = lector.leerEntero("Número de ítem (0 para cancelar):", 0, total);
        if (numero != 0) {
            int cantidad = lector.leerEntero("Nueva cantidad (1-99):", 1, 99);
            servicio.cambiarCantidad(pedidoId, numero - 1, cantidad);
            salida.println("  Cantidad actualizada. Subtotal: " + Moneda.formatear(servicio.subtotal(pedidoId)));
        }
    }

    private void descuento(int pedidoId) {
        salida.println("  1. Sin descuento");
        salida.println("  2. Cliente frecuente (10%)");
        salida.println("  3. Combo bebida + alimento (15%)");
        salida.println("  4. Código promocional");
        salida.println("  0. Cancelar");
        int opcion = lector.leerEntero("Elija una opción:", 0, 4);
        switch (opcion) {
            case 1 -> servicio.quitarDescuento(pedidoId);
            case 2 -> servicio.aplicarDescuentoClienteFrecuente(pedidoId);
            case 3 -> servicio.aplicarDescuentoCombo(pedidoId);
            case 4 -> servicio.aplicarCodigoPromocional(pedidoId, lector.leerTextoNoVacio("Código promocional:"));
            default -> {
                return;
            }
        }
        Pedido pedido = servicio.obtenerPedido(pedidoId);
        salida.println("  Descuento aplicado: " + Moneda.formatear(pedido.descuento())
                + ". Total: " + Moneda.formatear(pedido.total()));
    }

    private void pagar(int pedidoId) {
        Pedido pedido = servicio.obtenerPedido(pedidoId);
        salida.println("  Total a pagar: " + Moneda.formatear(pedido.total()));
        salida.println("  1. Efectivo");
        salida.println("  2. Tarjeta");
        salida.println("  3. Billetera digital");
        salida.println("  0. Cancelar");
        int opcion = lector.leerEntero("Elija un método:", 0, 3);
        ResultadoPago resultado;
        switch (opcion) {
            case 1 -> resultado = servicio.pagarEfectivo(pedidoId, lector.leerMonto("Monto recibido:"));
            case 2 -> resultado = servicio.pagarTarjeta(pedidoId, lector.leerTexto("Número de tarjeta (16 dígitos):"));
            case 3 -> resultado = servicio.pagarBilletera(pedidoId, lector.leerTexto("Identificador de la billetera:"));
            default -> {
                return;
            }
        }
        salida.println("  Pago aprobado: " + resultado.detalle());
    }

    private void cancelar(int pedidoId) {
        if (lector.confirmar("¿Seguro que desea cancelar el pedido #" + pedidoId + "?")) {
            servicio.cancelarPedido(pedidoId);
            salida.println("  Pedido cancelado.");
        }
    }

    private void ticket(int pedidoId) {
        List<String> formatos = servicio.formatosTicket();
        for (int i = 0; i < formatos.size(); i++) {
            salida.println("  " + (i + 1) + ". " + formatos.get(i));
        }
        int numero = lector.leerEntero("Formato (0 para cancelar):", 0, formatos.size());
        if (numero != 0) {
            salida.println();
            salida.println(servicio.generarTicket(pedidoId, formatos.get(numero - 1)));
        }
    }
}
