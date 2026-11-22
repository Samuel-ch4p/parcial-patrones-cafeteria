package com.cafeteria.extras;

import com.cafeteria.producto.Categoria;
import com.cafeteria.producto.Producto;
import java.util.ArrayList;
import java.util.List;

/**
 * Decorador base del patrón Decorator. Envuelve una bebida, suma un recargo al
 * precio y agrega una etiqueta a la descripción. Los decoradores se pueden apilar
 * en cualquier orden.
 *
 * @author Samuel Chaparro, Cristian Daniel Blanco, Juan Diego Afanador
 */
public abstract class DecoradorBebida implements Producto {

    private final Producto envuelto;
    private final String etiqueta;
    private final long recargo;

    protected DecoradorBebida(Producto envuelto, String etiqueta, long recargo) {
        if (!envuelto.categoria().esBebida()) {
            throw new PersonalizacionInvalidaException(
                    "Solo las bebidas se pueden personalizar: " + envuelto.nombre());
        }
        this.envuelto = envuelto;
        this.etiqueta = etiqueta;
        this.recargo = recargo;
    }

    @Override
    public String codigo() {
        return envuelto.codigo();
    }

    @Override
    public String nombre() {
        return envuelto.nombre();
    }

    @Override
    public Categoria categoria() {
        return envuelto.categoria();
    }

    @Override
    public long precioBase() {
        return envuelto.precioBase();
    }

    @Override
    public long precio() {
        return envuelto.precio() + recargo;
    }

    @Override
    public String detalle() {
        return envuelto.detalle();
    }

    @Override
    public List<String> personalizaciones() {
        List<String> lista = new ArrayList<>(envuelto.personalizaciones());
        lista.add(etiqueta);
        return lista;
    }
}
