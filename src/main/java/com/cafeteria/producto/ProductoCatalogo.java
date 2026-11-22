package com.cafeteria.producto;

import com.cafeteria.modelo.CafeteriaException;
import java.util.List;

/**
 * Producto del catálogo con datos fijos. Es la base de {@link Bebida} y {@link Alimento}.
 *
 * @author Samuel Chaparro, Cristian Daniel Blanco, Juan Diego Afanador
 */
public abstract class ProductoCatalogo implements Producto {

    private final String codigo;
    private final String nombre;
    private final Categoria categoria;
    private final long precioBase;
    private final String detalle;

    protected ProductoCatalogo(String codigo, String nombre, Categoria categoria, long precioBase, String detalle) {
        if (codigo == null || codigo.isBlank() || nombre == null || nombre.isBlank()) {
            throw new CafeteriaException("El producto necesita código y nombre.");
        }
        if (precioBase < 0) {
            throw new CafeteriaException("El precio base no puede ser negativo: " + nombre);
        }
        this.codigo = codigo;
        this.nombre = nombre;
        this.categoria = categoria;
        this.precioBase = precioBase;
        this.detalle = detalle;
    }

    @Override
    public String codigo() {
        return codigo;
    }

    @Override
    public String nombre() {
        return nombre;
    }

    @Override
    public Categoria categoria() {
        return categoria;
    }

    @Override
    public long precioBase() {
        return precioBase;
    }

    @Override
    public long precio() {
        return precioBase;
    }

    @Override
    public String detalle() {
        return detalle;
    }

    @Override
    public List<String> personalizaciones() {
        return List.of();
    }
}
