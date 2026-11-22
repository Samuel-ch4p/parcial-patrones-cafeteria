package com.cafeteria.producto;

/**
 * Categorías del catálogo. Las cuatro primeras son bebidas; las demás, alimentos.
 *
 * @author Samuel Chaparro, Cristian Daniel Blanco, Juan Diego Afanador
 */
public enum Categoria {
    CAFE("Café", true),
    TE("Té", true),
    INFUSION("Infusión", true),
    BEBIDA_FRIA("Bebida fría", true),
    PANADERIA("Panadería", false),
    POSTRE("Postre", false),
    SANDWICH("Sándwich", false);

    private final String etiqueta;
    private final boolean bebida;

    Categoria(String etiqueta, boolean bebida) {
        this.etiqueta = etiqueta;
        this.bebida = bebida;
    }

    public String etiqueta() {
        return etiqueta;
    }

    public boolean esBebida() {
        return bebida;
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}
