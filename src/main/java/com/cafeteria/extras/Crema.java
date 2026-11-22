package com.cafeteria.extras;

import com.cafeteria.producto.Producto;

/** Crema batida. */
public class Crema extends DecoradorBebida {

    public Crema(Producto bebida) {
        super(bebida, "Crema", 1000);
    }
}
