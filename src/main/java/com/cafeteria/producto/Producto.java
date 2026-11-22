package com.cafeteria.producto;

import java.util.List;

/**
 * Algo que se puede vender. Es el componente común de los productos del catálogo
 * y de los decoradores que personalizan una bebida.
 *
 * @author Samuel Chaparro, Cristian Daniel Blanco, Juan Diego Afanador
 */
public interface Producto {

    /** Identificador estable dentro del catálogo, por ejemplo {@code capuchino}. */
    String codigo();

    String nombre();

    Categoria categoria();

    /** Precio del producto sin personalizaciones, en pesos. */
    long precioBase();

    /** Precio final, incluidas las personalizaciones aplicadas. */
    long precio();

    /** Texto descriptivo del producto para la consulta de detalle. */
    String detalle();

    /** Personalizaciones aplicadas, en el orden en que se aplicaron. Vacía si no hay. */
    List<String> personalizaciones();

    /** Nombre seguido de las personalizaciones, por ejemplo {@code Latte (Grande, Crema)}. */
    default String descripcion() {
        List<String> extras = personalizaciones();
        if (extras.isEmpty()) {
            return nombre();
        }
        return nombre() + " (" + String.join(", ", extras) + ")";
    }
}
