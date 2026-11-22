package com.cafeteria.extras;

/** Grupo al que pertenece una opción de personalización. */
public enum TipoOpcion {
    TAMANO("Tamaño"),
    LECHE("Leche"),
    EXTRA("Extra");

    private final String etiqueta;

    TipoOpcion(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String etiqueta() {
        return etiqueta;
    }
}
