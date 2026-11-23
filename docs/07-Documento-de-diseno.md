# Documento de diseño

**Proyecto:** Sistema de Cafetería · parcial de Patrones de Diseño (Ingeniería de Software)
**Integrantes:** Samuel Chaparro, Cristian Daniel Blanco, Juan Diego Afanador
**Entrega:** 23 de noviembre de 2026

El sistema aplica ocho patrones de tres categorías: **creacional** (Factory Method), **estructural** (Decorator, Facade) y de **comportamiento** (Strategy en tres áreas, State y Observer). Cada patrón responde a un área de variación del [documento de alcance](01-alcance-y-requisitos.md) o a un requisito de arquitectura. Los diagramas están en [06-Uml.md](06-Uml.md) y la comparación con diseños ingenuos en [03-Problemas-y-patrones.md](03-Problemas-y-patrones.md).

Resumen:

| # | Patrón | Categoría | Área | Clase principal |
|---|---|---|---|---|
| 1 | Factory Method | Creacional | 1. Productos | `FabricaProducto` |
| 2 | Decorator | Estructural | 2. Personalizaciones | `DecoradorBebida` |
| 3 | Strategy | Comportamiento | 3. Descuentos | `PoliticaDescuento` |
| 4 | Strategy | Comportamiento | 4. Pagos | `MetodoPago` |
| 5 | State | Comportamiento | Ciclo de vida (F6) | `EstadoPedido` |
| 6 | Observer | Comportamiento | 5. Notificaciones | `Notificador`, `ReceptorEstado` |
| 7 | Strategy | Comportamiento | 6. Tickets | `FormatoTicket` |
| 8 | Facade | Estructural | Arquitectura (RNF-02) | `CafeteriaService` |

---

## 1. Factory Method — productos

**Problema.** Se agregan con frecuencia bebidas y alimentos nuevos. Si quien arma el catálogo conociera cada clase concreta, cada producto nuevo obligaría a modificarlo.

**Descripción.** Cada fábrica es responsable de crear los productos de un grupo (`FabricaCafes` crea `Espresso`, `Capuchino` y `Latte`). El catálogo no instancia productos: recibe fábricas con `registrar(FabricaProducto)` y les pide `crearProductos()`. La decisión de qué clase concreta se crea queda en la fábrica.

| Rol | Clase |
|---|---|
| Creator | `FabricaProducto` |
| ConcreteCreator | `FabricaCafes`, `FabricaTes`, `FabricaInfusiones`, `FabricaBebidasFrias`, `FabricaPanaderia`, `FabricaPostres`, `FabricaSandwiches` |
| Product | `Producto` |
| ConcreteProduct | `Espresso`, `Capuchino`, `Latte`, `TeNegro`, `InfusionManzanilla`, `InfusionFrutosRojos`, `LimonadaNatural`, `ColdBrew`, `Croissant`, `PanDeBono`, `BrownieChocolate`, `TortaZanahoria`, `SandwichJamonQueso`, `SandwichPollo` |
| Client | `CatalogoProductos` (y `CafeteriaService`, que registra las fábricas) |

**Alternativa descartada.** Un método estático `crearProducto(String tipo)` con `switch`. Obliga a abrir y modificar ese método por cada producto y mezcla en un solo lugar el conocimiento de todas las clases. Tampoco se eligió Abstract Factory porque no hay familias de productos que deban combinarse de forma consistente.

**Ventajas.** El catálogo depende de una interfaz. Un producto nuevo es una clase más (por ejemplo, una `FabricaTemporada` que se registra con `registrarFabrica`). Las pruebas registran fábricas propias (`FabricaProductoTest` crea un `Chai` sin tocar nada existente).

**Desventajas.** Más clases pequeñas (14 productos y 7 fábricas). Un producto nuevo dentro de una categoría existente pide una fábrica nueva o editar la fábrica de esa categoría.

**SOLID.** Abierto/cerrado (se agregan fábricas sin modificar el catálogo), inversión de dependencias (el catálogo depende de `FabricaProducto`), responsabilidad única (cada fábrica crea un grupo de productos).

## 2. Decorator — personalizaciones de bebida

**Problema.** Cada bebida admite tamaño, leche y extras que se combinan libremente y cambian precio y descripción. Las combinaciones posibles (1.920 con el catálogo actual) hacen inviable una clase por combinación.

**Descripción.** Una bebida personalizada es la bebida original envuelta por uno o varios decoradores. Cada decorador suma su recargo al precio del objeto envuelto y agrega su etiqueta a `personalizaciones()`. Como el decorador también es un `Producto`, el pedido lo trata igual que una bebida sin personalizar. `Personalizador` arma la pila en el orden tamaño → leche → extras a partir de códigos (`"grande"`, `"avena"`, `"shot"`).

| Rol | Clase |
|---|---|
| Component | `Producto` |
| ConcreteComponent | `Bebida` y sus subclases (`Espresso`, `Latte`, …) |
| Decorator | `DecoradorBebida` |
| ConcreteDecorator | `TamanoPequeno`, `TamanoMediano`, `TamanoGrande`, `LecheEntera`, `LecheDeslactosada`, `LecheAlmendras`, `LecheAvena`, `ShotEspresso`, `Crema`, `Jarabe`, `Canela` |
| Armado | `Personalizador`, `CatalogoOpciones`, `OpcionPersonalizacion` |

**Alternativa descartada.** Herencia (una subclase por combinación) por la explosión de clases, y banderas dentro de `Producto` (`conShot`, `conCrema`…) porque cada extra nuevo exige modificar `Producto` y su cálculo de precio, y permitiría personalizar alimentos.

**Ventajas.** Extra nuevo = una clase de pocas líneas más un registro en `CatalogoOpciones`. Combinaciones ilimitadas en tiempo de ejecución, incluso repetidas (doble shot). Los alimentos quedan protegidos: `DecoradorBebida` lanza `PersonalizacionInvalidaException` si el producto no es bebida.

**Desventajas.** Al decorar, el objeto resultante ya no es el original, por lo que `Pedido` compara por código y descripción para unir ítems idénticos. Una pila larga es más difícil de depurar. Para mostrar el recargo de cada opción, `CatalogoOpciones` lo calcula decorando una bebida de prueba.

**SOLID.** Abierto/cerrado (extras nuevos sin tocar los anteriores), responsabilidad única (cada decorador aporta una sola personalización), sustitución de Liskov (un decorador se usa donde se espera un `Producto`).

## 3. Strategy — políticas de descuento

**Problema.** Las promociones cambian por temporada, cliente o campaña. El pedido no debe cambiar cada vez que aparece una.

**Descripción.** `Pedido` guarda una `PoliticaDescuento` y le pide el monto a descontar con `calcular`. Cada política decide si aplica (`aplicable`) y cuánto descuenta. Para no acoplar paquetes, la política no recibe el pedido sino un `ContextoDescuento` con lo necesario (cliente frecuente, subtotal, si hay bebida y alimento). `DescuentoCodigoPromocional` valida el código al construirse, de modo que un código inválido no llega a alterar el pedido (RF-08).

| Rol | Clase |
|---|---|
| Strategy | `PoliticaDescuento` |
| ConcreteStrategy | `SinDescuento`, `DescuentoClienteFrecuente`, `DescuentoCombo`, `DescuentoCodigoPromocional` (las tres últimas sobre `DescuentoPorcentual`) |
| Context | `Pedido` |

**Alternativa descartada.** Un `if/else` dentro de `Pedido.calcularTotal()`. Mezcla todas las promociones en un método que crece y se rompe con cada campaña.

**Ventajas.** Cada promoción se prueba por separado (`DescuentoTest`). La política se puede cambiar mientras el pedido es modificable, y el descuento se recalcula si cambian los ítems. Promoción nueva = clase nueva.

**Desventajas.** Se necesita un objeto de contexto adicional. Los códigos promocionales viven en un mapa dentro de `DescuentoCodigoPromocional`; un listado dinámico requeriría otra clase.

**SOLID.** Abierto/cerrado, responsabilidad única (una política por clase), inversión de dependencias (`Pedido` depende de la interfaz).

## 4. Strategy — métodos de pago

**Problema.** Se suman medios de pago, cada uno con validaciones propias.

**Descripción.** `Pedido.pagar(MetodoPago)` verifica sus propias reglas (no vacío, no pagado, no cancelado) y delega la validación y el cobro en el método. `PagoEfectivo` exige monto suficiente y calcula el cambio; `PagoTarjeta` exige 16 dígitos; `PagoBilletera` exige un identificador de 4 a 30 caracteres. Si el método rechaza el pago, el pedido queda sin pagar.

| Rol | Clase |
|---|---|
| Strategy | `MetodoPago` |
| ConcreteStrategy | `PagoEfectivo`, `PagoTarjeta`, `PagoBilletera` |
| Context | `Pedido` |

**Alternativa descartada.** Un `switch` por tipo de pago dentro de `Pedido`. Las validaciones de cada medio quedarían mezcladas en la misma clase.

**Ventajas.** Validación aislada y probada por método (`MetodoPagoTest`). Medio nuevo = clase nueva (y un método en la fachada).

**Desventajas.** Quien llama debe construir la estrategia con sus datos; esa tarea la asume la fachada. Las comisiones que menciona el alcance no se modelaron: no se pidieron en los requisitos y se agregarían como un método más de `MetodoPago`.

**SOLID.** Abierto/cerrado, responsabilidad única, inversión de dependencias.

## 5. State — ciclo de vida del pedido

**Problema.** El pedido solo admite ciertas transiciones y su comportamiento depende del estado (qué se puede modificar, si se puede pagar o cancelar).

**Descripción.** `Pedido` delega `avanzar()` y `cancelar()` en su `EstadoPedido` actual, que devuelve el nuevo estado o lanza `TransicionInvalidaException` con un mensaje claro. Flujo: Recibido → En preparación → Listo → Entregado. Cancelar solo desde Recibido y En preparación. Solo Recibido permite modificar ítems; Cancelado no permite pago. Cada cambio válido se publica con `publicar`.

| Rol | Clase |
|---|---|
| State | `EstadoPedido` |
| ConcreteState | `Recibido`, `EnPreparacion`, `Listo`, `Entregado`, `Cancelado` |
| Context | `Pedido` |

**Alternativa descartada.** Una enumeración con `switch` en cada operación. Las reglas de un mismo estado quedarían repartidas en varios métodos de `Pedido`. Tampoco Strategy, porque aquí el comportamiento cambia por una transición interna, no porque el usuario elija un algoritmo.

**Ventajas.** Las transiciones inválidas son imposibles de olvidar: cada estado define las suyas. Se prueba cada estado (`EstadoPedidoTest`).

**Desventajas.** Cinco clases para un flujo corto. Los estados crean al siguiente con `new`, así que cambiar el flujo implica editar las clases involucradas. `TipoEstado` (enumeración) convive con las clases de estado para filtrar el historial.

**SOLID.** Responsabilidad única (un estado, una situación), abierto/cerrado en `Pedido` (agregar un estado no lo modifica), sustitución de Liskov.

## 6. Observer — notificación de estado

**Problema.** Cada cambio de estado debe llegar a cocina, cliente y registro, y se pueden agregar o quitar receptores durante la ejecución sin alterar el pedido.

**Descripción.** `Notificador` mantiene la lista de `ReceptorEstado` activos. `Pedido` publica un `CambioEstado` (al crearse y en cada transición) y el notificador se lo entrega a todos. Los tres receptores concretos formatean el mensaje, lo guardan y lo envían a una salida (la consola o el panel de la GUI). `CafeteriaService.quitarReceptor` y `agregarReceptor` los activan y desactivan en ejecución (RF-14).

| Rol | Clase |
|---|---|
| Subject | `Notificador` |
| Observer | `ReceptorEstado` |
| ConcreteObserver | `PantallaCocina`, `NotificacionCliente`, `RegistroActividad` (sobre `ReceptorConSalida`) |
| Evento | `CambioEstado` |

**Alternativa descartada.** Que `Pedido` llame directamente a cada receptor. Acopla el pedido a todos los canales y hace imposible quitar uno en ejecución. Tampoco se usó `java.util.Observable`, obsoleta desde Java 9.

**Ventajas.** El pedido no conoce a sus receptores. Receptor nuevo = clase que implementa `ReceptorEstado` y se registra (`NotificadorTest` agrega uno propio). La GUI y la consola reciben los avisos con el mismo mecanismo.

**Desventajas.** Los receptores se ejecutan en secuencia y en el mismo hilo; uno lento o con error afectaría a los demás. El orden de aviso es el de registro. No se garantiza entrega si un receptor se quita en medio de una notificación.

**SOLID.** Abierto/cerrado, inversión de dependencias (`Pedido` depende de `Notificador`, no de receptores), segregación de interfaces (`ReceptorEstado` tiene solo dos métodos).

## 7. Strategy — formatos de ticket

**Problema.** El mismo contenido debe poder presentarse de varias formas y aparecer formatos nuevos.

**Descripción.** `FormatoTicket.generar(Pedido)` devuelve el texto. `TicketTextoPlano` imprime el detalle con extras, subtotal, descuento, total y pago; `TicketResumido` condensa lo esencial en dos líneas. La fachada guarda los formatos por nombre y `registrarFormato` agrega uno nuevo.

| Rol | Clase |
|---|---|
| Strategy | `FormatoTicket` |
| ConcreteStrategy | `TicketTextoPlano`, `TicketResumido` |
| Context | `CafeteriaService` (elige el formato por nombre) |

**Alternativa descartada.** Un método `generarTicket(pedido, tipo)` con un `if` por formato. Cada formato nuevo modificaría una clase grande.

**Ventajas.** Formato nuevo sin tocar los existentes; cada formato se prueba por separado (`FormatoTicketTest`).

**Desventajas.** Cada formato recorre el pedido por su cuenta y puede repetir lógica de lectura (por ejemplo, el cálculo de unidades).

**SOLID.** Abierto/cerrado, responsabilidad única, inversión de dependencias.

## 8. Facade — arquitectura

**Problema.** La consola y la GUI necesitan las mismas operaciones; si cada una coordinara el núcleo, duplicarían reglas y secuencias.

**Descripción.** `CafeteriaService` expone las operaciones RF-01 a RF-16 y oculta cómo se coordinan catálogo, decoradores, descuentos, pagos, estados, notificaciones y tickets. Es la única clase del núcleo que conocen `consola` y `gui`. También ofrece los puntos de extensión (`registrarFabrica`, `registrarFormato`, `registrarReceptor`).

| Rol | Clase |
|---|---|
| Facade | `CafeteriaService` |
| Subsistema | `CatalogoProductos`, `Personalizador`, `Pedido`, `Notificador`, `FormatoTicket` y el resto del núcleo |
| Cliente | `AplicacionConsola`, `MenuPrincipal`, `MenuPedido`, `SesionGui` y los paneles de `gui` |

**Alternativa descartada.** Que cada interfaz use directamente las clases del núcleo (más flexible, pero duplica la coordinación y filtra reglas hacia la presentación). Tampoco se creó una clase "controlador" por interfaz, porque separaría la lógica en dos.

**Ventajas.** Un solo punto de entrada y de prueba (`CafeteriaServiceTest`). Las interfaces quedan delgadas. Cambiar el núcleo no obliga a cambiar la presentación mientras la fachada se mantenga.

**Desventajas.** La clase crece con cada operación nueva (hoy tiene unas 35 operaciones). Hay que vigilar que no acumule reglas de negocio. Por simplicidad, la fachada devuelve objetos del núcleo (`Pedido`, `Producto`) que las interfaces solo leen; un diseño más estricto devolvería objetos de transferencia de datos.

**SOLID.** Responsabilidad única en la presentación (solo muestra), inversión de dependencias (las interfaces dependen de la fachada y no de detalles del núcleo).

---

## 9. Conclusiones

**Qué se aprendió.**

- Elegir el patrón por la **variación concreta** y no por el nombre. Strategy sirvió en tres áreas distintas (descuentos, pagos, tickets) con la misma forma; State resultó mejor que Strategy para el pedido porque el comportamiento cambia por una transición interna.
- Las dependencias entre paquetes se diseñan. Para que `pedido` dependa de `descuento` y `notificacion` sin ciclos hubo que introducir `ContextoDescuento` y `CambioEstado`, con datos simples.
- Probar el núcleo sin interfaz fue posible gracias a la fachada: la consola se probó incluso con entrada simulada.
- Algunos patrones traen un costo de clases pequeñas; el beneficio se nota al extender (extras, fábricas y receptores nuevos se probaron sin tocar código existente).

**Qué se cambiaría.**

- Hacer que la fachada devuelva objetos de transferencia de datos en lugar de objetos del núcleo, para aislar del todo la presentación.
- Mover los códigos promocionales y los precios de los extras a un archivo de configuración.
- Aislar los fallos de un receptor en `Notificador` (captura de excepciones por receptor) y evaluar notificaciones asíncronas.
- Modelar comisiones de pago como parte de `MetodoPago` (el alcance las menciona y no se implementaron).
- Reemplazar el `new` de los estados por una tabla de transiciones si el flujo crece.

## 10. Declaración de uso de IA

Se utilizó **Claude** (asistente de IA de Anthropic) como apoyo en este proyecto. Se usó para:

- generar la estructura inicial del proyecto, el código Java del núcleo, la consola y la interfaz Swing;
- redactar las pruebas JUnit y ejecutar la compilación y las pruebas;
- redactar borradores del README y de los documentos 02 a 05, incluidos los diagramas Mermaid;
- verificar que los diagramas coincidieran con las clases reales.

La revisión de calidad posterior ([documento 08](08-Revision-de-calidad.md)) la realizó Juan Diego Afanador con apoyo de Claude, que ayudó a analizar el código, reproducir los errores y redactar las correcciones y sus pruebas. Juan Diego definió el alcance de la revisión, decidió qué corregir y qué dejar a decisión del grupo, autorizó cada cambio, ejecutó las pruebas en su equipo, revisó cada diferencia y publicó cada corrección como un commit independiente.

Las decisiones de alcance, patrones y supuestos fueron definidas por el grupo. El grupo revisó, probó y adaptó el resultado y asume la responsabilidad del contenido entregado, que puede explicar y defender.
