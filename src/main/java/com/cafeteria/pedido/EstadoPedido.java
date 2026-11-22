package com.cafeteria.pedido;

import com.cafeteria.modelo.TipoEstado;

/**
 * Estado del patrón State. Cada estado decide a cuál pasa el pedido y qué
 * operaciones permite; las transiciones inválidas lanzan {@link TransicionInvalidaException}.
 *
 * @author Samuel Chaparro, Cristian Daniel Blanco, Juan Diego Afanador
 */
public interface EstadoPedido {

    TipoEstado tipo();

    /** Estado siguiente en el flujo normal. */
    EstadoPedido avanzar();

    /** Estado resultante de cancelar el pedido. */
    EstadoPedido cancelar();

    /** Si en este estado todavía se pueden cambiar los ítems y el descuento. */
    default boolean permiteModificar() {
        return false;
    }

    /** Si en este estado se puede cobrar el pedido. */
    default boolean permitePago() {
        return true;
    }
}
