# Capturas reales de la aplicación

Tomadas el **29/10/2026**, Windows, OpenJDK 17.0.20.1, con `java.awt.Robot`.
No son maquetas ni recreaciones HTML. Se capturó la ventana Swing real cargada
desde el JAR versionado en el commit `12d01ba`.

| Archivo | Contenido |
|---|---|
| [01-residentes.png](01-residentes.png) | Tabla de ocho residentes y detalle de R001 |
| [02-vehiculos.png](02-vehiculos.png) | Dos vehículos del residente y contador 2/3 |
| [03-parqueo.png](03-parqueo.png) | 150 espacios, quince ocupados: tres socios y doce visitantes |
| [04-entrada.png](04-entrada.png) | Cola de espera, formulario de ejemplo y tres garitas atendiendo |
| [05-salida.png](05-salida.png) | Cola, visitantes activos y dos garitas atendiendo |
| [06-eventos.png](06-eventos.png) | Historial LIFO tras ingresos y salidas |
| [07-simulacion.png](07-simulacion.png) | Diálogo del botón real «Simular 10 movimientos» |

## Preparación de la sesión

La herramienta carga `datos/` solo para lectura; omite el diálogo inicial de
avisos al construir la misma `VentanaPrincipal`. Precarga tres solicitudes de
residentes y doce de visitantes para hacer visibles colas y garitas. Después
solicita ocho salidas y ejecuta el botón real de simulación. Los datos de prueba
están preparados automáticamente; esto no pretende evidenciar clics manuales
de registro para cada fila. La ventana se redimensiona al área útil de pantalla
y se pide un refresco antes de cada toma para mostrar el estado del modelo.

No se llama a `guardar()`, ni se dispara el cierre con guardado. Se detienen
solo las garitas y ventanas de la sesión de captura. Los píxeles se guardan
directamente como PNG, sin edición de su contenido. El orden de atención y las
placas aleatorias pueden variar entre ejecuciones.

Para reproducirlas, ver [herramientas de documentación](../herramientas/README.md).
No mover ventanas ni utilizar el escritorio mientras se toman las imágenes.
