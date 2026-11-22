# Diagramas UML

**Proyecto:** Sistema de Cafetería · **Integrantes:** Samuel Chaparro, Cristian Daniel Blanco, Juan Diego Afanador

Todos los diagramas están en Mermaid y usan los nombres reales de clases y métodos del código (`src/main/java/com/cafeteria`). En los diagramas por patrón, el rol del patrón se marca con una nota sobre cada clase. Se omiten getters triviales y las excepciones, salvo cuando son parte del comportamiento.

Índice: [1. Casos de uso](#1-casos-de-uso) · [2. Clases general](#2-diagrama-de-clases-general) · [3. Factory Method](#3-factory-method-productos) · [4. Decorator](#4-decorator-personalizaciones) · [5. Strategy descuentos](#5-strategy-descuentos) · [6. Strategy pagos](#6-strategy-métodos-de-pago) · [7. State](#7-state-ciclo-de-vida-del-pedido) · [8. Observer](#8-observer-notificaciones) · [9. Strategy tickets](#9-strategy-formatos-de-ticket) · [10. Facade](#10-facade) · [11. Secuencia](#11-diagrama-de-secuencia-flujo-principal)

## 1. Casos de uso

Mermaid no tiene un diagrama de casos de uso propio; se representa con un diagrama de flujo (actores a la izquierda, casos de uso como óvalos). Cada caso de uso está disponible por consola y por GUI (RF-17).

```mermaid
flowchart LR
    Cajero(["Cajero / Barista"])
    Cocina(["Cocina"])
    subgraph Sistema["Sistema de Cafetería"]
        UC1(["Consultar catálogo y detalle (RF-01, RF-02)"])
        UC2(["Personalizar bebida (RF-03, RF-04)"])
        UC3(["Crear pedido y gestionar ítems (RF-05, RF-06)"])
        UC4(["Aplicar descuento (RF-07, RF-08)"])
        UC5(["Pagar pedido (RF-09, RF-10, RF-11)"])
        UC6(["Cambiar estado o cancelar (RF-12)"])
        UC7(["Ver notificaciones (RF-13)"])
        UC8(["Agregar o quitar receptores (RF-14)"])
        UC9(["Generar ticket (RF-15)"])
        UC10(["Consultar historial con filtro (RF-16)"])
    end
    Cajero --- UC1
    Cajero --- UC2
    Cajero --- UC3
    Cajero --- UC4
    Cajero --- UC5
    Cajero --- UC6
    Cajero --- UC8
    Cajero --- UC9
    Cajero --- UC10
    Cocina --- UC6
    Cocina --- UC7
    UC2 -. extiende .-> UC3
    UC4 -. extiende .-> UC3
```

## 2. Diagrama de clases general

Muestra las clases centrales de cada paquete y cómo se relacionan. Los detalles de cada patrón están en las secciones 3 a 10.

```mermaid
classDiagram
    direction LR

    class AplicacionConsola
    class MenuPrincipal
    class MenuPedido
    class AplicacionGui
    class VentanaPrincipal
    class SesionGui

    class CafeteriaService {
        +catalogoPorCategoria() Map
        +previsualizar(codigo, tamano, leche, extras) Producto
        +crearPedido(nombreCliente, frecuente) Pedido
        +agregarItem(pedidoId, codigo, cantidad, tamano, leche, extras)
        +aplicarCodigoPromocional(pedidoId, codigo)
        +pagarEfectivo(pedidoId, montoRecibido) ResultadoPago
        +avanzarEstado(pedidoId)
        +generarTicket(pedidoId, formato) String
        +listarPedidos(estado) List
    }

    class Pedido {
        -id int
        -estado EstadoPedido
        -politicaDescuento PoliticaDescuento
        -pago ResultadoPago
        +agregar(producto, cantidad)
        +aplicarDescuento(politica)
        +pagar(metodo) ResultadoPago
        +avanzar()
        +cancelar()
        +subtotal() long
        +descuento() long
        +total() long
    }
    class LineaPedido {
        +producto Producto
        +cantidad int
        +subtotal() long
    }
    class Cliente {
        +nombre String
        +frecuente boolean
    }
    class CatalogoProductos
    class CatalogoOpciones
    class Personalizador
    class Notificador
    class Producto {
        <<interface>>
    }
    class EstadoPedido {
        <<interface>>
    }
    class PoliticaDescuento {
        <<interface>>
    }
    class MetodoPago {
        <<interface>>
    }
    class FormatoTicket {
        <<interface>>
    }
    class ReceptorEstado {
        <<interface>>
    }
    class FabricaProducto {
        <<interface>>
    }

    AplicacionConsola --> MenuPrincipal
    MenuPrincipal --> MenuPedido
    MenuPrincipal --> CafeteriaService
    MenuPedido --> CafeteriaService
    AplicacionGui --> VentanaPrincipal
    VentanaPrincipal --> SesionGui
    SesionGui --> CafeteriaService

    CafeteriaService --> CatalogoProductos
    CafeteriaService --> CatalogoOpciones
    CafeteriaService --> Personalizador
    CafeteriaService --> Notificador
    CafeteriaService --> FormatoTicket
    CafeteriaService "1" o-- "*" Pedido
    Personalizador --> CatalogoOpciones
    CatalogoProductos --> FabricaProducto

    Pedido "1" *-- "*" LineaPedido
    Pedido --> Cliente
    Pedido --> EstadoPedido
    Pedido --> PoliticaDescuento
    Pedido ..> MetodoPago : usa al pagar
    Pedido --> Notificador
    LineaPedido --> Producto
    Notificador "1" o-- "*" ReceptorEstado
    FormatoTicket ..> Pedido : lee
```

## 3. Factory Method: productos

Área de variación 1. El catálogo recibe fábricas y no conoce las clases concretas de producto.

```mermaid
classDiagram
    class Producto {
        <<interface>>
        +codigo() String
        +nombre() String
        +categoria() Categoria
        +precioBase() long
        +precio() long
        +detalle() String
        +personalizaciones() List
        +descripcion() String
    }
    class ProductoCatalogo {
        <<abstract>>
    }
    class Bebida {
        <<abstract>>
    }
    class Alimento {
        <<abstract>>
    }
    class FabricaProducto {
        <<interface>>
        +crearProductos() List
    }
    class CatalogoProductos {
        +registrar(fabrica FabricaProducto)
        +todos() List
        +porCategoria() Map
        +buscar(codigo) Producto
    }

    Producto <|.. ProductoCatalogo
    ProductoCatalogo <|-- Bebida
    ProductoCatalogo <|-- Alimento

    Bebida <|-- Espresso
    Bebida <|-- Capuchino
    Bebida <|-- Latte
    Bebida <|-- TeNegro
    Bebida <|-- InfusionManzanilla
    Bebida <|-- InfusionFrutosRojos
    Bebida <|-- LimonadaNatural
    Bebida <|-- ColdBrew
    Alimento <|-- Croissant
    Alimento <|-- PanDeBono
    Alimento <|-- BrownieChocolate
    Alimento <|-- TortaZanahoria
    Alimento <|-- SandwichJamonQueso
    Alimento <|-- SandwichPollo

    FabricaProducto <|.. FabricaCafes
    FabricaProducto <|.. FabricaTes
    FabricaProducto <|.. FabricaInfusiones
    FabricaProducto <|.. FabricaBebidasFrias
    FabricaProducto <|.. FabricaPanaderia
    FabricaProducto <|.. FabricaPostres
    FabricaProducto <|.. FabricaSandwiches

    FabricaCafes ..> Espresso : crea
    FabricaCafes ..> Capuchino : crea
    FabricaCafes ..> Latte : crea
    FabricaTes ..> TeNegro : crea
    FabricaInfusiones ..> InfusionManzanilla : crea
    FabricaInfusiones ..> InfusionFrutosRojos : crea
    FabricaBebidasFrias ..> LimonadaNatural : crea
    FabricaBebidasFrias ..> ColdBrew : crea
    FabricaPanaderia ..> Croissant : crea
    FabricaPanaderia ..> PanDeBono : crea
    FabricaPostres ..> BrownieChocolate : crea
    FabricaPostres ..> TortaZanahoria : crea
    FabricaSandwiches ..> SandwichJamonQueso : crea
    FabricaSandwiches ..> SandwichPollo : crea

    CatalogoProductos --> FabricaProducto : recibe fábricas
    CatalogoProductos o-- Producto : guarda

    note for FabricaProducto "Rol: Creator. crearProductos() es el método de fábrica"
    note for FabricaCafes "Rol: ConcreteCreator (igual para las otras seis fábricas)"
    note for Producto "Rol: Product"
    note for Espresso "Rol: ConcreteProduct (igual para los otros 13 productos)"
    note for CatalogoProductos "Rol: Client. Solo conoce FabricaProducto y Producto"
```

## 4. Decorator: personalizaciones

Área de variación 2. Un decorador envuelve una bebida, suma un recargo y agrega una etiqueta a la descripción.

```mermaid
classDiagram
    class Producto {
        <<interface>>
        +precio() long
        +personalizaciones() List
        +descripcion() String
    }
    class Bebida {
        <<abstract>>
    }
    class DecoradorBebida {
        <<abstract>>
        -envuelto Producto
        -etiqueta String
        -recargo long
        #DecoradorBebida(envuelto, etiqueta, recargo)
        +precio() long
        +personalizaciones() List
    }
    class TamanoPequeno
    class TamanoMediano
    class TamanoGrande
    class LecheEntera
    class LecheDeslactosada
    class LecheAlmendras
    class LecheAvena
    class ShotEspresso
    class Crema
    class Jarabe
    class Canela
    class Personalizador {
        +personalizar(base, tamano, leche, extras) Producto
    }
    class CatalogoOpciones {
        +registrar(codigo, nombre, tipo, aplicador)
        +opciones(tipo) List
        +buscar(tipo, codigo) OpcionPersonalizacion
    }

    Producto <|.. Bebida
    Producto <|.. DecoradorBebida
    DecoradorBebida o-- Producto : envuelve
    DecoradorBebida <|-- TamanoPequeno
    DecoradorBebida <|-- TamanoMediano
    DecoradorBebida <|-- TamanoGrande
    DecoradorBebida <|-- LecheEntera
    DecoradorBebida <|-- LecheDeslactosada
    DecoradorBebida <|-- LecheAlmendras
    DecoradorBebida <|-- LecheAvena
    DecoradorBebida <|-- ShotEspresso
    DecoradorBebida <|-- Crema
    DecoradorBebida <|-- Jarabe
    DecoradorBebida <|-- Canela
    Personalizador --> CatalogoOpciones
    Personalizador ..> DecoradorBebida : apila

    note for Producto "Rol: Component"
    note for Bebida "Rol: ConcreteComponent (Espresso, Capuchino, Latte, TeNegro, InfusionManzanilla, InfusionFrutosRojos, LimonadaNatural, ColdBrew)"
    note for DecoradorBebida "Rol: Decorator. Rechaza productos que no son bebidas"
    note for ShotEspresso "Rol: ConcreteDecorator (igual para las otras diez clases de tamaño, leche y extra)"
    note for Personalizador "Arma la pila: tamaño, leche y extras"
```

## 5. Strategy: descuentos

Área de variación 3. `Pedido` es el contexto; recibe un `ContextoDescuento` para que el paquete `descuento` no dependa de `pedido`.

```mermaid
classDiagram
    class PoliticaDescuento {
        <<interface>>
        +nombre() String
        +aplicable(contexto) boolean
        +motivoNoAplicable() String
        +calcular(contexto) long
        +validar(contexto)
    }
    class DescuentoPorcentual {
        <<abstract>>
        -nombre String
        -porcentaje int
        +calcular(contexto) long
    }
    class SinDescuento
    class DescuentoClienteFrecuente
    class DescuentoCombo
    class DescuentoCodigoPromocional
    class ContextoDescuento {
        <<record>>
        +clienteFrecuente boolean
        +subtotal long
        +hayBebida boolean
        +hayAlimento boolean
    }
    class Pedido {
        -politicaDescuento PoliticaDescuento
        +aplicarDescuento(politica)
        +descuento() long
        +total() long
    }

    PoliticaDescuento <|.. SinDescuento
    PoliticaDescuento <|.. DescuentoPorcentual
    DescuentoPorcentual <|-- DescuentoClienteFrecuente
    DescuentoPorcentual <|-- DescuentoCombo
    DescuentoPorcentual <|-- DescuentoCodigoPromocional
    Pedido --> PoliticaDescuento : politicaDescuento
    PoliticaDescuento ..> ContextoDescuento : recibe

    note for PoliticaDescuento "Rol: Strategy"
    note for DescuentoCombo "Rol: ConcreteStrategy (también SinDescuento, DescuentoClienteFrecuente y DescuentoCodigoPromocional)"
    note for Pedido "Rol: Context"
```

## 6. Strategy: métodos de pago

Área de variación 4. Cada método valida sus propios datos dentro de `pagar`.

```mermaid
classDiagram
    class MetodoPago {
        <<interface>>
        +nombre() String
        +pagar(total long) ResultadoPago
    }
    class PagoEfectivo {
        -montoRecibido long
    }
    class PagoTarjeta {
        -numero String
    }
    class PagoBilletera {
        -identificador String
    }
    class ResultadoPago {
        <<record>>
        +metodo String
        +detalle String
        +monto long
        +cambio long
    }
    class PagoInvalidoException
    class Pedido {
        +pagar(metodo MetodoPago) ResultadoPago
    }

    MetodoPago <|.. PagoEfectivo
    MetodoPago <|.. PagoTarjeta
    MetodoPago <|.. PagoBilletera
    MetodoPago ..> ResultadoPago : devuelve
    MetodoPago ..> PagoInvalidoException : lanza si los datos no son válidos
    Pedido ..> MetodoPago : usa

    note for MetodoPago "Rol: Strategy"
    note for PagoTarjeta "Rol: ConcreteStrategy (también PagoEfectivo y PagoBilletera)"
    note for Pedido "Rol: Context. Rechaza pedido vacío, ya pagado o cancelado"
```

## 7. State: ciclo de vida del pedido

Transiciones válidas: Recibido → En preparación → Listo → Entregado. Se puede cancelar desde Recibido y En preparación. Cualquier otra transición lanza `TransicionInvalidaException`.

```mermaid
classDiagram
    class EstadoPedido {
        <<interface>>
        +tipo() TipoEstado
        +avanzar() EstadoPedido
        +cancelar() EstadoPedido
        +permiteModificar() boolean
        +permitePago() boolean
    }
    class Recibido
    class EnPreparacion
    class Listo
    class Entregado
    class Cancelado
    class Pedido {
        -estado EstadoPedido
        +avanzar()
        +cancelar()
        -cambiarA(nuevo EstadoPedido)
    }
    class TransicionInvalidaException

    EstadoPedido <|.. Recibido
    EstadoPedido <|.. EnPreparacion
    EstadoPedido <|.. Listo
    EstadoPedido <|.. Entregado
    EstadoPedido <|.. Cancelado
    Pedido --> EstadoPedido : estado
    Recibido ..> EnPreparacion : avanzar
    Recibido ..> Cancelado : cancelar
    EnPreparacion ..> Listo : avanzar
    EnPreparacion ..> Cancelado : cancelar
    Listo ..> Entregado : avanzar
    Listo ..> TransicionInvalidaException : cancelar
    Entregado ..> TransicionInvalidaException : avanzar, cancelar
    Cancelado ..> TransicionInvalidaException : avanzar, cancelar

    note for EstadoPedido "Rol: State"
    note for Recibido "Rol: ConcreteState (igual para los otros cuatro)"
    note for Pedido "Rol: Context"
```

## 8. Observer: notificaciones

Área de variación 5. `Pedido` publica un `CambioEstado` en el `Notificador` y no conoce a los receptores.

```mermaid
classDiagram
    class Notificador {
        -receptores List
        +agregar(receptor ReceptorEstado)
        +quitar(nombre String)
        +activo(nombre String) boolean
        +nombres() List
        +notificar(cambio CambioEstado)
    }
    class ReceptorEstado {
        <<interface>>
        +nombre() String
        +actualizar(cambio CambioEstado)
    }
    class ReceptorConSalida {
        <<abstract>>
        -nombre String
        -salida Consumer
        -mensajes List
        +actualizar(cambio CambioEstado)
        +mensajes() List
        #formatear(cambio CambioEstado) String
    }
    class PantallaCocina
    class NotificacionCliente
    class RegistroActividad
    class CambioEstado {
        <<record>>
        +pedidoId int
        +cliente String
        +anterior TipoEstado
        +nuevo TipoEstado
    }
    class Pedido {
        -notificador Notificador
        -publicar(anterior TipoEstado)
    }

    ReceptorEstado <|.. ReceptorConSalida
    ReceptorConSalida <|-- PantallaCocina
    ReceptorConSalida <|-- NotificacionCliente
    ReceptorConSalida <|-- RegistroActividad
    Notificador "1" o-- "*" ReceptorEstado : receptores
    Notificador ..> CambioEstado : distribuye
    Pedido --> Notificador : notificador

    note for Notificador "Rol: Subject (agregar y quitar son attach y detach)"
    note for ReceptorEstado "Rol: Observer. actualizar() es update"
    note for PantallaCocina "Rol: ConcreteObserver (también NotificacionCliente y RegistroActividad)"
    note for CambioEstado "Estado enviado a los observadores"
```

## 9. Strategy: formatos de ticket

Área de variación 6.

```mermaid
classDiagram
    class FormatoTicket {
        <<interface>>
        +nombre() String
        +generar(pedido Pedido) String
    }
    class TicketTextoPlano
    class TicketResumido
    class CafeteriaService {
        -formatos Map
        +registrarFormato(formato FormatoTicket)
        +formatosTicket() List
        +generarTicket(pedidoId, formato) String
    }
    class Pedido

    FormatoTicket <|.. TicketTextoPlano
    FormatoTicket <|.. TicketResumido
    CafeteriaService o-- FormatoTicket : formatos
    FormatoTicket ..> Pedido : lee

    note for FormatoTicket "Rol: Strategy"
    note for TicketResumido "Rol: ConcreteStrategy (también TicketTextoPlano)"
    note for CafeteriaService "Rol: Context"
```

## 10. Facade

Arquitectura (RNF-02). Consola y GUI solo ven `CafeteriaService`.

```mermaid
classDiagram
    class CafeteriaService {
        +registrarFabrica(fabrica)
        +registrarFormato(formato)
        +registrarReceptor(receptor)
        +catalogoPorCategoria() Map
        +catalogo() List
        +obtenerProducto(codigo) Producto
        +opciones(tipo) List
        +previsualizar(codigo, tamano, leche, extras) Producto
        +crearPedido(nombreCliente, frecuente) Pedido
        +crearPedidoAnonimo() Pedido
        +obtenerPedido(pedidoId) Pedido
        +agregarItem(pedidoId, codigo, cantidad, tamano, leche, extras)
        +quitarItem(pedidoId, indice)
        +cambiarCantidad(pedidoId, indice, cantidad)
        +subtotal(pedidoId) long
        +quitarDescuento(pedidoId)
        +aplicarDescuentoClienteFrecuente(pedidoId)
        +aplicarDescuentoCombo(pedidoId)
        +aplicarCodigoPromocional(pedidoId, codigo)
        +pagarEfectivo(pedidoId, montoRecibido) ResultadoPago
        +pagarTarjeta(pedidoId, numero) ResultadoPago
        +pagarBilletera(pedidoId, identificador) ResultadoPago
        +avanzarEstado(pedidoId)
        +cancelarPedido(pedidoId)
        +receptoresActivos() List
        +receptoresInactivos() List
        +agregarReceptor(nombre)
        +quitarReceptor(nombre)
        +registroActividad() List
        +formatosTicket() List
        +generarTicket(pedidoId, formato) String
        +listarPedidos() List
        +listarPedidos(estado TipoEstado) List
    }
    class MenuPrincipal
    class MenuPedido
    class SesionGui
    class CatalogoProductos
    class Personalizador
    class Pedido
    class Notificador
    class FormatoTicket

    MenuPrincipal --> CafeteriaService
    MenuPedido --> CafeteriaService
    SesionGui --> CafeteriaService
    CafeteriaService --> CatalogoProductos
    CafeteriaService --> Personalizador
    CafeteriaService --> Pedido
    CafeteriaService --> Notificador
    CafeteriaService --> FormatoTicket

    note for CafeteriaService "Rol: Facade. Cubre RF-01 a RF-16"
    note for MenuPrincipal "Rol: Client (las clases de consola)"
    note for SesionGui "Rol: Client (las clases de gui)"
    note for Pedido "Rol: Subsystem (junto con el resto del núcleo)"
```

## 11. Diagrama de secuencia: flujo principal

Crear pedido → personalizar → descuento → pago → cambio de estado → notificación → ticket. El "Usuario" es quien opera la consola o la GUI; ambas hacen exactamente estas llamadas a la fachada.

```mermaid
sequenceDiagram
    actor U as Usuario (consola o GUI)
    participant S as CafeteriaService
    participant C as CatalogoProductos
    participant Z as Personalizador
    participant P as Pedido
    participant N as Notificador
    participant R as ReceptorEstado (cocina, cliente, registro)

    U->>S: crearPedido("Ana", true)
    S->>P: new Pedido(id, Cliente.frecuente("Ana"), notificador)
    P->>N: notificar(CambioEstado(null → RECIBIDO))
    N->>R: actualizar(cambio)
    S-->>U: Pedido

    U->>S: agregarItem(id, "latte", 2, "grande", "avena", ["shot"])
    S->>C: buscar("latte")
    C-->>S: Latte
    S->>Z: personalizar(latte, "grande", "avena", ["shot"])
    Note over Z: TamanoGrande(Latte), luego LecheAvena, luego ShotEspresso
    Z-->>S: Producto decorado
    S->>P: agregar(producto, 2)
    S-->>U: subtotal actualizado

    U->>S: aplicarDescuentoClienteFrecuente(id)
    S->>P: aplicarDescuento(new DescuentoClienteFrecuente())
    P->>P: politica.validar(ContextoDescuento)
    S-->>U: descuento y total

    U->>S: pagarTarjeta(id, "4111111111111111")
    S->>P: pagar(new PagoTarjeta(numero))
    P->>P: metodo.pagar(total())
    P-->>S: ResultadoPago
    S-->>U: pago aprobado

    U->>S: avanzarEstado(id)
    S->>P: avanzar()
    P->>P: estado.avanzar() devuelve new EnPreparacion()
    P->>N: notificar(CambioEstado(RECIBIDO → EN_PREPARACION))
    N->>R: actualizar(cambio) para cada receptor activo
    R-->>U: mensajes en consola o en el panel de la GUI

    U->>S: generarTicket(id, "Texto plano")
    S->>P: lee ítems, descuento, total y pago
    S-->>U: ticket (TicketTextoPlano.generar(pedido))
```
