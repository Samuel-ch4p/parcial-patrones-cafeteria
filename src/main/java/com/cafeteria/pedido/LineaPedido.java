package com.cafeteria.pedido;

import com.cafeteria.producto.Producto;

/**
 * Ítem de un pedido: un producto (con sus personalizaciones) y una cantidad.
 *
 * @param producto producto o bebida ya personalizada
 * @param cantidad unidades, entre 1 y {@value #MAXIMO}
 * @author Samuel Chaparro, Cristian Daniel Blanco, Juan Diego Afanador
 */
public record LineaPedido(Producto producto, int cantidad) {

    public static final int MAXIMO = 99;

    public LineaPedido {
        if (cantidad < 1 || cantidad > MAXIMO) {
            throw new PedidoException("La cantidad debe estar entre 1 y " + MAXIMO + ".");
        }
    }

    public long subtotal() {
        return producto.precio() * cantidad;
    }

    public LineaPedido conCantidad(int nueva) {
        return new LineaPedido(producto, nueva);
    }
}
