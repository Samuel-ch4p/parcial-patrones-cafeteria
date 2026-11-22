package com.cafeteria;

import com.cafeteria.consola.AplicacionConsola;
import com.cafeteria.gui.AplicacionGui;

/**
 * Punto de entrada. Modos: {@code consola} (por defecto) o {@code gui}.
 *
 * @author Samuel Chaparro, Cristian Daniel Blanco, Juan Diego Afanador
 */
public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        String modo = args.length == 0 ? "consola" : args[0].trim().toLowerCase();
        switch (modo) {
            case "consola" -> AplicacionConsola.iniciar();
            case "gui" -> AplicacionGui.iniciar();
            default -> {
                System.err.println("Modo desconocido: \"" + args[0] + "\". Use \"consola\" o \"gui\".");
                System.exit(1);
            }
        }
    }
}
