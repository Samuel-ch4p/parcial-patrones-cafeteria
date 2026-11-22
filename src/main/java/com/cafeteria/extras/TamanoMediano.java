package com.cafeteria.extras;

import com.cafeteria.producto.Producto;

/** Tamaño mediano. */
public class TamanoMediano extends DecoradorBebida {

    public TamanoMediano(Producto bebida) {
        super(bebida, "Mediano", 1000);
    }
}
