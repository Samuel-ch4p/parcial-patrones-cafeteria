package com.cafeteria.pago;

/** Pago con tarjeta: el número debe tener 16 dígitos (se aceptan espacios y guiones). */
public class PagoTarjeta implements MetodoPago {

    private final String numero;

    public PagoTarjeta(String numero) {
        this.numero = numero == null ? "" : numero.replaceAll("[\\s-]", "");
    }

    @Override
    public String nombre() {
        return "Tarjeta";
    }

    @Override
    public ResultadoPago pagar(long total) {
        if (!numero.matches("\\d{16}")) {
            throw new PagoInvalidoException("Número de tarjeta inválido: debe tener 16 dígitos.");
        }
        String detalle = "Tarjeta terminada en " + numero.substring(12);
        return new ResultadoPago(nombre(), detalle, total, 0);
    }
}
