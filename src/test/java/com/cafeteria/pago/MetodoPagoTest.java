package com.cafeteria.pago;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Patrón Strategy aplicado a los métodos de pago: validación propia de cada uno. */
class MetodoPagoTest {

    @Test
    void efectivoSuficienteCalculaElCambio() {
        ResultadoPago r = new PagoEfectivo(20000).pagar(17500);
        assertEquals(17500, r.monto());
        assertEquals(2500, r.cambio());
        assertTrue(r.detalle().contains("cambio $2.500"));
    }

    @Test
    void efectivoExactoNoTieneCambio() {
        assertEquals(0, new PagoEfectivo(5000).pagar(5000).cambio());
    }

    @Test
    void efectivoInsuficienteSeRechaza() {
        PagoInvalidoException e = assertThrows(PagoInvalidoException.class, () -> new PagoEfectivo(4000).pagar(5000));
        assertTrue(e.getMessage().contains("$1.000"));
    }

    @Test
    void tarjetaAceptaDieciseisDigitosConEspaciosOGuiones() {
        assertTrue(new PagoTarjeta("4111 1111 1111 1234").pagar(1000).detalle().endsWith("1234"));
        assertTrue(new PagoTarjeta("4111-1111-1111-5678").pagar(1000).detalle().endsWith("5678"));
    }

    @Test
    void tarjetaConFormatoInvalidoSeRechaza() {
        assertThrows(PagoInvalidoException.class, () -> new PagoTarjeta("1234").pagar(1000));
        assertThrows(PagoInvalidoException.class, () -> new PagoTarjeta("4111 1111 1111 12ab").pagar(1000));
        assertThrows(PagoInvalidoException.class, () -> new PagoTarjeta(null).pagar(1000));
    }

    @Test
    void billeteraRequiereIdentificadorValido() {
        assertEquals("Billetera digital", new PagoBilletera("3001234567").pagar(1000).metodo());
        assertThrows(PagoInvalidoException.class, () -> new PagoBilletera("").pagar(1000));
        assertThrows(PagoInvalidoException.class, () -> new PagoBilletera("ab").pagar(1000));
        assertThrows(PagoInvalidoException.class, () -> new PagoBilletera("no valido!").pagar(1000));
    }
}
