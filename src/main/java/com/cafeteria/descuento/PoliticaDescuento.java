package com.cafeteria.descuento;

/**
 * Estrategia de descuento. Cada política decide cuánto se descuenta de un pedido.
 *
 * @author Samuel Chaparro, Cristian Daniel Blanco, Juan Diego Afanador
 */
public interface PoliticaDescuento {

    /** Nombre para mostrar en pantalla y tickets. */
    String nombre();

    /** Indica si el pedido cumple la condición de la política. Por defecto, siempre. */
    default boolean aplicable(ContextoDescuento contexto) {
        return true;
    }

    /** Motivo que se informa cuando {@link #aplicable} es falso. */
    default String motivoNoAplicable() {
        return "El descuento no aplica a este pedido.";
    }

    /** Monto a descontar en pesos; 0 si la política no aplica. */
    long calcular(ContextoDescuento contexto);

    /** Lanza {@link DescuentoInvalidoException} si la política no aplica al pedido. */
    default void validar(ContextoDescuento contexto) {
        if (!aplicable(contexto)) {
            throw new DescuentoInvalidoException(motivoNoAplicable());
        }
    }
}
