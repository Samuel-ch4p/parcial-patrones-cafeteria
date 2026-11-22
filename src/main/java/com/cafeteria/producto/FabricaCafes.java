package com.cafeteria.producto;

import java.util.List;

/** Fábrica concreta de cafés. */
public class FabricaCafes implements FabricaProducto {

    @Override
    public List<Producto> crearProductos() {
        return List.of(new Espresso(), new Capuchino(), new Latte());
    }
}
