package com.cafeteria.producto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.cafeteria.modelo.CafeteriaException;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Patrón Factory Method: las fábricas crean los productos y el catálogo solo conoce la interfaz. */
class FabricaProductoTest {

    private CatalogoProductos catalogo;

    @BeforeEach
    void preparar() {
        catalogo = new CatalogoProductos();
        catalogo.registrar(new FabricaCafes());
        catalogo.registrar(new FabricaTes());
        catalogo.registrar(new FabricaInfusiones());
        catalogo.registrar(new FabricaBebidasFrias());
        catalogo.registrar(new FabricaPanaderia());
        catalogo.registrar(new FabricaPostres());
        catalogo.registrar(new FabricaSandwiches());
    }

    @Test
    void catalogoTieneAlMenosSeisBebidasYCincoAlimentos() {
        long bebidas = catalogo.todos().stream().filter(p -> p.categoria().esBebida()).count();
        long alimentos = catalogo.todos().stream().filter(p -> !p.categoria().esBebida()).count();
        assertTrue(bebidas >= 6, "bebidas: " + bebidas);
        assertTrue(alimentos >= 5, "alimentos: " + alimentos);
    }

    @Test
    void agrupaPorCategoriaConLasSieteCategorias() {
        Map<Categoria, List<Producto>> grupos = catalogo.porCategoria();
        assertEquals(Categoria.values().length, grupos.size());
    }

    @Test
    void cadaFabricaCreaProductosDeSuCategoria() {
        List<Producto> cafes = new FabricaCafes().crearProductos();
        assertTrue(cafes.stream().allMatch(p -> p.categoria() == Categoria.CAFE));
        assertTrue(new FabricaSandwiches().crearProductos().stream().noneMatch(p -> p.categoria().esBebida()));
    }

    @Test
    void buscaPorCodigoSinImportarMayusculas() {
        Producto producto = catalogo.buscar("  CAPUCHINO ");
        assertEquals("Capuchino", producto.nombre());
        assertEquals(7500, producto.precioBase());
        assertEquals(producto.precioBase(), producto.precio());
        assertSame(producto, catalogo.buscar("capuchino"));
    }

    @Test
    void productoInexistenteLanzaExcepcionClara() {
        ProductoNoEncontradoException e =
                assertThrows(ProductoNoEncontradoException.class, () -> catalogo.buscar("pizza"));
        assertTrue(e.getMessage().contains("pizza"));
    }

    @Test
    void agregarProductoNuevoNoRequiereTocarCodigoExistente() {
        class Chai extends Bebida {
            Chai() {
                super("chai", "Chai latte", Categoria.TE, 7000, "Té especiado con leche.");
            }
        }
        catalogo.registrar(() -> List.of(new Chai()));
        assertEquals(7000, catalogo.buscar("chai").precio());
    }

    @Test
    void rechazaCodigosRepetidos() {
        assertThrows(CafeteriaException.class, () -> catalogo.registrar(new FabricaCafes()));
    }

    @Test
    void unProductoConCodigoEnMayusculasSeEncuentra() {
        class Mocaccino extends Bebida {
            Mocaccino() {
                super(" MOCA ", "Mocaccino", Categoria.CAFE, 8000, "Café con chocolate.");
            }
        }
        catalogo.registrar(() -> List.of(new Mocaccino()));
        assertEquals(8000, catalogo.buscar("moca").precio());
        assertEquals(8000, catalogo.buscar("MOCA").precio());
    }

    @Test
    void rechazaCodigosRepetidosAunqueCambienMayusculas() {
        class EspressoDoble extends Bebida {
            EspressoDoble() {
                super("ESPRESSO", "Espresso doble", Categoria.CAFE, 6000, "");
            }
        }
        assertThrows(CafeteriaException.class, () -> catalogo.registrar(() -> List.of(new EspressoDoble())));
    }

    @Test
    void unAlimentoNoPuedeTenerCategoriaDeBebida() {
        assertThrows(CafeteriaException.class, () -> new Alimento("x", "X", Categoria.CAFE, 1000, "") { });
    }
}
