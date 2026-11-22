package com.cafeteria.descuento;

/** Política por defecto: no descuenta nada. */
public class SinDescuento implements PoliticaDescuento {

    @Override
    public String nombre() {
        return "Sin descuento";
    }

    @Override
    public long calcular(ContextoDescuento contexto) {
        return 0;
    }
}
