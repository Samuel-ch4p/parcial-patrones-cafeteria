package com.cafeteria.extras;

import com.cafeteria.producto.Producto;

/** Bebida vegetal de avena. */
public class LecheAvena extends DecoradorBebida {

    public LecheAvena(Producto bebida) {
        super(bebida, "Leche de avena", 1500);
    }
}
