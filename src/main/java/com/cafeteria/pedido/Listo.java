package com.cafeteria.pedido;

import com.cafeteria.modelo.TipoEstado;

/** El pedido espera al cliente: solo puede entregarse, ya no se cancela. */
public class Listo implements EstadoPedido {

    @Override
    public TipoEstado tipo() {
        return TipoEstado.LISTO;
    }

    @Override
    public EstadoPedido avanzar() {
        return new Entregado();
    }

    @Override
    public EstadoPedido cancelar() {
        throw new TransicionInvalidaException("No se puede cancelar un pedido que ya está Listo.");
    }
}
