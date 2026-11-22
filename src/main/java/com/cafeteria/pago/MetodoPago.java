package com.cafeteria.pago;

/**
 * Estrategia de pago. Cada método valida sus propios datos y simula el cobro.
 *
 * @author Samuel Chaparro, Cristian Daniel Blanco, Juan Diego Afanador
 */
public interface MetodoPago {

    String nombre();

    /**
     * Valida los datos propios del método y cobra el total.
     *
     * @param total valor a cobrar en pesos
     * @return el comprobante del pago
     * @throws PagoInvalidoException si los datos no son válidos
     */
    ResultadoPago pagar(long total);
}
