# Sistema de Cafetería

Parcial de Patrones de Diseño (Ingeniería de Software). Aplicación en Java para registrar y gestionar pedidos de una cafetería, con interfaz de consola y interfaz gráfica (Swing) sobre un mismo núcleo.

- **Propuesta:** 3 — Cafetería
- **Integrantes:** Samuel Chaparro, Cristian Daniel Blanco, Juan Diego Afanador
- **Entrega:** 23 de noviembre de 2026

> **Nota de integridad académica:** este proyecto se desarrolló con apoyo de Claude (IA) para generar código, pruebas y borradores de documentación. El grupo revisó, probó y adaptó el resultado y asume la responsabilidad de su contenido. El detalle está en [docs/07-Documento-de-diseno.md](docs/07-Documento-de-diseno.md).

## Qué hace

| Código | Funcionalidad | Dónde verla |
|---|---|---|
| F1 | Catálogo de 8 bebidas y 6 alimentos, con detalle por producto | Consola: opciones 1 y 2 · GUI: tabla de catálogo |
| F2 | Personalización de bebidas: tamaño, leche y extras (shot, crema, jarabe, canela) | Al agregar una bebida |
| F3 | Pedidos: crear, agregar, quitar, ajustar cantidades y subtotal en vivo | Menú del pedido · panel "Pedido en curso" |
| F4 | Descuentos: ninguno, cliente frecuente 10 %, combo 15 %, código promocional | Menú del pedido · selector "Descuento" |
| F5 | Pago con efectivo, tarjeta o billetera digital, con validación propia | Menú del pedido · selector "Pago" |
| F6 | Estados Recibido → En preparación → Listo → Entregado, o Cancelado | Menú del pedido · botones de estado |
| F7 | Notificaciones a cocina, cliente y registro; se activan y desactivan en ejecución | Consola: opción 6 · GUI: panel inferior |
| F8 | Ticket en texto plano o resumido | Menú del pedido · "Ver ticket" |
| F9 | Historial de la sesión con filtro por estado | Consola: opción 5 · GUI: pestaña Historial |
| F10 | Todo lo anterior por consola y por GUI | `Main` con dos modos |

Los datos viven en memoria (se pierden al cerrar), los pagos y las notificaciones son simulados y la moneda es el peso colombiano sin decimales. Códigos promocionales de prueba: `CAFE10`, `DULCE15`, `NOVIEMBRE20`.

## Requisitos

- Java 17 o superior (probado con Java 25).
- Maven 3.8 o superior.

## Compilar, ejecutar y probar

```powershell
mvn clean package
```

Compila, corre las pruebas y genera `target/sistema-cafeteria-1.0.0.jar`.

```powershell
java -jar target/sistema-cafeteria-1.0.0.jar
```

Sin argumentos abre la consola. Para la interfaz gráfica:

```powershell
java -jar target/sistema-cafeteria-1.0.0.jar gui
```

Solo las pruebas:

```powershell
mvn test
```

La consola también se puede alimentar con un archivo de entradas, por ejemplo `java -jar target/sistema-cafeteria-1.0.0.jar consola < entradas.txt`. Al acabarse el archivo termina sin errores.

### Desde Apache NetBeans

NetBeans ya trae Maven, así que no hace falta instalarlo aparte.

1. **File → Open Project…** y elegir la carpeta del proyecto (la que tiene `pom.xml`). La primera vez descarga las dependencias.
2. **Probar:** clic derecho sobre el proyecto → **Test** (`Alt + F6`). El resultado aparece en la ventana **Test Results**.
3. **Consola:** menú **Run → Run Project** (o clic derecho sobre el proyecto → **Run**). Si pide la clase principal, elegir `com.cafeteria.Main`. La consola se usa desde la ventana **Output** (`Ctrl + 4` si está cerrada).
4. **Interfaz gráfica:** clic derecho sobre la raíz del proyecto → **Properties → Run**. Poner `com.cafeteria.Main` en **Main Class** y `gui` en **Arguments**, y aceptar. Desde ahí **Run → Run Project** abre la ventana. Para volver a la consola, dejar **Arguments** vacío.
   - No usar *Run File* (`Shift + F6`) sobre `Main.java`: ignora los argumentos y siempre abre la consola.
5. **Generar el `.jar`:** clic derecho → **Clean and Build** (`Shift + F11`). Queda en `target/`.

## Estructura de paquetes

Base: `com.cafeteria`. Las flechas indican "depende de"; no hay ciclos.

```
Main
 ├─ consola ─┐
 └─ gui ─────┴─> servicio (fachada) ─> pedido ─> descuento, pago, notificacion
                                  │        └───> producto, modelo
                                  ├─> extras ──> producto
                                  ├─> ticket ──> pedido
                                  └─> producto, modelo
```

| Paquete | Contenido |
|---|---|
| `modelo` | `Cliente`, `TipoEstado`, `Moneda`, `CafeteriaException` |
| `producto` | `Producto`, `Bebida`, `Alimento`, productos concretos, `FabricaProducto` y sus fábricas, `CatalogoProductos` |
| `extras` | `DecoradorBebida` y decoradores (tamaño, leche, extras), `CatalogoOpciones`, `Personalizador` |
| `descuento` | `PoliticaDescuento` y sus implementaciones |
| `pago` | `MetodoPago` y sus implementaciones, `ResultadoPago` |
| `pedido` | `Pedido`, `LineaPedido`, `EstadoPedido` y sus cinco estados |
| `notificacion` | `Notificador`, `ReceptorEstado`, receptores concretos, `CambioEstado` |
| `ticket` | `FormatoTicket`, `TicketTextoPlano`, `TicketResumido` |
| `servicio` | `CafeteriaService` (fachada) |
| `consola`, `gui` | Presentación. Solo llaman a `CafeteriaService` |

El núcleo (todo menos `consola` y `gui`) no importa `javax.swing` ni nada de las interfaces. Más detalle en [docs/05-Arquitectura.md](docs/05-Arquitectura.md).

## Patrones aplicados (RNF-09)

| Patrón | Categoría | Área de variación | Clases reales |
|---|---|---|---|
| Factory Method | Creacional | 1. Productos y su creación | `FabricaProducto` (creador); `FabricaCafes`, `FabricaTes`, `FabricaInfusiones`, `FabricaBebidasFrias`, `FabricaPanaderia`, `FabricaPostres`, `FabricaSandwiches` (creadores concretos); `Producto` y sus 14 clases concretas (`Espresso`, `Latte`, `Croissant`…); `CatalogoProductos` (cliente) |
| Decorator | Estructural | 2. Personalizaciones de bebida | `Producto` (componente); `Bebida` y sus subclases (componentes concretos); `DecoradorBebida` (decorador); `TamanoPequeno/Mediano/Grande`, `LecheEntera/Deslactosada/Almendras/Avena`, `ShotEspresso`, `Crema`, `Jarabe`, `Canela` (decoradores concretos); `Personalizador` y `CatalogoOpciones` los arman |
| Strategy | Comportamiento | 3. Políticas de descuento | `PoliticaDescuento` (estrategia); `SinDescuento`, `DescuentoClienteFrecuente`, `DescuentoCombo`, `DescuentoCodigoPromocional` (concretas, con `DescuentoPorcentual` como base común); `Pedido` (contexto) |
| Strategy | Comportamiento | 4. Métodos de pago | `MetodoPago` (estrategia); `PagoEfectivo`, `PagoTarjeta`, `PagoBilletera` (concretas); `Pedido` (contexto) |
| Observer | Comportamiento | 5. Receptores de notificación | `Notificador` (sujeto); `ReceptorEstado` (observador); `PantallaCocina`, `NotificacionCliente`, `RegistroActividad` (concretos, sobre `ReceptorConSalida`); `CambioEstado` (evento) |
| Strategy | Comportamiento | 6. Formatos de ticket | `FormatoTicket` (estrategia); `TicketTextoPlano`, `TicketResumido` (concretas); `CafeteriaService` (contexto) |
| State | Comportamiento | Ciclo de vida del pedido (F6) | `EstadoPedido` (estado); `Recibido`, `EnPreparacion`, `Listo`, `Entregado`, `Cancelado` (estados concretos); `Pedido` (contexto) |
| Facade | Estructural | Arquitectura (RNF-02) | `CafeteriaService`; clientes: `consola.*` y `gui.*` |

Tres categorías: creacional (Factory Method), estructural (Decorator, Facade) y de comportamiento (Strategy, Observer, State).

## Documentación

1. [Alcance y requisitos](docs/01-alcance-y-requisitos.md)
2. [Problemas de diseño y patrones](docs/03-Problemas-y-patrones.md)
3. [Arquitectura](docs/05-Arquitectura.md)
4. [Diagramas UML](docs/06-Uml.md) (Mermaid)
5. [Documento de diseño](docs/07-Documento-de-diseno.md)
