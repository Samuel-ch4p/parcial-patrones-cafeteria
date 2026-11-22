package com.cafeteria.notificacion;

import java.util.function.Consumer;

/** Receptor del cliente: mensaje simulado, sin envío real. */
public class NotificacionCliente extends ReceptorConSalida {

    public NotificacionCliente(Consumer<String> salida) {
        super("cliente", salida);
    }

    @Override
    protected String formatear(CambioEstado cambio) {
        return "[CLIENTE] " + cambio.cliente() + ", su pedido #" + cambio.pedidoId()
                + " está: " + cambio.nuevo();
    }
}
