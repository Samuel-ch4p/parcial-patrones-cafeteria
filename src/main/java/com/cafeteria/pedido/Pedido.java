package com.cafeteria.pedido;

import com.cafeteria.descuento.ContextoDescuento;
import com.cafeteria.descuento.DescuentoInvalidoException;
import com.cafeteria.descuento.PoliticaDescuento;
import com.cafeteria.descuento.SinDescuento;
import com.cafeteria.modelo.Cliente;
import com.cafeteria.modelo.TipoEstado;
import com.cafeteria.notificacion.CambioEstado;
import com.cafeteria.notificacion.Notificador;
import com.cafeteria.pago.MetodoPago;
import com.cafeteria.pago.ResultadoPago;
import com.cafeteria.producto.Producto;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Pedido de un cliente. Es el contexto de tres patrones: delega su ciclo de vida en
 * un {@link EstadoPedido}, calcula el descuento con una {@link PoliticaDescuento}
 * y cobra con un {@link MetodoPago}. Cada cambio de estado se publica en el
 * {@link Notificador}.
 *
 * @author Samuel Chaparro, Cristian Daniel Blanco, Juan Diego Afanador
 */
public class Pedido {

    private final int id;
    private final Cliente cliente;
    private final Notificador notificador;
    private final List<LineaPedido> lineas = new ArrayList<>();
    private EstadoPedido estado = new Recibido();
    private PoliticaDescuento politicaDescuento = new SinDescuento();
    private ResultadoPago pago;

    public Pedido(int id, Cliente cliente, Notificador notificador) {
        this.id = id;
        this.cliente = cliente;
        this.notificador = notificador;
        publicar(null);
    }

    public int id() {
        return id;
    }

    public Cliente cliente() {
        return cliente;
    }

    public List<LineaPedido> lineas() {
        return Collections.unmodifiableList(lineas);
    }

    public TipoEstado tipoEstado() {
        return estado.tipo();
    }

    public PoliticaDescuento politicaDescuento() {
        return politicaDescuento;
    }

    public Optional<ResultadoPago> pago() {
        return Optional.ofNullable(pago);
    }

    public boolean estaPagado() {
        return pago != null;
    }

    // ----- Ítems -----

    /** Agrega el producto; si ya hay un ítem idéntico, suma la cantidad. */
    public void agregar(Producto producto, int cantidad) {
        exigirModificable();
        new LineaPedido(producto, cantidad); // valida la cantidad
        for (int i = 0; i < lineas.size(); i++) {
            LineaPedido existente = lineas.get(i);
            if (existente.producto().codigo().equals(producto.codigo())
                    && existente.producto().descripcion().equals(producto.descripcion())) {
                lineas.set(i, existente.conCantidad(existente.cantidad() + cantidad));
                return;
            }
        }
        lineas.add(new LineaPedido(producto, cantidad));
    }

    /** Quita el ítem en la posición indicada (base 0). */
    public void quitar(int indice) {
        exigirModificable();
        validarIndice(indice);
        lineas.remove(indice);
    }

    /** Cambia la cantidad del ítem en la posición indicada (base 0). */
    public void cambiarCantidad(int indice, int cantidad) {
        exigirModificable();
        validarIndice(indice);
        lineas.set(indice, lineas.get(indice).conCantidad(cantidad));
    }

    public long subtotal() {
        return lineas.stream().mapToLong(LineaPedido::subtotal).sum();
    }

    // ----- Descuento -----

    /** Cambia la política de descuento; falla si el pedido no cumple su condición. */
    public void aplicarDescuento(PoliticaDescuento politica) {
        exigirModificable();
        if (lineas.isEmpty() && !(politica instanceof SinDescuento)) {
            throw new DescuentoInvalidoException("Agregue productos al pedido antes de aplicar un descuento.");
        }
        politica.validar(contextoDescuento());
        this.politicaDescuento = politica;
    }

    /** Monto descontado según la política vigente. */
    public long descuento() {
        long monto = politicaDescuento.calcular(contextoDescuento());
        return Math.max(0, Math.min(monto, subtotal()));
    }

    public long total() {
        return subtotal() - descuento();
    }

    private ContextoDescuento contextoDescuento() {
        boolean hayBebida = lineas.stream().anyMatch(l -> l.producto().categoria().esBebida());
        boolean hayAlimento = lineas.stream().anyMatch(l -> !l.producto().categoria().esBebida());
        return new ContextoDescuento(cliente.frecuente(), subtotal(), hayBebida, hayAlimento);
    }

    // ----- Pago -----

    /**
     * Cobra el pedido con el método dado.
     *
     * @throws PedidoException si el pedido está vacío, ya fue pagado o está cancelado
     */
    public ResultadoPago pagar(MetodoPago metodo) {
        if (pago != null) {
            throw new PedidoException("El pedido #" + id + " ya fue pagado.");
        }
        if (lineas.isEmpty()) {
            throw new PedidoException("No se puede pagar un pedido vacío.");
        }
        if (!estado.permitePago()) {
            throw new PedidoException("No se puede pagar un pedido " + estado.tipo() + ".");
        }
        pago = metodo.pagar(total());
        return pago;
    }

    // ----- Ciclo de vida -----

    /** Pasa al estado siguiente y notifica el cambio. */
    public void avanzar() {
        cambiarA(estado.avanzar());
    }

    /** Cancela el pedido (solo Recibido o En preparación) y notifica el cambio. */
    public void cancelar() {
        cambiarA(estado.cancelar());
    }

    private void cambiarA(EstadoPedido nuevo) {
        TipoEstado anterior = estado.tipo();
        estado = nuevo;
        publicar(anterior);
    }

    private void publicar(TipoEstado anterior) {
        notificador.notificar(new CambioEstado(id, cliente.nombreVisible(), anterior, estado.tipo()));
    }

    // ----- Validaciones -----

    private void exigirModificable() {
        if (pago != null) {
            throw new PedidoException("El pedido #" + id + " ya fue pagado: no se puede modificar.");
        }
        if (!estado.permiteModificar()) {
            throw new PedidoException("El pedido #" + id + " está " + estado.tipo() + ": no se puede modificar.");
        }
    }

    private void validarIndice(int indice) {
        if (indice < 0 || indice >= lineas.size()) {
            throw new PedidoException("No existe el ítem número " + (indice + 1) + " en el pedido.");
        }
    }
}
