package com.cafeteria.descuento;

/**
 * Base de las políticas que descuentan un porcentaje del subtotal cuando se cumple
 * una condición. Las subclases solo definen la condición.
 *
 * @author Samuel Chaparro, Cristian Daniel Blanco, Juan Diego Afanador
 */
public abstract class DescuentoPorcentual implements PoliticaDescuento {

    private final String nombre;
    private final int porcentaje;

    protected DescuentoPorcentual(String nombre, int porcentaje) {
        this.nombre = nombre;
        this.porcentaje = porcentaje;
    }

    public int porcentaje() {
        return porcentaje;
    }

    @Override
    public String nombre() {
        return nombre;
    }

    @Override
    public long calcular(ContextoDescuento contexto) {
        if (!aplicable(contexto)) {
            return 0;
        }
        return contexto.subtotal() * porcentaje / 100;
    }
}
