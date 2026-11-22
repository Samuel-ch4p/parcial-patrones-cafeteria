package com.cafeteria.ticket;

import com.cafeteria.pedido.Pedido;

/**
 * Estrategia de presentación del ticket: mismo contenido, distinta forma.
 *
 * @author Samuel Chaparro, Cristian Daniel Blanco, Juan Diego Afanador
 */
public interface FormatoTicket {

    /** Nombre con el que se elige el formato, por ejemplo {@code Texto plano}. */
    String nombre();

    String generar(Pedido pedido);
}
