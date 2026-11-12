# Alcance y requisitos — Sistema de Cafetería

**Proyecto:** parcial de Patrones de Diseño (Ingeniería de Software)
**Propuesta elegida:** 3 — Cafetería
**Integrantes:** Samuel Chaparro, Cristian Daniel Blanco, Juan Diego Afanador
**Fecha de entrega:** 23 de noviembre de 2026


---

## 1. Alcance

El sistema permite registrar y gestionar pedidos de una cafetería. Se ejecuta por consola y mediante una interfaz gráfica sencilla, ambas sobre el mismo núcleo de negocio.

### 1.1 Funcionalidades incluidas

| Código | Funcionalidad | Descripción |
|---|---|---|
| F1 | Catálogo de productos | Consulta de bebidas (café, té, infusiones, bebidas frías) y alimentos (panadería, postres, sándwiches) con nombre, categoría y precio base. |
| F2 | Personalización de bebidas | Cada bebida admite tamaño, tipo de leche y extras (shot de espresso, crema, jarabe, canela). Cada opción modifica el precio y la descripción. |
| F3 | Gestión de pedidos | Crear un pedido, agregar y quitar ítems, ajustar cantidades y consultar el subtotal. |
| F4 | Descuentos | Aplicar una política de descuento al pedido (sin descuento, porcentaje por cliente frecuente, combo bebida + alimento, código promocional). |
| F5 | Pago | Cobrar el pedido con un método de pago (efectivo, tarjeta, billetera digital) y validar los datos propios de cada método. |
| F6 | Ciclo de vida del pedido | El pedido pasa por los estados Recibido, En preparación, Listo y Entregado, o Cancelado. |
| F7 | Notificación de estado | Cada cambio de estado se informa a los interesados (pantalla de cocina, cliente, registro de actividad). |
| F8 | Ticket | Generación del resumen del pedido, con detalle, descuento, total y método de pago, en texto plano o en formato resumido. |
| F9 | Historial de pedidos | Consulta de los pedidos registrados en la sesión, con filtro por estado. |
| F10 | Doble interfaz | Todas las funciones anteriores se pueden usar desde la consola y desde la interfaz gráfica. |

### 1.2 Funcionalidades excluidas

| Funcionalidad | Razón |
|---|---|
| Persistencia en base de datos | Añade configuración y código de acceso a datos que no aportan al objetivo de patrones. Los datos viven en memoria durante la ejecución. |
| Autenticación y roles de usuario | No es necesaria para demostrar los patrones y amplía el alcance de seguridad. |
| Pasarelas de pago reales | Requieren credenciales y servicios externos. Los pagos se simulan. |
| Envío real de notificaciones (correo, SMS, push) | Depende de servicios externos. Las notificaciones se muestran en consola, en la interfaz y en un registro. |
| Control de inventario y proveedores | Es un dominio aparte, con reglas propias que exceden el tiempo disponible. |
| Reportes contables y de ventas | No aportan variación de diseño relevante frente al historial. |
| Reservas de mesa y pedidos a domicilio | Son flujos distintos que duplicarían el esfuerzo sin cubrir patrones nuevos. |
| Interfaz web o móvil | Fuera del alcance: la interfaz gráfica es de escritorio y sencilla. |

### 1.3 Áreas de variación

Son las partes donde se esperan cambios frecuentes y que el diseño debe poder extender sin tocar el código existente.

1. **Productos y su creación (F1).** Se agregan con frecuencia bebidas y alimentos nuevos, con distinta forma de preparación y distintos atributos.
2. **Personalizaciones y extras de bebida (F2).** Aparecen extras, tipos de leche y tamaños nuevos, que se combinan libremente y alteran precio y descripción.
3. **Políticas de descuento (F4).** Las promociones cambian por temporada, cliente o campaña.
4. **Métodos de pago (F5).** Se suman medios de pago, cada uno con sus propias validaciones y comisiones.
5. **Canales o destinatarios de notificación (F7).** Pueden añadirse nuevos receptores del cambio de estado sin alterar el pedido.
6. **Formatos de ticket (F8).** Pueden requerirse nuevas presentaciones del mismo contenido.

### 1.4 Realismo del alcance

El alcance es abordable por tres estudiantes en pocas semanas: un único dominio, datos en memoria y sin servicios externos. Las áreas de variación permiten aplicar patrones de al menos dos categorías (creacionales, estructurales y de comportamiento) sin sobredimensionar el proyecto.

---

## 2. Requisitos

### 2.1 Requisitos funcionales

| ID | Requisito | Func. |
|---|---|---|
| RF-01 | El sistema debe listar el catálogo de productos agrupado por categoría, mostrando nombre y precio base. | F1 |
| RF-02 | El sistema debe permitir consultar el detalle de un producto del catálogo. | F1 |
| RF-03 | El sistema debe permitir elegir tamaño (pequeño, mediano, grande) para una bebida. | F2 |
| RF-04 | El sistema debe permitir agregar a una bebida uno o más extras y un tipo de leche, y mostrar la descripción y el precio resultantes. | F2 |
| RF-05 | El sistema debe permitir crear un pedido nuevo, agregar productos con su cantidad y quitar ítems antes del pago. | F3 |
| RF-06 | El sistema debe calcular y mostrar el subtotal del pedido, actualizado tras cada cambio. | F3 |
| RF-07 | El sistema debe permitir aplicar al pedido una política de descuento y mostrar el monto descontado y el total. | F4 |
| RF-08 | El sistema debe rechazar un código promocional inválido y mantener el pedido sin cambios. | F4 |
| RF-09 | El sistema debe permitir pagar un pedido con efectivo, tarjeta o billetera digital. | F5 |
| RF-10 | El sistema debe validar los datos del método elegido (monto recibido suficiente en efectivo; formato de número en tarjeta; identificador en billetera) y rechazar el pago si no son válidos. | F5 |
| RF-11 | El sistema debe impedir el pago de un pedido vacío o ya pagado. | F5 |
| RF-12 | El sistema debe gestionar los estados Recibido, En preparación, Listo, Entregado y Cancelado, y permitir solo las transiciones válidas. | F6 |
| RF-13 | El sistema debe notificar cada cambio de estado a todos los receptores registrados (cocina, cliente, registro). | F7 |
| RF-14 | El sistema debe permitir agregar o quitar receptores de notificación durante la ejecución. | F7 |
| RF-15 | El sistema debe generar el ticket del pedido con ítems, extras, descuento, total y método de pago, en al menos dos formatos. | F8 |
| RF-16 | El sistema debe listar los pedidos registrados y permitir filtrarlos por estado. | F9 |
| RF-17 | El sistema debe ofrecer todas las operaciones RF-01 a RF-16 tanto por consola como por la interfaz gráfica. | F10 |

### 2.2 Requisitos no funcionales

| ID | Categoría | Requisito |
|---|---|---|
| RNF-01 | Tecnología | El sistema debe desarrollarse en Java (versión por confirmar, ver sección 3) y compilar y ejecutarse con una herramienta de construcción estándar. |
| RNF-02 | Arquitectura | La consola y la interfaz gráfica deben usar el mismo núcleo de negocio. Ninguna clase del núcleo debe depender de clases de presentación. |
| RNF-03 | Estructura | El código debe organizarse en paquetes con responsabilidades claras (por ejemplo: modelo, servicios o núcleo, consola, interfaz gráfica, pruebas), sin dependencias cíclicas entre ellos. |
| RNF-04 | Extensibilidad | Agregar un nuevo producto, extra, descuento, método de pago, receptor de notificación o formato de ticket debe hacerse creando clases nuevas, sin modificar el código existente de esa área. |
| RNF-05 | Pruebas | La lógica de negocio debe contar con pruebas unitarias automatizadas que cubran, como mínimo, cálculo de precios, descuentos, validación de pagos y transiciones de estado. Deben ejecutarse sin interfaz. |
| RNF-06 | Mantenibilidad | El código debe seguir convenciones de nombres de Java, con clases de responsabilidad única y documentación breve en las interfaces públicas. |
| RNF-07 | Usabilidad | La consola debe mostrar menús numerados y mensajes de error claros ante entradas inválidas, sin cerrarse. La interfaz gráfica debe permitir completar un pedido de principio a fin sin instrucciones previas. |
| RNF-08 | Datos | El sistema debe funcionar sin base de datos ni servicios externos, con datos en memoria. |
| RNF-09 | Trazabilidad | Cada patrón de diseño aplicado debe poder ubicarse en el código y asociarse con un área de variación de la sección 1.3. |

