package com.cafeteria.consola;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;

/**
 * Lee y valida las entradas del usuario. Repite la pregunta ante un valor inválido
 * y solo termina si se acaba la entrada.
 *
 * @author Samuel Chaparro, Cristian Daniel Blanco, Juan Diego Afanador
 */
class LectorEntrada {

    /** Se lanza cuando ya no hay más entrada (por ejemplo, fin de un archivo redirigido). */
    static class FinDeEntradaException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        FinDeEntradaException() {
            super("Fin de la entrada.");
        }
    }

    private final BufferedReader entrada;
    private final PrintStream salida;

    LectorEntrada(BufferedReader entrada, PrintStream salida) {
        this.entrada = entrada;
        this.salida = salida;
    }

    /** Lee una línea de texto (puede estar vacía). */
    String leerTexto(String pregunta) {
        salida.print(pregunta + " ");
        salida.flush();
        try {
            String linea = entrada.readLine();
            if (linea == null) {
                throw new FinDeEntradaException();
            }
            return linea.trim();
        } catch (IOException e) {
            throw new FinDeEntradaException();
        }
    }

    /** Lee un texto que no puede estar vacío. */
    String leerTextoNoVacio(String pregunta) {
        while (true) {
            String texto = leerTexto(pregunta);
            if (!texto.isEmpty()) {
                return texto;
            }
            salida.println("  Error: este dato es obligatorio.");
        }
    }

    /** Lee un entero dentro de [min, max]; repite hasta recibir uno válido. */
    int leerEntero(String pregunta, int min, int max) {
        while (true) {
            String texto = leerTexto(pregunta);
            try {
                int valor = Integer.parseInt(texto);
                if (valor >= min && valor <= max) {
                    return valor;
                }
                salida.println("  Error: ingrese un número entre " + min + " y " + max + ".");
            } catch (NumberFormatException e) {
                salida.println("  Error: \"" + texto + "\" no es un número válido.");
            }
        }
    }

    /** Lee un monto en pesos (entero positivo, se aceptan puntos de miles). */
    long leerMonto(String pregunta) {
        while (true) {
            String texto = leerTexto(pregunta).replace(".", "").replace("$", "");
            try {
                long valor = Long.parseLong(texto);
                if (valor >= 0) {
                    return valor;
                }
                salida.println("  Error: el monto no puede ser negativo.");
            } catch (NumberFormatException e) {
                salida.println("  Error: ingrese el monto como número entero, por ejemplo 20000.");
            }
        }
    }

    /** Pregunta sí/no. */
    boolean confirmar(String pregunta) {
        while (true) {
            String texto = leerTexto(pregunta + " (s/n)").toLowerCase();
            if (texto.equals("s") || texto.equals("si") || texto.equals("sí")) {
                return true;
            }
            if (texto.equals("n") || texto.equals("no")) {
                return false;
            }
            salida.println("  Error: responda s o n.");
        }
    }
}
