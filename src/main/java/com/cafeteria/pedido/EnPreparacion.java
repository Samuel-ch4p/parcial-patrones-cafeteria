package com.cafeteria.pedido;

import com.cafeteria.modelo.TipoEstado;

/** La cocina está preparando el pedido; todavía se puede cancelar. */
public class EnPreparacion implements EstadoPedido {

    @Override
    public TipoEstado tipo() {
        return TipoEstado.EN_PREPARACION;
    }

    @Override
    public EstadoPedido avanzar() {
        return new Listo();
    }

    @Override
    public EstadoPedido cancelar() {
        return new Cancelado();
    }
}
