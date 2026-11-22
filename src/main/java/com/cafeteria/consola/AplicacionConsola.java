package com.cafeteria.consola;

import com.cafeteria.servicio.CafeteriaService;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;

/**
 * Arranque de la interfaz de consola. Crea la fachada, la conecta con la salida
 * de notificaciones y abre el menú principal.
 *
 * @author Samuel Chaparro, Cristian Daniel Blanco, Juan Diego Afanador
 */
public final class AplicacionConsola {

    private AplicacionConsola() {
    }

    /** Ejecuta la consola con la entrada y salida estándar. */
    public static void iniciar() {
        ejecutar(new BufferedReader(new InputStreamReader(System.in)), System.out);
    }

    /** Ejecuta la consola con entrada y salida dadas (útil para pruebas con entrada simulada). */
    public static void ejecutar(BufferedReader entrada, PrintStream salida) {
        CafeteriaService servicio = new CafeteriaService(mensaje -> salida.println("  >> " + mensaje));
        LectorEntrada lector = new LectorEntrada(entrada, salida);
        MenuPrincipal menu = new MenuPrincipal(servicio, lector, salida);
        try {
            menu.mostrar();
        } catch (LectorEntrada.FinDeEntradaException e) {
            salida.println();
            salida.println("Fin de la entrada. Hasta pronto.");
        }
    }
}
