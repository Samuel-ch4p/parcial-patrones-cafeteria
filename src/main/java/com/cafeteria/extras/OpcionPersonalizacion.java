package com.cafeteria.extras;

import com.cafeteria.producto.Producto;
import java.util.function.UnaryOperator;

/**
 * Opción que el usuario puede elegir y el decorador que la implementa.
 *
 * @param codigo    identificador usado por las interfaces, por ejemplo {@code shot}
 * @param nombre    nombre para mostrar
 * @param tipo      grupo de la opción
 * @param recargo   precio adicional en pesos
 * @param aplicador función que envuelve la bebida con el decorador correspondiente
 * @author Samuel Chaparro, Cristian Daniel Blanco, Juan Diego Afanador
 */
public record OpcionPersonalizacion(String codigo, String nombre, TipoOpcion tipo, long recargo,
                                    UnaryOperator<Producto> aplicador) {

    /** Envuelve la bebida con el decorador de esta opción. */
    public Producto aplicar(Producto bebida) {
        return aplicador.apply(bebida);
    }
}
