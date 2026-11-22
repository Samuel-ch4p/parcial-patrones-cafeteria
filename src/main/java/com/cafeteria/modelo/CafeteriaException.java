package com.cafeteria.modelo;

/**
 * Error de negocio con un mensaje pensado para mostrarse al usuario.
 * Todas las excepciones del sistema heredan de esta clase.
 *
 * @author Samuel Chaparro, Cristian Daniel Blanco, Juan Diego Afanador
 */
public class CafeteriaException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public CafeteriaException(String mensaje) {
        super(mensaje);
    }
}
