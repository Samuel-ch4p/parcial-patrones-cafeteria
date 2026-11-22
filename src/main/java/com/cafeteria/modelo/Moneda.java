package com.cafeteria.modelo;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Formato de dinero. El sistema trabaja con una sola moneda, pesos colombianos
 * (COP), sin decimales, representados como {@code long}.
 *
 * @author Samuel Chaparro, Cristian Daniel Blanco, Juan Diego Afanador
 */
public final class Moneda {

    private Moneda() {
    }

    /** Devuelve el monto con separador de miles, por ejemplo {@code $12.500}. */
    public static String formatear(long monto) {
        DecimalFormatSymbols simbolos = new DecimalFormatSymbols(new Locale.Builder().setLanguage("es").setRegion("CO").build());
        simbolos.setGroupingSeparator('.');
        String numero = new DecimalFormat("#,##0", simbolos).format(Math.abs(monto));
        return (monto < 0 ? "-$" : "$") + numero;
    }
}
