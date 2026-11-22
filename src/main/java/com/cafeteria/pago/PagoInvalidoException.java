package com.cafeteria.pago;

import com.cafeteria.modelo.CafeteriaException;

/** Los datos del método de pago no son válidos y el cobro se rechaza. */
public class PagoInvalidoException extends CafeteriaException {

    private static final long serialVersionUID = 1L;

    public PagoInvalidoException(String mensaje) {
        super(mensaje);
    }
}
