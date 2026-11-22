package com.cafeteria.pago;

/**
 * Comprobante de un pago aprobado (simulado).
 *
 * @param metodo  nombre del método usado
 * @param detalle texto para el ticket, por ejemplo {@code Tarjeta terminada en 1234}
 * @param monto   valor cobrado en pesos
 * @param cambio  devuelta al cliente en pesos (solo efectivo)
 * @author Samuel Chaparro, Cristian Daniel Blanco, Juan Diego Afanador
 */
public record ResultadoPago(String metodo, String detalle, long monto, long cambio) {
}
