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
    void parseaMontosValidos() {
        assertEquals(20000, Moneda.parsear("20000"));
        assertEquals(20000, Moneda.parsear("20.000"));
        assertEquals(20000, Moneda.parsear("$20.000"));
        assertEquals(20000, Moneda.parsear("  $20000  "));
        assertEquals(1250000, Moneda.parsear("1.250.000"));
        assertEquals(950, Moneda.parsear("950"));
        assertEquals(0, Moneda.parsear("0"));
    }

    @Test
    void rechazaMontosInvalidos() {
        for (String texto : new String[] {"20..000", "-20000", "20.000abc", "200.00", "2.0.0.0.0",
                "20.0000", ".20000", "20000.", "$", "", "   ", "abc", "+20000", "20 000",
                "99999999999999999999"}) {
            assertThrows(CafeteriaException.class, () -> Moneda.parsear(texto), texto);
        }
        assertThrows(CafeteriaException.class, () -> Moneda.parsear(null));
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
