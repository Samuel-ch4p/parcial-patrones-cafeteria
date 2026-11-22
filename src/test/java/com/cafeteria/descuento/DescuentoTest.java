package com.cafeteria.descuento;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Patrón Strategy aplicado a los descuentos. */
class DescuentoTest {

    private static ContextoDescuento ctx(boolean frecuente, long subtotal, boolean bebida, boolean alimento) {
        return new ContextoDescuento(frecuente, subtotal, bebida, alimento);
    }

    @Test
    void sinDescuentoNoDescuentaNada() {
        assertEquals(0, new SinDescuento().calcular(ctx(true, 20000, true, true)));
    }

    @Test
    void clienteFrecuenteDescuentaDiezPorCiento() {
        assertEquals(2000, new DescuentoClienteFrecuente().calcular(ctx(true, 20000, true, false)));
    }

    @Test
    void clienteAnonimoNoPuedeUsarElDescuentoFrecuente() {
        PoliticaDescuento politica = new DescuentoClienteFrecuente();
        ContextoDescuento anonimo = ctx(false, 20000, true, false);
        assertFalse(politica.aplicable(anonimo));
        assertEquals(0, politica.calcular(anonimo));
        assertThrows(DescuentoInvalidoException.class, () -> politica.validar(anonimo));
    }

    @Test
    void comboRequiereBebidaYAlimento() {
        PoliticaDescuento combo = new DescuentoCombo();
        assertEquals(1500, combo.calcular(ctx(false, 10000, true, true)));
        assertFalse(combo.aplicable(ctx(false, 10000, true, false)));
        assertFalse(combo.aplicable(ctx(false, 10000, false, true)));
        assertEquals(0, combo.calcular(ctx(false, 10000, true, false)));
    }

    @Test
    void codigoPromocionalValidoDescuentaSuPorcentaje() {
        PoliticaDescuento politica = new DescuentoCodigoPromocional(" cafe10 ");
        assertEquals(1000, politica.calcular(ctx(false, 10000, true, false)));
        assertTrue(politica.nombre().contains("CAFE10"));
        assertEquals(2000, new DescuentoCodigoPromocional("NOVIEMBRE20").calcular(ctx(false, 10000, true, false)));
    }

    @Test
    void codigoPromocionalInvalidoSeRechaza() {
        DescuentoInvalidoException e = assertThrows(DescuentoInvalidoException.class,
                () -> new DescuentoCodigoPromocional("INVENTADO"));
        assertTrue(e.getMessage().contains("INVENTADO"));
        assertThrows(DescuentoInvalidoException.class, () -> new DescuentoCodigoPromocional(null));
    }
}
