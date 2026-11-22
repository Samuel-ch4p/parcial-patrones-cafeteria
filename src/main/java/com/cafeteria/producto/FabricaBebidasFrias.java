package com.cafeteria.producto;

import java.util.List;

/** Fábrica concreta de bebidas frías. */
public class FabricaBebidasFrias implements FabricaProducto {

    @Override
    public List<Producto> crearProductos() {
        return List.of(new LimonadaNatural(), new ColdBrew());
    }
}
