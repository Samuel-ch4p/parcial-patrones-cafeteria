package com.cafeteria.notificacion;

import java.util.function.Consumer;

/** Receptor de la cocina: muestra qué pedido entra y en qué estado queda. */
public class PantallaCocina extends ReceptorConSalida {

    public PantallaCocina(Consumer<String> salida) {
        super("cocina", salida);
    }

    @Override
    protected String formatear(CambioEstado cambio) {
        if (cambio.anterior() == null) {
            return "[COCINA] Nuevo pedido #" + cambio.pedidoId();
        }
        return "[COCINA] Pedido #" + cambio.pedidoId() + ": " + cambio.anterior() + " -> " + cambio.nuevo();
    }
}
