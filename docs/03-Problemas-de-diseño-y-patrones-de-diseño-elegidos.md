# 11/11 Problemas de diseño y patrones de diseño seleccionados

## 3. Problemas de diseño identificados

Durante el análisis del sistema se identificaron cinco situaciones que podrían generar problemas de mantenimiento y escalabilidad si se implementan utilizando soluciones demasiado rígidas. Cada una representa una necesidad del proyecto en la que es importante organizar correctamente el código para facilitar la incorporación de nuevas funcionalidades.

### Problema 1. Creación de productos con atributos diferentes (área: productos, F1)

**Situación:**

El catálogo del sistema contiene diferentes tipos de productos, como cafés, tés, infusiones, bebidas frías, panes, postres y sándwiches. Cada categoría tiene características diferentes. Por ejemplo, una bebida necesita atributos como tamaño y tipo de leche, mientras que un sándwich no requiere esa información.

Además, los productos deben crearse a partir de un menú configurable, permitiendo agregar nuevas opciones sin modificar constantemente el código existente.

**Diseño ingenuo:**

Una solución inicial sería utilizar una clase `Menu` o `PedidoService` con un método encargado de crear todos los productos mediante condicionales:

```java
if (tipo.equals("CAFE")) {
    return new Cafe(...);
} else if (tipo.equals("TE")) {
    return new Te(...);
} else if (tipo.equals("SANDWICH")) {
    return new Sandwich(...);
}
```

**Por qué falla:**

Cada vez que se agregue un producto nuevo, como una limonada o un croissant, será necesario modificar la misma clase para incluir otra condición. Esto provoca que la clase acumule responsabilidades y dependa directamente de las implementaciones concretas de cada producto.

Además, se incumple el requisito RNF-04, que busca permitir la extensión del sistema sin modificar el código existente.

### Problema 2. Personalizaciones que se pueden combinar libremente (área: extras, F2)

**Situación:**

El sistema debe permitir personalizar las bebidas mediante diferentes extras. Por ejemplo, un cliente puede solicitar un cappuccino mediano con leche de almendra, doble shot de espresso y canela. Cada personalización modifica el precio y la descripción de la bebida.

Las combinaciones pueden variar dependiendo de las preferencias del cliente y también pueden aparecer nuevos extras en el futuro.

**Diseño ingenuo:**

Una alternativa sería crear una subclase para cada combinación posible:

```java
class CafeConLecheYCanela extends Cafe { ... }
class CafeConShotYCrema extends Cafe { ... }
class CafeConLecheShotYCanela extends Cafe { ... }
```

Otra posibilidad sería manejar todas las opciones mediante variables booleanas dentro de una sola clase:

```java
class Bebida {
    boolean tieneCanela;
    boolean tieneCrema;
    boolean tieneShot;
    boolean tieneLecheAlmendra;

    double calcularPrecio() {
        // Muchos condicionales para calcular los extras
    }
}
```

**Por qué falla:**

El problema es que las combinaciones aumentan rápidamente. Si existen cuatro extras, tres tipos de leche y tres tamaños, la cantidad de posibilidades crece considerablemente.

Con herencia, sería necesario crear demasiadas subclases. Con variables booleanas, la clase `Bebida` terminaría concentrando muchas condiciones y responsabilidades. En ambos casos, agregar una nueva personalización haría más difícil mantener el sistema.

### Problema 3. Políticas de descuento que cambian según las promociones (área: descuentos, F4)

**Situación:**

El sistema debe manejar diferentes políticas de descuento, como descuentos para clientes frecuentes, promociones por comprar una bebida y un alimento, códigos promocionales y pedidos sin descuento.

Estas reglas pueden cambiar dependiendo de las campañas o temporadas del establecimiento, por lo que deben poder actualizarse sin afectar el cálculo general de los pedidos.

**Diseño ingenuo:**

Se podría implementar un método `calcularTotal()` dentro de la clase `Pedido`, utilizando condicionales para identificar qué descuento corresponde:

```java
if (tipoDescuento.equals("CLIENTE_FRECUENTE")) {
    // Aplicar descuento al cliente frecuente
} else if (tipoDescuento.equals("COMBO")) {
    // Aplicar descuento por combo
} else if (tipoDescuento.equals("CODIGO")) {
    // Aplicar código promocional
} else {
    // Sin descuento
}
```

**Por qué falla:**

Cada nueva promoción obligaría a modificar el método `calcularTotal()` de la clase `Pedido`. Esto hace que la clase conozca todas las reglas de descuento y acumule lógica que debería estar separada.

También dificulta realizar pruebas individuales, porque todas las políticas están mezcladas en un mismo método.

### Problema 4. Estados del pedido con transiciones restringidas (área: ciclo de vida, F6)

**Situación:**

Un pedido pasa por diferentes estados durante su ciclo de vida: recibido, en preparación, listo, entregado o cancelado. Sin embargo, no todos los cambios están permitidos.

Por ejemplo, un pedido entregado no puede regresar al estado de preparación ni ser cancelado. Tampoco se deberían agregar nuevos productos cuando el pedido ya se encuentra en preparación.

**Diseño ingenuo:**

Se podría utilizar una variable `estado` de tipo `String` o `enum` y agregar condicionales dentro de cada método para controlar las acciones permitidas:

```java
public void cancelar() {
    if (estado.equals("ENTREGADO")) {
        throw new IllegalStateException();
    }
    estado = "CANCELADO";
}

public void agregarItem(Item item) {
    if (estado.equals("EN_PREPARACION")) {
        throw new IllegalStateException();
    }
    items.add(item);
}
```

**Por qué falla:**

Las reglas de transición quedan distribuidas entre diferentes métodos de la clase `Pedido`. Si se agrega un nuevo estado, como `PAGO_PENDIENTE`, será necesario revisar varios métodos para actualizar las condiciones correspondientes.

Esto aumenta la posibilidad de cometer errores y hace que la lógica del ciclo de vida sea más difícil de mantener.

### Problema 5. Notificaciones de cambios de estado a diferentes receptores (área: notificaciones, F7)

**Situación:**

Cuando un pedido cambia de estado, por ejemplo, cuando pasa a estar listo, es necesario informar a diferentes componentes del sistema. Entre ellos están la pantalla de cocina, el cliente y el registro de actividad.

Además, el sistema debe permitir agregar o quitar receptores durante la ejecución, como lo establece el requisito RF-14.

**Diseño ingenuo:**

Una solución sería hacer que la clase `Pedido` llame directamente a cada componente encargado de recibir las notificaciones:

```java
public void cambiarEstado() {
    estado = "LISTO";

    cocina.mostrar();
    cliente.avisar();
    registro.guardar();
}
```

**Por qué falla:**

La clase `Pedido` queda directamente relacionada con todos los receptores de notificaciones. Si se agrega un nuevo componente, será necesario modificar nuevamente esa clase.

Esto genera un alto acoplamiento y dificulta realizar pruebas independientes, ya que el pedido necesita conocer los componentes encargados de recibir los avisos.

---
## 4. Patrones de diseño elegidos

Para solucionar los problemas identificados se seleccionaron cinco patrones de diseño. Cada uno responde a una necesidad específica del sistema y permite mejorar su organización, mantenimiento y capacidad de ampliación.

### Patrón 1. Factory Method (creacional) → Problema 1

**Qué resuelve:**

Centraliza la creación de los productos y evita que las demás partes del sistema dependan directamente de las clases concretas.

**Por qué se eligió:**

Se eligió porque cada categoría de producto tiene características y necesidades diferentes durante su creación. Con este patrón, cada fábrica se encarga de construir los productos correspondientes a su categoría.

Por ejemplo, `BebidaFactory` puede encargarse de crear cafés, tés y bebidas frías, mientras que `AlimentoFactory` puede crear sándwiches, panes y postres.

De esta manera, agregar una nueva categoría no obliga a modificar las fábricas existentes.

**Alternativa considerada:**

Se consideró utilizar una fábrica simple con un `switch` o varios `if/else` para identificar el producto que se debe crear. Se descartó porque seguiría siendo necesario modificar esa clase cada vez que se agregue un producto nuevo.

También se evaluó Abstract Factory, pero no se consideró necesario porque el sistema no necesita crear familias de productos relacionados que deban utilizarse conjuntamente.

**Consecuencias:**

- **Ventaja:** Permite agregar nuevos productos sin modificar las clases encargadas de crear los existentes y mantiene separada la lógica de creación.
- **Desventaja:** Aumenta la cantidad de clases y agrega cierta complejidad para un catálogo pequeño.

**Clases tentativas:**

| Rol | Clase |
|---|---|
| Product | Producto |
| ConcreteProduct | Bebida, Alimento y sus variantes |
| Creator | ProductoFactory |
| ConcreteCreator | BebidaFactory, AlimentoFactory |

### Patrón 2. Decorator (estructural) → Problema 2

**Qué resuelve:**

Permite agregar funcionalidades y características adicionales a una bebida en tiempo de ejecución, sin necesidad de modificar su clase original.

**Por qué se eligió:**

Se eligió porque las personalizaciones pueden combinarse libremente. Cada extra funciona como un decorador que envuelve la bebida original y agrega su propio precio y descripción.

Por ejemplo, un café puede recibir primero leche de almendra, después un shot adicional y finalmente canela. Cada elemento se agrega de manera independiente.

**Alternativa considerada:**

Se consideró utilizar herencia para crear una subclase por cada combinación de extras, pero esto produciría una gran cantidad de clases.

También se evaluó utilizar variables booleanas dentro de la clase `Bebida`, pero esta alternativa concentraría demasiadas condiciones en un solo lugar y dificultaría su mantenimiento.

**Consecuencias:**

- **Ventaja:** Permite combinar diferentes extras y agregar nuevas personalizaciones sin modificar las clases originales de las bebidas.
- **Desventaja:** Se generan varios objetos pequeños que se envuelven entre sí, por lo que puede ser más difícil seguir la construcción de una bebida con muchas personalizaciones.

**Clases tentativas:**

| Rol | Clase |
|---|---|
| Component | Bebida (interfaz) |
| ConcreteComponent | Cafe, Te, BebidaFria |
| Decorator | ExtraDecorator |
| ConcreteDecorator | ShotEspresso, Crema, Jarabe, Canela, LecheDeAlmendra |

### Patrón 3. Strategy (comportamiento) → Problema 3

**Qué resuelve:**

Permite encapsular diferentes políticas de descuento en clases independientes e intercambiarlas según las necesidades del pedido.

**Por qué se eligió:**

Se eligió porque las promociones pueden cambiar constantemente y cada una tiene reglas diferentes. Con Strategy, la clase `Pedido` no necesita conocer cómo funciona cada descuento, sino que utiliza una interfaz común para calcularlo.

Así, cuando se agregue una nueva promoción, solamente será necesario implementar una nueva estrategia, sin modificar las políticas existentes.

**Alternativa considerada:**

Se consideró implementar una cadena de condicionales dentro de `Pedido`, pero esto provocaría que cada nueva promoción obligara a modificar la misma clase.

También se evaluó utilizar Decorator para acumular descuentos, pero se descartó porque el alcance actual establece que cada pedido utiliza una sola política de descuento.

**Consecuencias:**

- **Ventaja:** Las reglas de descuento quedan separadas, facilitando su mantenimiento, ampliación y prueba individual.
- **Desventaja:** Se necesita una clase por cada política y el sistema debe seleccionar la estrategia correspondiente para cada pedido.

**Clases tentativas:**

| Rol | Clase |
|---|---|
| Strategy | PoliticaDescuento (interfaz) |
| ConcreteStrategy | SinDescuento, DescuentoClienteFrecuente, DescuentoCombo, DescuentoCodigoPromocional |
| Context | Pedido |

**Nota:** El mismo patrón también puede utilizarse para los métodos de pago (F5) y los formatos de ticket (F8). Sin embargo, se considera una reutilización de Strategy y no una aplicación adicional dentro de la selección principal.

### Patrón 4. State (comportamiento) → Problema 4

**Qué resuelve:**

Permite representar los diferentes estados del pedido mediante clases independientes, donde cada una define las acciones permitidas y las transiciones disponibles.

**Por qué se eligió:**

Se eligió porque cada estado del pedido tiene reglas diferentes. Por ejemplo, un pedido recibido puede permitir agregar productos, mientras que uno en preparación ya no debería permitir modificaciones.

Con State, estas reglas quedan dentro de las clases que representan cada estado, evitando que toda la lógica se concentre en la clase `Pedido`.

**Alternativa considerada:**

Se consideró utilizar un `enum` acompañado de una tabla de transiciones permitidas. Aunque sería una solución más sencilla, se descartó porque no permite organizar de la misma manera el comportamiento específico de cada estado.

Con State es posible controlar tanto las transiciones como las acciones que se pueden realizar en cada etapa.

**Consecuencias:**

- **Ventaja:** Las reglas de cada estado quedan organizadas de forma independiente y se reduce el uso de condicionales dentro de `Pedido`.
- **Desventaja:** Se necesitan varias clases para representar los estados, lo que aumenta la cantidad de código y distribuye la lógica entre diferentes archivos.

**Clases tentativas:**

| Rol | Clase |
|---|---|
| Context | Pedido |
| State | EstadoPedido (interfaz) |
| ConcreteState | Recibido, EnPreparacion, Listo, Entregado, Cancelado |

### Patrón 5. Observer (comportamiento) → Problema 5

**Qué resuelve:**

Permite notificar automáticamente a diferentes componentes cuando ocurre un cambio en el pedido, sin que la clase principal tenga que conocer directamente a cada receptor.

**Por qué se eligió:**

Se eligió porque el sistema necesita informar a varios componentes cuando cambia el estado de un pedido. Con Observer, cada receptor puede suscribirse para recibir las notificaciones y también puede dejar de recibirlas cuando sea necesario.

Por ejemplo, la pantalla de cocina, el cliente y el registro de actividad pueden implementar la misma interfaz de observador y recibir las actualizaciones del pedido.

Esto permite cumplir con el requisito RF-14 y facilita agregar nuevos receptores sin modificar la clase `Pedido`.

**Alternativa considerada:**

Se consideró realizar llamadas directas desde `Pedido` hacia cada receptor, pero esto generaría dependencias innecesarias.

También se evaluó utilizar un bus de eventos centralizado, pero se descartó porque sería una solución más compleja para las necesidades actuales del proyecto.

**Consecuencias:**

- **Ventaja:** Reduce el acoplamiento entre los componentes y permite agregar o quitar observadores durante la ejecución.
- **Desventaja:** El orden de las notificaciones no está garantizado y es necesario controlar los posibles errores de los receptores para evitar que afecten a los demás.

**Clases tentativas:**

| Rol | Clase |
|---|---|
| Subject | Pedido |
| Observer | ObservadorPedido (interfaz) |
| ConcreteObserver | PantallaCocina, NotificacionCliente, RegistroActividad |

---

## 3. Resumen de los patrones seleccionados

| N.º | Problema | Patrón | Categoría | Área de variación |
|---|---|---|---|---|
| 1 | Creación de productos | Factory Method | Creacional | Productos (F1) |
| 2 | Personalización de bebidas | Decorator | Estructural | Extras (F2) |
| 3 | Aplicación de descuentos | Strategy | Comportamiento | Descuentos (F4) |
| 4 | Control de estados del pedido | State | Comportamiento | Ciclo de vida (F6) |
| 5 | Notificaciones de cambios | Observer | Comportamiento | Notificaciones (F7) |

## 4. Aspectos pendientes por confirmar

Para definir el alcance final del análisis, se tuvieron en cuenta los siguientes supuestos:

- Los métodos de pago (F5) y los formatos de ticket (F8) quedan fuera de la selección principal, ya que también podrían implementarse mediante Strategy.
- Se cubren tres categorías de patrones: creacionales, estructurales y de comportamiento, superando el mínimo solicitado de dos.
- Se asume que los descuentos no se acumulan entre sí y que cada pedido utiliza una sola política.

**Por validar con la docente:**

- Si es suficiente trabajar con las cinco áreas seleccionadas o si también se deben incluir los pagos y los formatos de ticket.
- Si se mantiene el alcance de las seis u ocho áreas de variación planteadas inicialmente o si se puede trabajar con una selección reducida.

## 5. Conclusión

Los patrones seleccionados permiten resolver diferentes problemas de diseño que pueden aparecer durante el desarrollo del sistema. Cada uno ayuda a separar responsabilidades y evita que la incorporación de nuevas funcionalidades obligue a modificar constantemente el código existente.

De esta manera, se busca construir un sistema más organizado, flexible y fácil de mantener, que pueda adaptarse a nuevos productos, personalizaciones, promociones y cambios en el funcionamiento de los pedidos.
