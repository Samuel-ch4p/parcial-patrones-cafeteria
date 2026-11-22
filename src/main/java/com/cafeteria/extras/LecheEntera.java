package com.cafeteria.extras;

import com.cafeteria.producto.Producto;

/** Leche entera: sin recargo. */
public class LecheEntera extends DecoradorBebida {

    public LecheEntera(Producto bebida) {
        super(bebida, "Leche entera", 0);
    }
}
