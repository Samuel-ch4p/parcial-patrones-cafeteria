package com.cafeteria.notificacion;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

/** Receptor de auditoría: deja constancia con hora de cada cambio de estado. */
public class RegistroActividad extends ReceptorConSalida {

    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm:ss");

    public RegistroActividad(Consumer<String> salida) {
        super("registro", salida);
    }

    @Override
    protected String formatear(CambioEstado cambio) {
        String hora = LocalTime.now().format(HORA);
        if (cambio.anterior() == null) {
            return "[REGISTRO " + hora + "] Pedido #" + cambio.pedidoId() + " creado (" + cambio.nuevo() + ")";
        }
        return "[REGISTRO " + hora + "] Pedido #" + cambio.pedidoId() + " pasó de "
                + cambio.anterior() + " a " + cambio.nuevo();
    }
}
