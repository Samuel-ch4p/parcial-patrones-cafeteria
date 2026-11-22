package com.cafeteria.descuento;

import com.cafeteria.modelo.CafeteriaException;

/** El descuento no se puede aplicar (código inexistente o condición no cumplida). */
public class DescuentoInvalidoException extends CafeteriaException {

    private static final long serialVersionUID = 1L;

    public DescuentoInvalidoException(String mensaje) {
        super(mensaje);
    }
}
