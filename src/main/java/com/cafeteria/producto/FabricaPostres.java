package com.cafeteria.producto;

import java.util.List;

/** Fábrica concreta de postres. */
public class FabricaPostres implements FabricaProducto {

    @Override
    public List<Producto> crearProductos() {
        return List.of(new BrownieChocolate(), new TortaZanahoria());
    }
}
