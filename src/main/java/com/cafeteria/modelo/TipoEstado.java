package com.cafeteria.modelo;

/**
 * Estados posibles de un pedido. Sirve para filtrar el historial y para
 * informar los cambios; el comportamiento de cada estado vive en el paquete pedido.
 *
 * @author Samuel Chaparro, Cristian Daniel Blanco, Juan Diego Afanador
 */
public enum TipoEstado {
    RECIBIDO("Recibido"),
    EN_PREPARACION("En preparación"),
    LISTO("Listo"),
    ENTREGADO("Entregado"),
    CANCELADO("Cancelado");

    private final String etiqueta;

    TipoEstado(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String etiqueta() {
        return etiqueta;
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}
