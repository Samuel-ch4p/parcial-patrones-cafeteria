package com.cafeteria.notificacion;

import com.cafeteria.modelo.CafeteriaException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Sujeto del patrón Observer. Mantiene los receptores activos y les avisa de cada
 * cambio. Los pedidos solo conocen esta clase, no a los receptores concretos.
 *
 * @author Samuel Chaparro, Cristian Daniel Blanco, Juan Diego Afanador
 */
public class Notificador {

    private final List<ReceptorEstado> receptores = new CopyOnWriteArrayList<>();

    /** Suscribe un receptor. Falla si ya hay uno activo con el mismo nombre. */
    public void agregar(ReceptorEstado receptor) {
        if (activo(receptor.nombre())) {
            throw new CafeteriaException("El receptor \"" + receptor.nombre() + "\" ya está activo.");
        }
        receptores.add(receptor);
    }

    /** Quita el receptor con ese nombre. Falla si no estaba activo. */
    public void quitar(String nombre) {
        boolean quitado = receptores.removeIf(r -> r.nombre().equalsIgnoreCase(nombre));
        if (!quitado) {
            throw new CafeteriaException("No hay un receptor activo llamado \"" + nombre + "\".");
        }
    }

    public boolean activo(String nombre) {
        return receptores.stream().anyMatch(r -> r.nombre().equalsIgnoreCase(nombre));
    }

    /** Nombres de los receptores activos, en orden de registro. */
    public List<String> nombres() {
        return receptores.stream().map(ReceptorEstado::nombre).toList();
    }

    /** Avisa a todos los receptores activos. */
    public void notificar(CambioEstado cambio) {
        for (ReceptorEstado receptor : receptores) {
            receptor.actualizar(cambio);
        }
    }
}
