package com.cafeteria.notificacion;

import com.cafeteria.modelo.TipoEstado;

/**
 * Evento que se comunica a los receptores cuando un pedido cambia de estado.
 *
 * @param pedidoId número del pedido
 * @param cliente  nombre visible del cliente
 * @param anterior estado previo, o {@code null} si el pedido acaba de crearse
 * @param nuevo    estado actual
 * @author Samuel Chaparro, Cristian Daniel Blanco, Juan Diego Afanador
 */
public record CambioEstado(int pedidoId, String cliente, TipoEstado anterior, TipoEstado nuevo) {
}
