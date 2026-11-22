package com.cafeteria.modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class MonedaYClienteTest {

    @Test
    void formateaPesosConSeparadorDeMiles() {
        assertEquals("$0", Moneda.formatear(0));
        assertEquals("$950", Moneda.formatear(950));
        assertEquals("$12.500", Moneda.formatear(12500));
        assertEquals("$1.250.000", Moneda.formatear(1250000));
        assertEquals("-$1.500", Moneda.formatear(-1500));
    }

    @Test
    void clienteAnonimoYFrecuente() {
        assertEquals("Cliente anónimo", Cliente.anonimo().nombreVisible());
        assertEquals("Ana (frecuente)", Cliente.frecuente(" Ana ").nombreVisible());
        assertEquals("Luis", new Cliente("Luis", false).nombreVisible());
    }

    @Test
    void clienteFrecuenteNecesitaNombre() {
        assertThrows(CafeteriaException.class, () -> Cliente.frecuente("  "));
    }
}
