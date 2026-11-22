package com.cafeteria.producto;

import com.cafeteria.modelo.CafeteriaException;

/**
 * Producto base de las bebidas: solo ellas se pueden personalizar.
 *
 * @author Samuel Chaparro, Cristian Daniel Blanco, Juan Diego Afanador
 */
public abstract class Bebida extends ProductoCatalogo {

    protected Bebida(String codigo, String nombre, Categoria categoria, long precioBase, String detalle) {
        super(codigo, nombre, categoria, precioBase, detalle);
        if (!categoria.esBebida()) {
            throw new CafeteriaException("La categoría " + categoria + " no es de bebidas: " + nombre);
        }
    }
}
