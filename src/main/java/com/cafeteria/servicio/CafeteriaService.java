package com.cafeteria.servicio;

import com.cafeteria.descuento.DescuentoClienteFrecuente;
import com.cafeteria.descuento.DescuentoCodigoPromocional;
import com.cafeteria.descuento.DescuentoCombo;
import com.cafeteria.descuento.SinDescuento;
import com.cafeteria.extras.CatalogoOpciones;
import com.cafeteria.extras.OpcionPersonalizacion;
import com.cafeteria.extras.Personalizador;
import com.cafeteria.extras.TipoOpcion;
import com.cafeteria.modelo.CafeteriaException;
import com.cafeteria.modelo.Cliente;
import com.cafeteria.modelo.TipoEstado;
import com.cafeteria.notificacion.NotificacionCliente;
import com.cafeteria.notificacion.Notificador;
import com.cafeteria.notificacion.PantallaCocina;
import com.cafeteria.notificacion.ReceptorEstado;
import com.cafeteria.notificacion.RegistroActividad;
import com.cafeteria.pago.MetodoPago;
import com.cafeteria.pago.PagoBilletera;
import com.cafeteria.pago.PagoEfectivo;
import com.cafeteria.pago.PagoTarjeta;
import com.cafeteria.pago.ResultadoPago;
import com.cafeteria.pedido.Pedido;
import com.cafeteria.pedido.PedidoException;
import com.cafeteria.producto.CatalogoProductos;
import com.cafeteria.producto.Categoria;
import com.cafeteria.producto.FabricaBebidasFrias;
import com.cafeteria.producto.FabricaCafes;
import com.cafeteria.producto.FabricaInfusiones;
import com.cafeteria.producto.FabricaPanaderia;
import com.cafeteria.producto.FabricaPostres;
import com.cafeteria.producto.FabricaProducto;
import com.cafeteria.producto.FabricaSandwiches;
import com.cafeteria.producto.FabricaTes;
import com.cafeteria.producto.Producto;
import com.cafeteria.ticket.FormatoTicket;
import com.cafeteria.ticket.TicketResumido;
import com.cafeteria.ticket.TicketTextoPlano;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * Fachada del sistema (patrón Facade). Es el único punto de entrada para la consola
 * y la interfaz gráfica: expone las operaciones RF-01 a RF-16 y oculta cómo se
 * coordinan catálogo, decoradores, descuentos, pagos, estados, notificaciones y tickets.
 *
 * @author Samuel Chaparro, Cristian Daniel Blanco, Juan Diego Afanador
 */
public class CafeteriaService {

    private final CatalogoProductos catalogo = new CatalogoProductos();
    private final CatalogoOpciones opciones = new CatalogoOpciones();
    private final Personalizador personalizador = new Personalizador(opciones);
    private final Notificador notificador = new Notificador();
    private final Map<String, ReceptorEstado> receptoresConocidos = new LinkedHashMap<>();
    private final Map<String, FormatoTicket> formatos = new LinkedHashMap<>();
    private final Map<Integer, Pedido> pedidos = new LinkedHashMap<>();
    private final AtomicInteger secuencia = new AtomicInteger();
    private final RegistroActividad registro;

    public CafeteriaService() {
        this(null);
    }

    /**
     * @param salidaNotificaciones recibe cada mensaje que generan cocina, cliente y
     *                             registro (por ejemplo, la consola o un panel); puede ser {@code null}
     */
    public CafeteriaService(Consumer<String> salidaNotificaciones) {
        registrarFabrica(new FabricaCafes());
        registrarFabrica(new FabricaTes());
        registrarFabrica(new FabricaInfusiones());
        registrarFabrica(new FabricaBebidasFrias());
        registrarFabrica(new FabricaPanaderia());
        registrarFabrica(new FabricaPostres());
        registrarFabrica(new FabricaSandwiches());

        registrarFormato(new TicketTextoPlano());
        registrarFormato(new TicketResumido());

        registro = new RegistroActividad(salidaNotificaciones);
        registrarReceptor(new PantallaCocina(salidaNotificaciones));
        registrarReceptor(new NotificacionCliente(salidaNotificaciones));
        registrarReceptor(registro);
    }

    // ===== Puntos de extensión =====

    /** Agrega al catálogo los productos de una fábrica nueva. */
    public void registrarFabrica(FabricaProducto fabrica) {
        catalogo.registrar(fabrica);
    }

    /** Agrega un formato de ticket nuevo. */
    public void registrarFormato(FormatoTicket formato) {
        formatos.put(formato.nombre().toLowerCase(), formato);
    }

    /** Agrega un receptor nuevo y lo deja activo. */
    public void registrarReceptor(ReceptorEstado receptor) {
        receptoresConocidos.put(receptor.nombre().toLowerCase(), receptor);
        notificador.agregar(receptor);
    }

    // ===== Catálogo (RF-01, RF-02) =====

    /** RF-01: productos agrupados por categoría. */
    public Map<Categoria, List<Producto>> catalogoPorCategoria() {
        return catalogo.porCategoria();
    }

    public List<Producto> catalogo() {
        return catalogo.todos();
    }

    /** RF-02: detalle de un producto. */
    public Producto obtenerProducto(String codigo) {
        return catalogo.buscar(codigo);
    }

    // ===== Personalización (RF-03, RF-04) =====

    /** Opciones disponibles de un grupo: tamaños, leches o extras. */
    public List<OpcionPersonalizacion> opciones(TipoOpcion tipo) {
        return opciones.opciones(tipo);
    }

    /** RF-03/RF-04: calcula cómo queda una bebida personalizada, sin agregarla a ningún pedido. */
    public Producto previsualizar(String codigo, String tamano, String leche, List<String> extras) {
        return personalizador.personalizar(catalogo.buscar(codigo), tamano, leche, extras);
    }

    // ===== Pedidos (RF-05, RF-06) =====

    /** RF-05: crea un pedido para un cliente anónimo (nombre vacío) o frecuente. */
    public Pedido crearPedido(String nombreCliente, boolean frecuente) {
        Cliente cliente = frecuente ? Cliente.frecuente(nombreCliente) : new Cliente(nombreCliente, false);
        Pedido pedido = new Pedido(secuencia.incrementAndGet(), cliente, notificador);
        pedidos.put(pedido.id(), pedido);
        return pedido;
    }

    public Pedido crearPedidoAnonimo() {
        return crearPedido("", false);
    }

    public Pedido obtenerPedido(int pedidoId) {
        Pedido pedido = pedidos.get(pedidoId);
        if (pedido == null) {
            throw new PedidoException("No existe el pedido #" + pedidoId + ".");
        }
        return pedido;
    }

    /**
     * RF-05: agrega un producto al pedido. Para una bebida se pueden indicar tamaño,
     * leche y extras (códigos de {@link #opciones}); para un alimento, deben ir vacíos.
     */
    public void agregarItem(int pedidoId, String codigo, int cantidad,
                            String tamano, String leche, List<String> extras) {
        Producto producto = previsualizar(codigo, tamano, leche, extras);
        obtenerPedido(pedidoId).agregar(producto, cantidad);
    }

    /** RF-05: agrega un producto sin personalizar. */
    public void agregarItem(int pedidoId, String codigo, int cantidad) {
        agregarItem(pedidoId, codigo, cantidad, null, null, List.of());
    }

    /** RF-05: quita un ítem (índice base 0). */
    public void quitarItem(int pedidoId, int indice) {
        obtenerPedido(pedidoId).quitar(indice);
    }

    /** RF-05: ajusta la cantidad de un ítem (índice base 0). */
    public void cambiarCantidad(int pedidoId, int indice, int cantidad) {
        obtenerPedido(pedidoId).cambiarCantidad(indice, cantidad);
    }

    /** RF-06: subtotal actual del pedido. */
    public long subtotal(int pedidoId) {
        return obtenerPedido(pedidoId).subtotal();
    }

    // ===== Descuentos (RF-07, RF-08) =====

    public void quitarDescuento(int pedidoId) {
        obtenerPedido(pedidoId).aplicarDescuento(new SinDescuento());
    }

    public void aplicarDescuentoClienteFrecuente(int pedidoId) {
        obtenerPedido(pedidoId).aplicarDescuento(new DescuentoClienteFrecuente());
    }

    public void aplicarDescuentoCombo(int pedidoId) {
        obtenerPedido(pedidoId).aplicarDescuento(new DescuentoCombo());
    }

    /** RF-08: si el código no existe lanza una excepción y el pedido queda como estaba. */
    public void aplicarCodigoPromocional(int pedidoId, String codigo) {
        Pedido pedido = obtenerPedido(pedidoId);
        pedido.aplicarDescuento(new DescuentoCodigoPromocional(codigo));
    }

    // ===== Pago (RF-09, RF-10, RF-11) =====

    public ResultadoPago pagarEfectivo(int pedidoId, long montoRecibido) {
        return pagar(pedidoId, new PagoEfectivo(montoRecibido));
    }

    public ResultadoPago pagarTarjeta(int pedidoId, String numero) {
        return pagar(pedidoId, new PagoTarjeta(numero));
    }

    public ResultadoPago pagarBilletera(int pedidoId, String identificador) {
        return pagar(pedidoId, new PagoBilletera(identificador));
    }

    private ResultadoPago pagar(int pedidoId, MetodoPago metodo) {
        return obtenerPedido(pedidoId).pagar(metodo);
    }

    // ===== Estados (RF-12) =====

    public void avanzarEstado(int pedidoId) {
        obtenerPedido(pedidoId).avanzar();
    }

    public void cancelarPedido(int pedidoId) {
        obtenerPedido(pedidoId).cancelar();
    }

    // ===== Notificaciones (RF-13, RF-14) =====

    /** Nombres de los receptores que reciben avisos ahora mismo. */
    public List<String> receptoresActivos() {
        return notificador.nombres();
    }

    /** Receptores conocidos que están desactivados y se pueden volver a agregar. */
    public List<String> receptoresInactivos() {
        List<String> inactivos = new ArrayList<>();
        for (ReceptorEstado receptor : receptoresConocidos.values()) {
            if (!notificador.activo(receptor.nombre())) {
                inactivos.add(receptor.nombre());
            }
        }
        return inactivos;
    }

    /** RF-14: vuelve a activar un receptor conocido. */
    public void agregarReceptor(String nombre) {
        ReceptorEstado receptor = nombre == null ? null : receptoresConocidos.get(nombre.trim().toLowerCase());
        if (receptor == null) {
            throw new CafeteriaException("No existe el receptor \"" + nombre + "\".");
        }
        notificador.agregar(receptor);
    }

    /** RF-14: desactiva un receptor en ejecución. */
    public void quitarReceptor(String nombre) {
        notificador.quitar(nombre == null ? "" : nombre.trim());
    }

    /** Mensajes que ha recibido el registro de actividad desde que arrancó el sistema. */
    public List<String> registroActividad() {
        return registro.mensajes();
    }

    // ===== Ticket (RF-15) =====

    public List<String> formatosTicket() {
        return formatos.values().stream().map(FormatoTicket::nombre).toList();
    }

    public String generarTicket(int pedidoId, String formato) {
        FormatoTicket elegido = formato == null ? null : formatos.get(formato.trim().toLowerCase());
        if (elegido == null) {
            throw new CafeteriaException("No existe el formato de ticket \"" + formato + "\".");
        }
        return elegido.generar(obtenerPedido(pedidoId));
    }

    // ===== Historial (RF-16) =====

    /** RF-16: todos los pedidos de la sesión, en orden de creación. */
    public List<Pedido> listarPedidos() {
        return new ArrayList<>(pedidos.values());
    }

    /** RF-16: pedidos de la sesión en un estado dado. */
    public List<Pedido> listarPedidos(TipoEstado estado) {
        return pedidos.values().stream().filter(p -> p.tipoEstado() == estado).toList();
    }
}
