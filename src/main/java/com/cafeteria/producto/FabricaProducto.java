package com.cafeteria.producto;

import java.util.List;

/**
 * Creador del patrón Factory Method. Cada fábrica decide qué clases concretas de
 * {@link Producto} instancia; el catálogo solo conoce esta interfaz.
 *
 * @author Samuel Chaparro, Cristian Daniel Blanco, Juan Diego Afanador
 */
public interface FabricaProducto {

    /** Método de fábrica: crea los productos que esta fábrica sabe producir. */
    List<Producto> crearProductos();
}
