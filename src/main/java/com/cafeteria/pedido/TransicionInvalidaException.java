package com.cafeteria.pedido;

import com.cafeteria.modelo.CafeteriaException;

/** Se intentó una transición de estado que el ciclo de vida no permite. */
public class TransicionInvalidaException extends CafeteriaException {

    private static final long serialVersionUID = 1L;

    public TransicionInvalidaException(String mensaje) {
        super(mensaje);
    }
}
