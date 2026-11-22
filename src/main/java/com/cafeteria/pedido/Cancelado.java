package com.cafeteria.pedido;

import com.cafeteria.modelo.TipoEstado;

/** Estado final: el pedido fue cancelado y no admite pago ni más cambios. */
public class Cancelado implements EstadoPedido {

    @Override
    public TipoEstado tipo() {
        return TipoEstado.CANCELADO;
    }

    @Override
    public EstadoPedido avanzar() {
        throw new TransicionInvalidaException("El pedido está Cancelado: no se puede avanzar.");
    }

    @Override
    public EstadoPedido cancelar() {
        throw new TransicionInvalidaException("El pedido ya estaba Cancelado.");
    }

    @Override
    public boolean permitePago() {
        return false;
    }
}
