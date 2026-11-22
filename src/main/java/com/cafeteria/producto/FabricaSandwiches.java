package com.cafeteria.producto;

import java.util.List;

/** Fábrica concreta de sándwiches. */
public class FabricaSandwiches implements FabricaProducto {

    @Override
    public List<Producto> crearProductos() {
        return List.of(new SandwichJamonQueso(), new SandwichPollo());
    }
}
