package com.cafeteria.producto;

import java.util.List;

/** Fábrica concreta de tés. */
public class FabricaTes implements FabricaProducto {

    @Override
    public List<Producto> crearProductos() {
        return List.of(new TeNegro());
    }
}
