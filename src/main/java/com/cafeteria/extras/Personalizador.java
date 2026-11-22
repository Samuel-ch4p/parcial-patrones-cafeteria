package com.cafeteria.extras;

import com.cafeteria.producto.Producto;
import java.util.List;

/**
 * Arma una bebida personalizada apilando decoradores: primero tamaño, luego leche
 * y al final los extras, en el orden recibido (se pueden repetir).
 *
 * @author Samuel Chaparro, Cristian Daniel Blanco, Juan Diego Afanador
 */
public class Personalizador {

    private final CatalogoOpciones catalogo;

    public Personalizador(CatalogoOpciones catalogo) {
        this.catalogo = catalogo;
    }

    /**
     * @param base   bebida del catálogo
     * @param tamano código de tamaño, o {@code null} para no cambiarlo
     * @param leche  código de leche, o {@code null} para no cambiarla
     * @param extras códigos de extras, o {@code null}
     * @return la bebida decorada, o la misma bebida si no se pidió nada
     */
    public Producto personalizar(Producto base, String tamano, String leche, List<String> extras) {
        Producto resultado = base;
        if (hayTexto(tamano)) {
            resultado = catalogo.buscar(TipoOpcion.TAMANO, tamano).aplicar(resultado);
        }
        if (hayTexto(leche)) {
            resultado = catalogo.buscar(TipoOpcion.LECHE, leche).aplicar(resultado);
        }
        if (extras != null) {
            for (String extra : extras) {
                resultado = catalogo.buscar(TipoOpcion.EXTRA, extra).aplicar(resultado);
            }
        }
        return resultado;
    }

    private static boolean hayTexto(String valor) {
        return valor != null && !valor.isBlank();
    }
}
