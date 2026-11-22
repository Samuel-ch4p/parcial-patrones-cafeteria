package com.cafeteria.producto;

import java.util.List;

/** Fábrica concreta de panadería. */
public class FabricaPanaderia implements FabricaProducto {

    @Override
    public List<Producto> crearProductos() {
        return List.of(new Croissant(), new PanDeBono());
    }
}
