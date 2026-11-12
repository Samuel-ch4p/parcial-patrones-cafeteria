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
