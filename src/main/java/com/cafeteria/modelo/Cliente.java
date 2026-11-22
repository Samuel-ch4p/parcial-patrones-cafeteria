package com.cafeteria.modelo;

/**
 * Cliente de un pedido: anónimo o frecuente.
 *
 * @param nombre     nombre del cliente (vacío si es anónimo)
 * @param frecuente  indica si tiene derecho al descuento de cliente frecuente
 * @author Samuel Chaparro, Cristian Daniel Blanco, Juan Diego Afanador
 */
public record Cliente(String nombre, boolean frecuente) {

    public Cliente {
        nombre = nombre == null ? "" : nombre.trim();
        if (frecuente && nombre.isEmpty()) {
            throw new CafeteriaException("Un cliente frecuente debe tener nombre.");
        }
    }

    public static Cliente anonimo() {
        return new Cliente("", false);
    }

    public static Cliente frecuente(String nombre) {
        return new Cliente(nombre, true);
    }

    /** Nombre listo para mostrar en pantalla y tickets. */
    public String nombreVisible() {
        if (nombre.isEmpty()) {
            return "Cliente anónimo";
        }
        return frecuente ? nombre + " (frecuente)" : nombre;
    }
}
