package com.cafeteria.modelo;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Formato de dinero. El sistema trabaja con una sola moneda, pesos colombianos
 * (COP), sin decimales, representados como {@code long}.
 *
 * @author Samuel Chaparro, Cristian Daniel Blanco, Juan Diego Afanador
 */
public final class Moneda {

    /**
     * Monto válido: {@code $} opcional y luego solo dígitos ({@code 20000}) o
     * miles agrupados de a tres con punto ({@code 20.000}, {@code 1.250.000}).
     */
    private static final Pattern FORMATO_MONTO = Pattern.compile("\\$?(\\d{1,3}(\\.\\d{3})+|\\d+)");

    private Moneda() {
    }

    /**
     * Convierte un texto escrito por el usuario en un monto en pesos.
     * Acepta {@code 20000}, {@code 20.000} y {@code $20.000}; rechaza
     * puntos mal ubicados, signos, decimales y caracteres extra.
     *
     * @throws CafeteriaException si el texto no es un monto válido
     */
    public static long parsear(String texto) {
        String limpio = texto == null ? "" : texto.trim();
        if (!FORMATO_MONTO.matcher(limpio).matches()) {
            throw new CafeteriaException("Monto inválido: \"" + limpio
                    + "\". Escríbalo como 20000, 20.000 o $20.000.");
        }
        try {
            return Long.parseLong(limpio.replace("$", "").replace(".", ""));
        } catch (NumberFormatException e) {
            throw new CafeteriaException("Monto inválido: el valor es demasiado grande.");
        }
    }

    /** Devuelve el monto con separador de miles, por ejemplo {@code $12.500}. */
    public static String formatear(long monto) {
        DecimalFormatSymbols simbolos = new DecimalFormatSymbols(new Locale.Builder().setLanguage("es").setRegion("CO").build());
        simbolos.setGroupingSeparator('.');
        String numero = new DecimalFormat("#,##0", simbolos).format(Math.abs(monto));
        return (monto < 0 ? "-$" : "$") + numero;
    }
}
