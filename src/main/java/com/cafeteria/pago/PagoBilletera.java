package com.cafeteria.pago;

/** Pago con billetera digital: requiere un identificador de 4 a 30 caracteres (letras, números, . _ @ -). */
public class PagoBilletera implements MetodoPago {

    private final String identificador;

    public PagoBilletera(String identificador) {
        this.identificador = identificador == null ? "" : identificador.trim();
    }

    @Override
    public String nombre() {
        return "Billetera digital";
    }

    @Override
    public ResultadoPago pagar(long total) {
        if (!identificador.matches("[A-Za-z0-9._@-]{4,30}")) {
            throw new PagoInvalidoException(
                    "Identificador de billetera inválido: use de 4 a 30 letras, números o . _ @ -");
        }
        return new ResultadoPago(nombre(), "Billetera digital (" + identificador + ")", total, 0);
    }
}
