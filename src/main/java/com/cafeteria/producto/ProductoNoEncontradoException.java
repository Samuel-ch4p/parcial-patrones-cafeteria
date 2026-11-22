package com.cafeteria.producto;

import com.cafeteria.modelo.CafeteriaException;

/** Se pidió un producto que no existe en el catálogo, o se intentó registrar uno repetido. */
public class ProductoNoEncontradoException extends CafeteriaException {

    private static final long serialVersionUID = 1L;

    public ProductoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
