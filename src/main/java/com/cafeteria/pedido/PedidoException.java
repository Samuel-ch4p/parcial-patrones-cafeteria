package com.cafeteria.pedido;

import com.cafeteria.modelo.CafeteriaException;

/** Operación no permitida sobre un pedido (vacío, ya pagado, inexistente, etc.). */
public class PedidoException extends CafeteriaException {

    private static final long serialVersionUID = 1L;

    public PedidoException(String mensaje) {
        super(mensaje);
    }
}
