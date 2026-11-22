package com.cafeteria.descuento;

/** 15 % del subtotal si el pedido incluye al menos una bebida y un alimento. */
public class DescuentoCombo extends DescuentoPorcentual {

    public DescuentoCombo() {
        super("Combo bebida + alimento 15%", 15);
    }

    @Override
    public boolean aplicable(ContextoDescuento contexto) {
        return contexto.hayBebida() && contexto.hayAlimento();
    }

    @Override
    public String motivoNoAplicable() {
        return "El combo requiere al menos una bebida y un alimento en el pedido.";
    }
}
