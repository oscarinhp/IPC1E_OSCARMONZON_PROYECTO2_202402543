# Informe de desarrollo

**Proyecto:** Parque Residencial: Sistema de Gestión de Parqueo

**Estudiante responsable:** Oscar Monzón · 202402543 · IPC1, sección E

**Cierre de esta documentación:** 29 de octubre de 2026.

## 1. Objetivo y alcance

Integrar listas enlazadas propias, dos colas FIFO, una pila LIFO, cinco hilos
de garitas, persistencia en texto y seis paneles Swing. Este informe registra
el trabajo observable en Git y las verificaciones realizadas; no certifica
que todos los casos posibles estén libres de errores ni cumplimiento académico total.

## 2. Plan y avance real

Se organizaron 16 etapas: base, estructuras, modelo, parqueo, concurrencia,
persistencia, interfaz, simulación, ejecutable y documentación. Se añadieron
dos correcciones independientes al detectar problemas. El calendario inicial
de `PLAN_COMMITS.md` fue orientativo; se adelantaron etapas para documentar
antes del 31 de octubre. No se alteraron fechas de commits.

Registro obtenido de `git log --reverse --date=iso-strict`; horas locales UTC−06:00:

| Etapa | Fecha y hora | Commit | Resultado |
|---|---|---|---|
| 1 | 25/10 09:54 | `e305a32` | Paquetes, build y ventana base |
| 2 | 25/10 09:56 | `cfc0a9f` | Nodos, lista simple y doble |
| 3 | 25/10 16:28 | `4e758f8` | Cola y pila propias |
| 4 | 25/10 17:24 | `3e0bd6a` | Residentes, vehículos y reglas |
| 5 | 26/10 10:40 | `fd98274` | Parqueo y zonas, 150 espacios |
| 6 | 26/10 11:13 | `b69b72d` | Cinco garitas y sincronización |
| 7 | 26/10 12:58 | `fc659c3` | Carga y guardado en texto |
| 8 | 26/10 17:45 | `920829b` | Ventana y Residentes |
| Corrección | 26/10 17:49 | `e15685d` | Importación del proyecto Java en VS Code |
| 9 | 27/10 09:05 | `2a69441` | Vehículos por residente |
| 10 | 27/10 22:48 | `95bf64f` | Mapa del parqueo |
| 11 | 28/10 10:55 | `18fb23e` | Entrada, Salida y visitantes |
| Corrección | 28/10 11:01 | `60fbd6c` | Evitar NPE al mostrar garitas aún no creadas |
| 12 | 28/10 17:10 | `f5c74b1` | Eventos y observador Swing |
| 13 | 28/10 23:10 | `21e9637` | Botones de simulación |
| 14 | 29/10 08:39 | `12d01ba` | JAR y archivos de ejemplo |
| 15 | 29/10 | `b205293` | Manual técnico y diagramas |
| 16 | 29/10 | Commit que incorpora este informe | Manual de usuario, capturas, pruebas e índice final |

Las etapas no equivalen al número total de commits: hay correcciones adicionales.
Las fechas/horas exactas de los últimos commits pueden consultarse en GitHub o
con `git log --date=iso-strict --format="%h %ad %s"`.

## 3. Decisiones de implementación

1. **Dominio enlazado:** lista doble de residentes, lista simple por residente;
   cada estructura usa nodos propios, no colecciones de Java.
2. **Parqueo:** lista doble de filas con listas simples de espacios y referencias
   directas arriba/abajo en cada espacio. Total: `3×25 + 5×15 = 150`.
3. **Asignación por zona:** socios solo A–C; no socios y visitantes solo E–I.
   No hay desbordamiento de socios a general; se documenta la regla elegida.
4. **Colas compartidas:** tres consumidores de entrada y dos de salida, sin
   crear una cola por garita. Espera bloqueante con `while`/`wait` y `notifyAll`.
5. **Swing manual:** los paneles consultan el sistema y las garitas notifican un
   observador que agenda el refresco con `SwingUtilities.invokeLater`.
6. **Persistencia sencilla:** dos archivos UTF-8 separados por `|`; se sigue el
   formato sugerido de cinco campos para vehículos, sin extenderlo para el tipo.
7. **Documentación separada:** herramientas de prueba/captura bajo `docs/herramientas`,
   fuera de `src/`, para no incorporarlas al ejecutable de la aplicación.

## 4. Problemas encontrados y soluciones aplicadas

| Problema observado | Causa o análisis | Medida aplicada |
|---|---|---|
| `javac` interpretaba rutas `\U` | Rutas Windows en el archivo de argumentos | Generar rutas con `/` en `build.bat` |
| Tres errores del editor al importar `vista` | Configuración/caché del proyecto Java | `.project`, `.classpath`, configuración automática y recarga del workspace |
| La aplicación no abría tras agregar Entrada/Salida | Los paneles consultaban garitas antes de `iniciar()` | Control de referencias nulas y etiqueta «pendiente», commit `60fbd6c` |
| JAR aparecía en preparaciones de commits intermedios | Artefacto generado/selección del IDE | Ignorar `dist/`, revisar el índice y añadir el JAR de entrega explícitamente en la etapa 14 |
| Archivos de datos inexistentes al inicio | Aún no se habían añadido ejemplos | Carga con avisos; ocho residentes y ocho vehículos en la etapa 14 |
| Calendario inicial quedó desactualizado | Se adelantó el trabajo a petición del usuario | Registrar fechas reales sin reescribir el historial |

## 5. Verificación final realizada

Se compiló el proyecto con `build.bat` en Windows y JDK **17.0.20.1**.
Además se extrajo, sin modificarlo, el JAR versionado en `12d01ba` y se ejecutaron
las herramientas de documentación contra sus clases, no contra dobles de prueba.

**Resultado: 31 comprobaciones correctas**, incluyendo:

- referencias de listas tras eliminación, recorrido inverso y vaciado;
- FIFO, LIFO, espera de cola vacía y despertar del consumidor;
- cuarto vehículo, placa repetida y solicitudes duplicadas;
- protección de residentes activos, zonas de socios y visitantes;
- 150 espacios, enlaces verticales y rechazo con zona general llena;
- guardado y reconstrucción en una carpeta temporal, excluyendo visitantes;
- nueve ingresos y salidas con cinco garitas, sin espacios duplicados en esa ejecución.

Se generaron **siete capturas reales**: seis pestañas y el resumen de simulación.
No se guardaron datos de la sesión de capturas. Las pruebas escriben sus copias
solo en una subcarpeta temporal nueva.

Evidencia, comandos reproducibles y alcance:
[Resultado de verificación](pruebas/Resultado_Verificacion.md).
Las pruebas no son una demostración formal de ausencia de condiciones de carrera
ni cubren exhaustivamente todas las acciones de los botones.

## 6. Limitaciones detectadas y no corregidas en estas etapas documentales

Los commits 15–16 no cambian la lógica del programa. Se registran las siguientes
limitaciones para no presentar funciones inexistentes como terminadas:

- El alta/edición de residentes puede dejar tablas/selectores desactualizados;
  se puede releer la tabla con **Ver todos** o guardar/cerrar/reabrir para selectores.
- El estado visible de las garitas no se refresca en cada fase del trámite.
- El tipo de vehículo se pierde al recargar el formato de cinco campos.
- El guardado no es atómico y el cierre continúa si ocurre un error de escritura.
- La validación de campos de visitantes y edición de residentes no es exhaustiva.
- El listado de «estacionados» incluye vehículos que esperan salida; el simulador
  puede intentar repetir una solicitud y mostrar un rechazo en el resumen.
- La opción sin filtro de Eventos se muestra en blanco y el límite precede al filtro.
- El tema nativo de Windows atenúa los fondos de los botones del parqueo; los
  colores pueden verse en el borde. Tooltips, detalles y contadores complementan el mapa.
- No hay deshacer, pausa/reanudación ni ajuste de velocidad (funciones opcionales).

Se detallan en el [manual técnico](Manual_Tecnico.md) y sus precauciones de uso
en el [manual de usuario](Manual_Usuario.md). Las correcciones funcionales futuras
deberán probarse y reflejarse en un JAR nuevo antes de entregar otra versión.

### Corrección posterior: refresco al registrar y editar residentes

Después de las etapas 15–16 se corrigió la falta de avisos al registrar o editar
residentes: `SistemaParqueo` ahora llama a `notificarCambio()` en ambas operaciones.
La tabla y el selector de Vehículos se actualizan mediante el observador en el
EDT sin reiniciar, conservando la selección del residente.

Se actualizó el JAR y se verificó el caso R015 / Oscar Lopez con diez
comprobaciones nuevas de `VerificarRefrescoResidentes`; también volvieron a pasar
las 31 comprobaciones generales contra los datos de ejemplo versionados.
El resto de limitaciones de esta sección no se considera corregido por este cambio.

## 7. Asistencia de IA, detector y preparación académica

Durante este desarrollo se utilizó un asistente de IA para generar y modificar
código, ejecutar pruebas y redactar documentación. No se declara autoría
exclusivamente humana ni se oculta esa asistencia. El estudiante debe revisar
los archivos y consultar al docente sobre su admisibilidad: la rúbrica advierte
de penalizaciones por uso mayoritario de IA. Un detector no sustituye esa regla
ni acredita por sí mismo autoría u originalidad.

**Pendiente:** obtener el reporte real de [aicodedetector.org](https://aicodedetector.org/),
que aparece en la hoja de calificación. No se envió código a ese servicio durante
esta etapa y no hay porcentajes ni certificaciones inventadas. Ver
[estado del reporte y pasos para adjuntarlo](Reporte_AI_Code_Detector.md).

Antes de la evaluación, el estudiante debe poder localizar y explicar:

| Tema | Archivos y operaciones a estudiar |
|---|---|
| Lista doble y simple | `ListaDoble.eliminar`, referencias vecinas; `Residente.agregarVehiculo` |
| Lista de listas | `Parqueo.enlazarEspaciosVerticales`, `asignarEspacio`, `liberarEspacio` |
| Colas y pila | `Cola.encolar/desencolar`, `Pila.apilar/desapilar/aArreglo` |
| Concurrencia | `AdministradorGaritas`, bucles `run`, monitor de la cola y EDT |
| Persistencia | `GestorArchivos.cargar/guardar`, asociaciones por ID y exclusión de visitas |

## 8. Lista de entrega

- [x] Código fuente organizado y JAR versionado con datos de ejemplo.
- [x] Manual técnico y diagramas.
- [x] Manual de usuario con capturas reales de seis paneles y simulación.
- [x] Informe de etapas, avances, problemas, soluciones y limitaciones.
- [x] Pruebas reproducibles y resultado documentado.
- [ ] Reporte real de AI Code Detector: no se sustituye por la guía incluida.
- [ ] Confirmar invitación/aceptación de `EduardoAjsivinac` como colaborador.
- [ ] Revisar asistencia de IA con el docente y preparar explicación/modificación presencial.

No se afirma que la entrega académica esté totalmente cerrada mientras falten
las verificaciones externas anteriores.
