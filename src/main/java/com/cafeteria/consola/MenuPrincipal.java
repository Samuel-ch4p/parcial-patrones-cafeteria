package com.cafeteria.consola;

import com.cafeteria.modelo.CafeteriaException;
import com.cafeteria.modelo.TipoEstado;
import com.cafeteria.pedido.Pedido;
import com.cafeteria.producto.Producto;
import com.cafeteria.servicio.CafeteriaService;
import java.io.PrintStream;
import java.util.List;

/**
 * Menú principal de la consola: catálogo, pedidos, historial y receptores.
 * Solo invoca operaciones de {@link CafeteriaService}.
 *
 * @author Samuel Chaparro, Cristian Daniel Blanco, Juan Diego Afanador
 */
class MenuPrincipal {

    private final CafeteriaService servicio;
    private final LectorEntrada lector;
    private final PrintStream salida;
    private final MenuPedido menuPedido;

    MenuPrincipal(CafeteriaService servicio, LectorEntrada lector, PrintStream salida) {
        this.servicio = servicio;
        this.lector = lector;
        this.salida = salida;
        this.menuPedido = new MenuPedido(servicio, lector, salida);
    }

    void mostrar() {
        salida.println("==========================================");
        salida.println("        SISTEMA DE CAFETERIA");
        salida.println("==========================================");
        boolean salir = false;
        while (!salir) {
            salida.println();
            salida.println("--- MENU PRINCIPAL ---");
            salida.println("1. Ver catálogo");
            salida.println("2. Ver detalle de un producto");
            salida.println("3. Nuevo pedido");
            salida.println("4. Continuar un pedido existente");
            salida.println("5. Historial de pedidos");
            salida.println("6. Receptores de notificación");
            salida.println("7. Registro de actividad");
            salida.println("0. Salir");
            int opcion = lector.leerEntero("Elija una opción:", 0, 7);
            try {
                switch (opcion) {
                    case 1 -> salida.print(Presentador.catalogo(servicio.catalogoPorCategoria()));
                    case 2 -> detalleProducto();
                    case 3 -> nuevoPedido();
                    case 4 -> continuarPedido();
                    case 5 -> historial();
                    case 6 -> receptores();
                    case 7 -> registro();
                    default -> salir = true;
                }
            } catch (CafeteriaException e) {
                salida.println("  Error: " + e.getMessage());
            }
        }
        salida.println("Hasta pronto.");
    }

    private void detalleProducto() {
        List<Producto> productos = servicio.catalogo();
        Producto elegido = MenuPedido.elegirProducto(productos, lector, salida);
        if (elegido != null) {
            salida.println(Presentador.detalle(servicio.obtenerProducto(elegido.codigo())));
        }
    }

    private void nuevoPedido() {
        boolean frecuente = lector.confirmar("¿El cliente es frecuente?");
        String nombre = frecuente
                ? lector.leerTextoNoVacio("Nombre del cliente:")
                : lector.leerTexto("Nombre del cliente (Enter para anónimo):");
        Pedido pedido = servicio.crearPedido(nombre, frecuente);
        salida.println("  Pedido #" + pedido.id() + " creado para " + pedido.cliente().nombreVisible() + ".");
        menuPedido.mostrar(pedido.id());
    }

    private void continuarPedido() {
        List<Pedido> pedidos = servicio.listarPedidos();
        if (pedidos.isEmpty()) {
            salida.println("  Aún no hay pedidos. Cree uno con la opción 3.");
            return;
        }
        pedidos.forEach(p -> salida.println(Presentador.resumenLinea(p)));
        int numero = lector.leerEntero("Número de pedido (0 para volver):", 0, Integer.MAX_VALUE);
        if (numero != 0) {
            servicio.obtenerPedido(numero);
            menuPedido.mostrar(numero);
        }
    }

    private void historial() {
        salida.println("Filtrar por estado:");
        salida.println("  0. Todos");
        TipoEstado[] estados = TipoEstado.values();
        for (int i = 0; i < estados.length; i++) {
            salida.println("  " + (i + 1) + ". " + estados[i].etiqueta());
        }
        int opcion = lector.leerEntero("Elija una opción:", 0, estados.length);
        List<Pedido> pedidos = opcion == 0 ? servicio.listarPedidos() : servicio.listarPedidos(estados[opcion - 1]);
        if (pedidos.isEmpty()) {
            salida.println("  No hay pedidos para ese filtro.");
            return;
        }
        pedidos.forEach(p -> salida.println(Presentador.resumenLinea(p)));
    }

    private void receptores() {
        boolean volver = false;
        while (!volver) {
            salida.println("--- RECEPTORES DE NOTIFICACION ---");
            salida.println("  Activos:   " + texto(servicio.receptoresActivos()));
            salida.println("  Inactivos: " + texto(servicio.receptoresInactivos()));
            salida.println("1. Quitar un receptor");
            salida.println("2. Agregar un receptor");
            salida.println("0. Volver");
            int opcion = lector.leerEntero("Elija una opción:", 0, 2);
            try {
                switch (opcion) {
                    case 1 -> {
                        String nombre = elegirNombre(servicio.receptoresActivos(), "Receptor a quitar");
                        if (nombre != null) {
                            servicio.quitarReceptor(nombre);
                            salida.println("  Receptor \"" + nombre + "\" desactivado.");
                        }
                    }
                    case 2 -> {
                        String nombre = elegirNombre(servicio.receptoresInactivos(), "Receptor a agregar");
                        if (nombre != null) {
                            servicio.agregarReceptor(nombre);
                            salida.println("  Receptor \"" + nombre + "\" activado.");
                        }
                    }
                    default -> volver = true;
                }
            } catch (CafeteriaException e) {
                salida.println("  Error: " + e.getMessage());
            }
        }
    }

    private String elegirNombre(List<String> nombres, String pregunta) {
        if (nombres.isEmpty()) {
            salida.println("  No hay receptores en esa lista.");
            return null;
        }
        for (int i = 0; i < nombres.size(); i++) {
            salida.println("  " + (i + 1) + ". " + nombres.get(i));
        }
        int opcion = lector.leerEntero(pregunta + " (0 para cancelar):", 0, nombres.size());
        return opcion == 0 ? null : nombres.get(opcion - 1);
    }

    private void registro() {
        List<String> mensajes = servicio.registroActividad();
        if (mensajes.isEmpty()) {
            salida.println("  El registro está vacío.");
        }
        mensajes.forEach(m -> salida.println("  " + m));
    }

    private static String texto(List<String> nombres) {
        return nombres.isEmpty() ? "(ninguno)" : String.join(", ", nombres);
    }
}
