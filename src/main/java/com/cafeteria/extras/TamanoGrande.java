package com.cafeteria.extras;

import com.cafeteria.producto.Producto;

/** Tamaño grande. */
public class TamanoGrande extends DecoradorBebida {

    public TamanoGrande(Producto bebida) {
        super(bebida, "Grande", 2000);
    }
}
