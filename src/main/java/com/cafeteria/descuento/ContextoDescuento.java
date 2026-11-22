package com.cafeteria.descuento;

/**
 * Datos del pedido que una política de descuento necesita para decidir.
 * Se usa un contexto propio para que el paquete no dependa de la clase Pedido.
 *
 * @param clienteFrecuente si el cliente del pedido es frecuente
 * @param subtotal         suma de los ítems, en pesos
 * @param hayBebida        si el pedido incluye al menos una bebida
 * @param hayAlimento      si el pedido incluye al menos un alimento
 * @author Samuel Chaparro, Cristian Daniel Blanco, Juan Diego Afanador
 */
public record ContextoDescuento(boolean clienteFrecuente, long subtotal, boolean hayBebida, boolean hayAlimento) {
}
