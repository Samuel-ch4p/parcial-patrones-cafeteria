package com.cafeteria.ticket;

import com.cafeteria.modelo.Moneda;
import com.cafeteria.pago.ResultadoPago;
import com.cafeteria.pedido.LineaPedido;
import com.cafeteria.pedido.Pedido;

/** Ticket detallado, una línea por ítem con sus personalizaciones. */
public class TicketTextoPlano implements FormatoTicket {

    private static final int ANCHO = 44;

    @Override
    public String nombre() {
        return "Texto plano";
    }

    @Override
    public String generar(Pedido pedido) {
        String separador = "-".repeat(ANCHO);
        StringBuilder sb = new StringBuilder();
        sb.append("CAFETERIA - TICKET\n");
        sb.append("Pedido #").append(pedido.id()).append('\n');
        sb.append("Cliente: ").append(pedido.cliente().nombreVisible()).append('\n');
        sb.append("Estado:  ").append(pedido.tipoEstado()).append('\n');
        sb.append(separador).append('\n');
        for (LineaPedido linea : pedido.lineas()) {
            sb.append(fila(linea.cantidad() + " x " + linea.producto().nombre(),
                    Moneda.formatear(linea.subtotal()))).append('\n');
            for (String extra : linea.producto().personalizaciones()) {
                sb.append("      + ").append(extra).append('\n');
            }
        }
        sb.append(separador).append('\n');
        sb.append(fila("Subtotal", Moneda.formatear(pedido.subtotal()))).append('\n');
        if (pedido.descuento() > 0) {
            sb.append(fila("Descuento: " + pedido.politicaDescuento().nombre(),
                    "-" + Moneda.formatear(pedido.descuento()))).append('\n');
        }
        sb.append(fila("TOTAL", Moneda.formatear(pedido.total()))).append('\n');
        sb.append(separador).append('\n');
        sb.append("Pago: ").append(pedido.pago().map(ResultadoPago::detalle).orElse("pendiente")).append('\n');
        return sb.toString();
    }

    private static String fila(String izquierda, String derecha) {
        int espacios = Math.max(1, ANCHO - izquierda.length() - derecha.length());
        return izquierda + " ".repeat(espacios) + derecha;
    }
}
