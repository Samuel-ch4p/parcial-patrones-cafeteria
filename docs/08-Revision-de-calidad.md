# 08 — Revisión de calidad

**Responsable:** Juan Diego Afanador · **Fecha:** 23 de noviembre de 2026

Después de terminar las funcionalidades (F1 a F10) se hizo una revisión de calidad del código. Se corrigieron 7 problemas, cada uno en un commit independiente con sus pruebas. Las pruebas pasaron de 80 a 90 y no cambió la arquitectura ni los patrones.

## 1. Método

1. **Lectura del código** del núcleo (`pedido`, `descuento`, `pago`, `extras`, `producto`, `notificacion`, `ticket`, `servicio`), de la consola y de la GUI.
2. **Reproducción** de cada caso sospechoso ejecutando el código, para confirmar que era un error real antes de corregirlo.
3. **Corrección mínima**: solo la clase afectada, sin cambiar la arquitectura, los patrones ni el comportamiento que el resto del sistema espera.
4. **Pruebas**: cada corrección agrega pruebas JUnit y se ejecuta toda la suite antes del commit.
5. **Commit independiente** por corrección, con un mensaje que explica qué cambió y por qué.

## 2. Problemas corregidos

| Commit | Problema | Corrección | Archivos |
|---|---|---|---|
| `473ba08` | Los montos se leían borrando todos los puntos: `20..000` y `200.00` se aceptaban como 20000. La GUI convertía un texto inválido en 0 y respondía "Monto insuficiente". | `Moneda.parsear` acepta `20000`, `20.000` y `$20.000` y rechaza el resto. La consola y la GUI usan la misma regla y el mismo mensaje. | `Moneda`, `LectorEntrada`, `PanelPago` |
| `e275ede` | Se podía aplicar un código promocional a un pedido sin productos. | `Pedido.aplicarDescuento` lo rechaza. "Quitar descuento" sigue funcionando. | `Pedido` |
| `f9be6da` | Con 60 unidades de un producto, agregar 50 más mostraba "La cantidad debe estar entre 1 y 99", aunque 50 es válido. | El mensaje indica cuántas unidades hay y cuántas se pueden agregar. | `Pedido` |
| `3201e10` | Al registrar, los códigos de productos y opciones se guardaban tal cual, pero al buscar se pasaban a minúsculas: un código con mayúsculas quedaba registrado e inaccesible. | Se guardan en minúsculas y sin espacios, igual que al buscar. | `CatalogoProductos`, `CatalogoOpciones` |
| `88f2bf1` | La sección 4 de `05-Arquitectura.md` tenía dos párrafos repetidos. | Se eliminó la copia. | `docs/05-Arquitectura.md` |
| `b84b853` | Un precio muy alto desbordaba el `long` y producía subtotales negativos. | Cada ítem (precio × cantidad) tiene un máximo de $100.000.000. | `LineaPedido` |
| `fee9e51` | En la GUI, el tamaño, la leche y los extras quedaban marcados después de agregar un producto y se aplicaban al siguiente. | Se reinician después de agregar. | `PanelCatalogo` |

Además se agregó al README cómo abrir, probar y ejecutar el proyecto en NetBeans, incluida la interfaz gráfica.

## 3. Revisado sin hallazgos

- Las transiciones de estado inválidas (Listo → Cancelado, avanzar desde Entregado o Cancelado) se rechazan.
- Los pagos con tarjeta y billetera validan su formato. El pago no se puede repetir ni hacer sobre un pedido cancelado.
- La personalización rechaza alimentos y el descuento combo se recalcula al cambiar los ítems.
- La consola valida rangos e índices en todos los menús.

## 4. Decisiones pendientes para el grupo

Estos casos no son errores técnicos, sino reglas de negocio. No se cambiaron porque afectan el comportamiento que esperan las pruebas existentes o requieren una decisión del grupo.

| Caso | Situación actual | Por qué no se cambió |
|---|---|---|
| Pedido vacío que avanza | Un pedido sin productos puede llegar a Entregado. | Doce pruebas del grupo avanzan pedidos vacíos para probar el flujo de estados. |
| Entrega sin pago | Un pedido puede pasar a Entregado sin haberse pagado. | Los requisitos no exigen pagar antes de preparar o entregar. |
| Cancelación de un pedido pagado | Se permite cancelar después de pagar y el ticket sigue mostrando el pago, sin devolución. | Requiere definir cómo se registra una devolución. |
| Fallo de un receptor | Si un receptor lanza una excepción, los siguientes no reciben el aviso, aunque el estado ya cambió. | Ya figura como mejora futura en el documento 07, sección 9. |
