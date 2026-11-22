package com.cafeteria.pago;

import com.cafeteria.modelo.Moneda;

/** Pago en efectivo: el monto recibido debe cubrir el total y se calcula la devuelta. */
public class PagoEfectivo implements MetodoPago {

    private final long montoRecibido;

    public PagoEfectivo(long montoRecibido) {
        this.montoRecibido = montoRecibido;
    }

    @Override
    public String nombre() {
        return "Efectivo";
    }

    @Override
    public ResultadoPago pagar(long total) {
        if (montoRecibido < total) {
            throw new PagoInvalidoException("Monto insuficiente: faltan "
                    + Moneda.formatear(total - montoRecibido) + " para cubrir " + Moneda.formatear(total) + ".");
        }
        long cambio = montoRecibido - total;
        String detalle = "Efectivo (recibido " + Moneda.formatear(montoRecibido)
                + ", cambio " + Moneda.formatear(cambio) + ")";
        return new ResultadoPago(nombre(), detalle, total, cambio);
    }
}
