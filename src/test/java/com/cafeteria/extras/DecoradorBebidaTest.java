package com.cafeteria.extras;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.cafeteria.producto.Croissant;
import com.cafeteria.producto.Latte;
import com.cafeteria.producto.Producto;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Patrón Decorator y cálculo de precios de bebidas personalizadas. */
class DecoradorBebidaTest {

    private final Personalizador personalizador = new Personalizador(new CatalogoOpciones());

    @Test
    void unDecoradorSumaRecargoYAgregaDescripcion() {
        Producto grande = new TamanoGrande(new Latte());
        assertEquals(8000 + 2000, grande.precio());
        assertEquals(8000, grande.precioBase());
        assertEquals("Latte (Grande)", grande.descripcion());
    }

    @Test
    void losDecoradoresSeApilanLibremente() {
        Producto bebida = new Canela(new ShotEspresso(new LecheAvena(new TamanoMediano(new Latte()))));
        assertEquals(8000 + 1000 + 1500 + 2000 + 500, bebida.precio());
        assertEquals(List.of("Mediano", "Leche de avena", "Shot de espresso", "Canela"), bebida.personalizaciones());
        assertEquals("Latte (Mediano, Leche de avena, Shot de espresso, Canela)", bebida.descripcion());
    }

    @Test
    void elOrdenNoCambiaElPrecio() {
        Producto a = new Crema(new Jarabe(new Latte()));
        Producto b = new Jarabe(new Crema(new Latte()));
        assertEquals(a.precio(), b.precio());
    }

    @Test
    void sePuedeRepetirUnExtra() {
        Producto dobleShot = new ShotEspresso(new ShotEspresso(new Latte()));
        assertEquals(8000 + 4000, dobleShot.precio());
    }

    @Test
    void noSePuedeDecorarUnAlimento() {
        assertThrows(PersonalizacionInvalidaException.class, () -> new Crema(new Croissant()));
    }

    @Test
    void elPersonalizadorArmaTamanoLecheYExtras() {
        Producto bebida = personalizador.personalizar(new Latte(), "grande", "almendras", List.of("shot", "canela"));
        assertEquals(8000 + 2000 + 1500 + 2000 + 500, bebida.precio());
        assertTrue(bebida.descripcion().startsWith("Latte (Grande, Leche de almendras"));
    }

    @Test
    void sinOpcionesDevuelveLaMismaBebida() {
        Producto latte = new Latte();
        assertEquals(latte, personalizador.personalizar(latte, null, " ", List.of()));
    }

    @Test
    void opcionInexistenteOEnGrupoEquivocadoSeRechaza() {
        assertThrows(PersonalizacionInvalidaException.class,
                () -> personalizador.personalizar(new Latte(), "gigante", null, null));
        assertThrows(PersonalizacionInvalidaException.class,
                () -> personalizador.personalizar(new Latte(), null, "shot", null));
    }

    @Test
    void elCatalogoDeOpcionesCalculaElRecargoDeCadaDecorador() {
        CatalogoOpciones opciones = new CatalogoOpciones();
        assertEquals(2000, opciones.buscar(TipoOpcion.EXTRA, "shot").recargo());
        assertEquals(0, opciones.buscar(TipoOpcion.TAMANO, "pequeno").recargo());
        assertEquals(4, opciones.opciones(TipoOpcion.EXTRA).size());
    }

    @Test
    void unExtraNuevoSeRegistraSinTocarLosExistentes() {
        class Vainilla extends DecoradorBebida {
            Vainilla(Producto p) {
                super(p, "Vainilla", 1200);
            }
        }
        CatalogoOpciones opciones = new CatalogoOpciones();
        opciones.registrar("vainilla", "Vainilla", TipoOpcion.EXTRA, Vainilla::new);
        Producto bebida = new Personalizador(opciones).personalizar(new Latte(), null, null, List.of("vainilla"));
        assertEquals(9200, bebida.precio());
    }
}
