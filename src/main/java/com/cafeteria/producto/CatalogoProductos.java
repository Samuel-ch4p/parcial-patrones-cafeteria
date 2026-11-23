package com.cafeteria.producto;

import com.cafeteria.modelo.CafeteriaException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Catálogo en memoria. Se alimenta registrando fábricas de producto, por lo que
 * agregar productos nuevos no obliga a modificar esta clase.
 *
 * @author Samuel Chaparro, Cristian Daniel Blanco, Juan Diego Afanador
 */
public class CatalogoProductos {

    private final Map<String, Producto> productos = new LinkedHashMap<>();

    /** Registra todos los productos que produce la fábrica. Rechaza códigos repetidos. */
    public void registrar(FabricaProducto fabrica) {
        for (Producto producto : fabrica.crearProductos()) {
            String clave = producto.codigo().trim().toLowerCase();
            if (productos.containsKey(clave)) {
                throw new CafeteriaException("Ya existe un producto con el código " + producto.codigo());
            }
            productos.put(clave, producto);
        }
    }

    public List<Producto> todos() {
        return Collections.unmodifiableList(new ArrayList<>(productos.values()));
    }

    /** Productos agrupados por categoría, con las categorías en su orden natural. */
    public Map<Categoria, List<Producto>> porCategoria() {
        Map<Categoria, List<Producto>> grupos = new EnumMap<>(Categoria.class);
        for (Producto producto : productos.values()) {
            grupos.computeIfAbsent(producto.categoria(), c -> new ArrayList<>()).add(producto);
        }
        return grupos;
    }

    /** Busca por código, sin distinguir mayúsculas. */
    public Producto buscar(String codigo) {
        Producto producto = codigo == null ? null : productos.get(codigo.trim().toLowerCase());
        if (producto == null) {
            throw new ProductoNoEncontradoException("No existe el producto \"" + codigo + "\" en el catálogo.");
        }
        return producto;
    }
}
