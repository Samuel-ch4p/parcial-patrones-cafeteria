package com.cafeteria.ticket;

import com.cafeteria.modelo.Moneda;
import com.cafeteria.pago.ResultadoPago;
import com.cafeteria.pedido.LineaPedido;
import com.cafeteria.pedido.Pedido;

/** Ticket de dos líneas: resumen del pedido y datos de cobro. */
public class TicketResumido implements FormatoTicket {

    @Override
    public String nombre() {
        return "Resumido";
    }

    @Override
    public String generar(Pedido pedido) {
        int unidades = pedido.lineas().stream().mapToInt(LineaPedido::cantidad).sum();
        StringBuilder sb = new StringBuilder();
        sb.append("Pedido #").append(pedido.id())
                .append(" | ").append(pedido.cliente().nombreVisible())
                .append(" | ").append(unidades).append(unidades == 1 ? " unidad" : " unidades")
                .append(" | ").append(pedido.tipoEstado()).append('\n');
        sb.append("Subtotal ").append(Moneda.formatear(pedido.subtotal()));
        if (pedido.descuento() > 0) {
            sb.append(" | Descuento -").append(Moneda.formatear(pedido.descuento()));
        }
        sb.append(" | TOTAL ").append(Moneda.formatear(pedido.total()))
                .append(" | Pago: ").append(pedido.pago().map(ResultadoPago::metodo).orElse("pendiente"))
                .append('\n');
        return sb.toString();
    }
}
