package com.cafeteria.producto;

import java.util.List;

/** Fábrica concreta de infusiones. */
public class FabricaInfusiones implements FabricaProducto {

    @Override
    public List<Producto> crearProductos() {
        return List.of(new InfusionManzanilla(), new InfusionFrutosRojos());
    }
}
