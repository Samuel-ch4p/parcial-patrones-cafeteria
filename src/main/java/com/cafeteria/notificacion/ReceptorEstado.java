package com.cafeteria.notificacion;

/**
 * Observador del patrón Observer: recibe cada cambio de estado de los pedidos.
 *
 * @author Samuel Chaparro, Cristian Daniel Blanco, Juan Diego Afanador
 */
public interface ReceptorEstado {

    /** Nombre único con el que se registra y se quita el receptor. */
    String nombre();

    void actualizar(CambioEstado cambio);
}
