package com.cafeteria.extras;

import com.cafeteria.producto.Bebida;
import com.cafeteria.producto.Categoria;
import com.cafeteria.producto.Producto;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

/**
 * Registro de las opciones de personalización disponibles. Una opción nueva es una
 * clase decoradora más una llamada a {@link #registrar}.
 *
 * @author Samuel Chaparro, Cristian Daniel Blanco, Juan Diego Afanador
 */
public class CatalogoOpciones {

    /** Bebida de prueba, usada solo para calcular el recargo de cada decorador. */
    private static final class Sonda extends Bebida {
        Sonda() {
            super("sonda", "Sonda", Categoria.CAFE, 0, "");
        }
    }

    private static final Producto SONDA = new Sonda();

    private final Map<String, OpcionPersonalizacion> opciones = new LinkedHashMap<>();

    public CatalogoOpciones() {
        registrar("pequeno", "Pequeño", TipoOpcion.TAMANO, TamanoPequeno::new);
        registrar("mediano", "Mediano", TipoOpcion.TAMANO, TamanoMediano::new);
        registrar("grande", "Grande", TipoOpcion.TAMANO, TamanoGrande::new);
        registrar("entera", "Leche entera", TipoOpcion.LECHE, LecheEntera::new);
        registrar("deslactosada", "Leche deslactosada", TipoOpcion.LECHE, LecheDeslactosada::new);
        registrar("almendras", "Leche de almendras", TipoOpcion.LECHE, LecheAlmendras::new);
        registrar("avena", "Leche de avena", TipoOpcion.LECHE, LecheAvena::new);
        registrar("shot", "Shot de espresso", TipoOpcion.EXTRA, ShotEspresso::new);
        registrar("crema", "Crema", TipoOpcion.EXTRA, Crema::new);
        registrar("jarabe", "Jarabe", TipoOpcion.EXTRA, Jarabe::new);
        registrar("canela", "Canela", TipoOpcion.EXTRA, Canela::new);
    }

    /** Registra una opción nueva; el recargo se calcula a partir del propio decorador. */
    public void registrar(String codigo, String nombre, TipoOpcion tipo, UnaryOperator<Producto> aplicador) {
        if (codigo == null || codigo.isBlank()) {
            throw new PersonalizacionInvalidaException("La opción necesita un código.");
        }
        String clave = codigo.trim().toLowerCase();
        if (opciones.containsKey(clave)) {
            throw new PersonalizacionInvalidaException("Ya existe la opción " + clave);
        }
        long recargo = aplicador.apply(SONDA).precio() - SONDA.precio();
        opciones.put(clave, new OpcionPersonalizacion(clave, nombre, tipo, recargo, aplicador));
    }

    public List<OpcionPersonalizacion> opciones(TipoOpcion tipo) {
        List<OpcionPersonalizacion> resultado = new ArrayList<>();
        for (OpcionPersonalizacion opcion : opciones.values()) {
            if (opcion.tipo() == tipo) {
                resultado.add(opcion);
            }
        }
        return resultado;
    }

    /** Busca una opción del tipo indicado; falla si no existe o es de otro tipo. */
    public OpcionPersonalizacion buscar(TipoOpcion tipo, String codigo) {
        OpcionPersonalizacion opcion = codigo == null ? null : opciones.get(codigo.trim().toLowerCase());
        if (opcion == null || opcion.tipo() != tipo) {
            throw new PersonalizacionInvalidaException(
                    "No existe la opción \"" + codigo + "\" en " + tipo.etiqueta().toLowerCase() + ".");
        }
        return opcion;
    }
}
