package com.cafeteria.extras;

import com.cafeteria.producto.Producto;

/** Jarabe de sabor. */
public class Jarabe extends DecoradorBebida {

    public Jarabe(Producto bebida) {
        super(bebida, "Jarabe", 1500);
    }
}
