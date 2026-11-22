package com.cafeteria.notificacion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * Base de los receptores que dan formato a un mensaje, lo guardan y lo envían a una
 * salida (consola, panel de la interfaz o nada).
 *
 * @author Samuel Chaparro, Cristian Daniel Blanco, Juan Diego Afanador
 */
public abstract class ReceptorConSalida implements ReceptorEstado {

    private final String nombre;
    private final Consumer<String> salida;
    private final List<String> mensajes = new ArrayList<>();

    protected ReceptorConSalida(String nombre, Consumer<String> salida) {
        this.nombre = nombre;
        this.salida = salida == null ? m -> { } : salida;
    }

    @Override
    public String nombre() {
        return nombre;
    }

    @Override
    public final void actualizar(CambioEstado cambio) {
        String mensaje = formatear(cambio);
        mensajes.add(mensaje);
        salida.accept(mensaje);
    }

    /** Mensajes recibidos hasta ahora, del más antiguo al más reciente. */
    public List<String> mensajes() {
        return Collections.unmodifiableList(new ArrayList<>(mensajes));
    }

    protected abstract String formatear(CambioEstado cambio);
}
