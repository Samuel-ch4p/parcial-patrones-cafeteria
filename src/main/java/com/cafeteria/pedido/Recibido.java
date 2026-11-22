package com.cafeteria.pedido;

import com.cafeteria.modelo.TipoEstado;

/** Estado inicial: el pedido se puede modificar, avanzar o cancelar. */
public class Recibido implements EstadoPedido {

    @Override
    public TipoEstado tipo() {
        return TipoEstado.RECIBIDO;
    }

    @Override
    public EstadoPedido avanzar() {
        return new EnPreparacion();
    }

    @Override
    public EstadoPedido cancelar() {
        return new Cancelado();
    }

    @Override
    public boolean permiteModificar() {
        return true;
    }
}
