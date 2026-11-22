package com.cafeteria.producto;

import com.cafeteria.modelo.CafeteriaException;

/**
 * Producto base de los alimentos (panadería, postres y sándwiches).
 *
 * @author Samuel Chaparro, Cristian Daniel Blanco, Juan Diego Afanador
 */
public abstract class Alimento extends ProductoCatalogo {

    protected Alimento(String codigo, String nombre, Categoria categoria, long precioBase, String detalle) {
        super(codigo, nombre, categoria, precioBase, detalle);
        if (categoria.esBebida()) {
            throw new CafeteriaException("La categoría " + categoria + " es de bebidas: " + nombre);
        }
    }
}
