package com.cafeteria.pedido;

import com.cafeteria.modelo.Moneda;
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

    /** Valor máximo de un ítem (precio x cantidad): 100 millones de pesos. Evita desbordar los cálculos. */
    public static final long VALOR_MAXIMO = 100_000_000L;

    public LineaPedido {
        if (cantidad < 1 || cantidad > MAXIMO) {
            throw new PedidoException("La cantidad debe estar entre 1 y " + MAXIMO + ".");
        }
        if (producto.precio() > VALOR_MAXIMO / cantidad) {
            throw new PedidoException("El valor del ítem supera el máximo permitido de "
                    + Moneda.formatear(VALOR_MAXIMO) + ".");
        }
    }

    public long subtotal() {
        return producto.precio() * cantidad;
    }

    public LineaPedido conCantidad(int nueva) {
        return new LineaPedido(producto, nueva);
    }
}
