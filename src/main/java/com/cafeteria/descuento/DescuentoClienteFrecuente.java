package com.cafeteria.descuento;

/** 10 % del subtotal para clientes frecuentes. */
public class DescuentoClienteFrecuente extends DescuentoPorcentual {

    public DescuentoClienteFrecuente() {
        super("Cliente frecuente 10%", 10);
    }

    @Override
    public boolean aplicable(ContextoDescuento contexto) {
        return contexto.clienteFrecuente();
    }

    @Override
    public String motivoNoAplicable() {
        return "Este descuento es solo para clientes frecuentes.";
    }
}
