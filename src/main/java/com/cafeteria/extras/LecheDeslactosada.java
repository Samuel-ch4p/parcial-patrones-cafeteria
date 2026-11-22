package com.cafeteria.extras;

import com.cafeteria.producto.Producto;

/** Leche deslactosada. */
public class LecheDeslactosada extends DecoradorBebida {

    public LecheDeslactosada(Producto bebida) {
        super(bebida, "Leche deslactosada", 500);
    }
}
