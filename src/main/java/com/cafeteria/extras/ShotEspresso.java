package com.cafeteria.extras;

import com.cafeteria.producto.Producto;

/** Un shot adicional de espresso. */
public class ShotEspresso extends DecoradorBebida {

    public ShotEspresso(Producto bebida) {
        super(bebida, "Shot de espresso", 2000);
    }
}
