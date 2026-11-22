package com.cafeteria.extras;

import com.cafeteria.producto.Producto;

/** Bebida vegetal de almendras. */
public class LecheAlmendras extends DecoradorBebida {

    public LecheAlmendras(Producto bebida) {
        super(bebida, "Leche de almendras", 1500);
    }
}
