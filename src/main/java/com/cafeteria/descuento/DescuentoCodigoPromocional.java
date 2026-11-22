package com.cafeteria.descuento;

import java.util.Map;

/**
 * Descuento por código promocional. El constructor rechaza los códigos que no
 * existen, así que un código inválido nunca llega a quedar asociado a un pedido.
 *
 * @author Samuel Chaparro, Cristian Daniel Blanco, Juan Diego Afanador
 */
public class DescuentoCodigoPromocional extends DescuentoPorcentual {

    private static final Map<String, Integer> CODIGOS = Map.of(
            "CAFE10", 10,
            "DULCE15", 15,
            "NOVIEMBRE20", 20);

    public DescuentoCodigoPromocional(String codigo) {
        super(nombreDe(normalizar(codigo)), porcentajeDe(normalizar(codigo)));
    }

    private static String normalizar(String codigo) {
        return codigo == null ? "" : codigo.trim().toUpperCase();
    }

    private static int porcentajeDe(String codigo) {
        Integer porcentaje = CODIGOS.get(codigo);
        if (porcentaje == null) {
            throw new DescuentoInvalidoException("El código promocional \"" + codigo + "\" no es válido.");
        }
        return porcentaje;
    }

    private static String nombreDe(String codigo) {
        return "Código " + codigo + " (" + porcentajeDe(codigo) + "%)";
    }
}
