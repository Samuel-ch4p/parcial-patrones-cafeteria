package com.cafeteria.pedido;

import com.cafeteria.modelo.TipoEstado;

/** Estado final: el pedido fue entregado y no admite más cambios. */
public class Entregado implements EstadoPedido {

    @Override
    public TipoEstado tipo() {
        return TipoEstado.ENTREGADO;
    }

    @Override
    public EstadoPedido avanzar() {
        throw new TransicionInvalidaException("El pedido ya fue Entregado: no hay un estado siguiente.");
    }

    @Override
    public EstadoPedido cancelar() {
        throw new TransicionInvalidaException("No se puede cancelar un pedido ya Entregado.");
    }
}
