package com.cafeteria.consola;

import com.cafeteria.modelo.Moneda;
import com.cafeteria.pedido.LineaPedido;
import com.cafeteria.pedido.Pedido;
import com.cafeteria.producto.Categoria;
import com.cafeteria.producto.Producto;
import java.util.List;
import java.util.Map;

/**
 * Da formato de texto a catálogo, productos y pedidos para la consola.
 * No contiene reglas de negocio: solo lee lo que devuelve la fachada.
 *
 * @author Samuel Chaparro, Cristian Daniel Blanco, Juan Diego Afanador
 */
final class Presentador {

    private Presentador() {
    }

    static String catalogo(Map<Categoria, List<Producto>> porCategoria) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Categoria, List<Producto>> grupo : porCategoria.entrySet()) {
            sb.append("\n  ").append(grupo.getKey().etiqueta().toUpperCase()).append('\n');
            for (Producto p : grupo.getValue()) {
                sb.append(String.format("    %-24s %-30s %9s%n", p.codigo(), p.nombre(), Moneda.formatear(p.precioBase())));
            }
        }
        return sb.toString();
    }

    static String detalle(Producto p) {
        return "  " + p.nombre() + "\n"
                + "  Código:     " + p.codigo() + "\n"
                + "  Categoría:  " + p.categoria().etiqueta() + (p.categoria().esBebida() ? " (personalizable)" : "") + "\n"
                + "  Precio base: " + Moneda.formatear(p.precioBase()) + "\n"
                + "  " + p.detalle();
    }

    static String pedido(Pedido pedido) {
        StringBuilder sb = new StringBuilder();
        sb.append("  Pedido #").append(pedido.id())
                .append(" | ").append(pedido.cliente().nombreVisible())
                .append(" | Estado: ").append(pedido.tipoEstado())
                .append(" | ").append(pedido.estaPagado() ? "Pagado" : "Sin pagar").append('\n');
        if (pedido.lineas().isEmpty()) {
            sb.append("  (sin ítems)\n");
        }
        int numero = 1;
        for (LineaPedido linea : pedido.lineas()) {
            sb.append(String.format("  %2d) %d x %-45s %9s%n", numero++, linea.cantidad(),
                    linea.producto().descripcion(), Moneda.formatear(linea.subtotal())));
        }
        sb.append("  Subtotal:  ").append(Moneda.formatear(pedido.subtotal())).append('\n');
        sb.append("  Descuento: ").append(Moneda.formatear(pedido.descuento()))
                .append(" (").append(pedido.politicaDescuento().nombre()).append(")\n");
        sb.append("  TOTAL:     ").append(Moneda.formatear(pedido.total()));
        return sb.toString();
    }

    static String resumenLinea(Pedido pedido) {
        return String.format("  #%-3d %-28s %-15s %-9s %10s", pedido.id(), pedido.cliente().nombreVisible(),
                pedido.tipoEstado(), pedido.estaPagado() ? "Pagado" : "Sin pagar",
                Moneda.formatear(pedido.total()));
    }
}
