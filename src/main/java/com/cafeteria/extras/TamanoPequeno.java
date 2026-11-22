package com.cafeteria.extras;

import com.cafeteria.producto.Producto;

/** Tamaño pequeño: sin recargo. */
public class TamanoPequeno extends DecoradorBebida {

    public TamanoPequeno(Producto bebida) {
        super(bebida, "Pequeño", 0);
    }
}
