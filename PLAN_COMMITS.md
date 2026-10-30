# Plan de commits — Proyecto 2

El proyecto se divide en **16 commits semánticos**. La idea es subir mínimo
**2 commits por día** hasta completar el proyecto. Si la fecha de entrega se
adelanta, se pueden subir 3 o más en un día.

## Calendario propuesto originalmente (histórico)

| Día | Fecha | Commits |
|---|---|---|
| 1 | domingo 25/10/2026 | 1 y 2 |
| 2 | lunes 26/10/2026 | 3 y 4 |
| 3 | martes 27/10/2026 | 5 y 6 |
| 4 | miércoles 28/10/2026 | 7 y 8 |
| 5 | jueves 29/10/2026 | 9 y 10 |
| 6 | viernes 30/10/2026 | 11 y 12 |
| 7 | sábado 31/10/2026 | 13 y 14 |
| 8 | domingo 01/11/2026 | 15 y 16 |

> Este calendario fue una guía inicial, no el registro de ejecución. Las etapas
> se adelantaron: código y ejecutable hasta la etapa 14, y documentación 15–16,
> se prepararon antes del 31/10. Consultar las fechas reales de Git en
> [Informe de desarrollo](docs/Informe_Desarrollo.md). No se deben interpretar
> las fechas de la tabla como fechas reales ni como autorización para entregar tarde.

## Commits

- [x] **1. `chore: estructura inicial, build y README`**  
  Paquetes base, `.gitignore`, scripts `build.bat`/`build.sh`, `run.bat`,
  configuración de VS Code, ventana inicial y plan de trabajo.

- [x] **2. `feat(estructuras): nodos, lista doble y lista simple`**  
  `NodoSimple`, `NodoDoble`, `ListaSimple` y `ListaDoble` con inserción,
  búsqueda, eliminación, actualización y recorridos.

- [x] **3. `feat(estructuras): cola fifo y pila lifo`**  
  `Cola` con `encolar`/`desencolar` y `Pila` con `apilar`/`desapilar`,
  implementadas con nodos propios.

- [x] **4. `feat(modelo): residentes, vehiculos y validaciones`**  
  `Residente`, `Vehiculo`, enums de tipo/estado, límite de 3 vehículos por
  residente y validación de placas únicas.

- [x] **5. `feat(modelo): parqueo 150 espacios y reglas de socios`**  
  `Parqueo` como lista doble de listas simples: A–C con 25 espacios y E–I con
  15. Asignación, liberación y control del área de socios.

- [x] **6. `feat(hilos): garitas concurrentes y sincronizacion`**  
  `SistemaParqueo`, `GaritaEntrada` y `GaritaSalida`. 3 garitas de entrada y 2
  de salida, `synchronized`, `wait()` en ciclo `while` y `notifyAll()`.

- [x] **7. `feat(persistencia): archivos txt y reconstruccion`**  
  `GestorArchivos` para guardar y cargar residentes y vehículos. Los visitantes
  no se persisten.

- [x] **8. `feat(vista): ventana principal y panel de residentes`**  
  Ventana con pestañas, tema visual y CRUD de residentes con recorrido de la
  lista en ambos sentidos.

- [x] **9. `feat(vista): panel de vehiculos`**  
  Selección de residente, alta y baja de vehículos, validación de 0 a 3 y
  recorrido de la lista simple.

- [x] **10. `feat(vista): panel de parqueo en tiempo real`**  
  Los 150 espacios con colores, contadores, leyenda y detalle del vehículo
  estacionado.

- [x] **11. `feat(vista): entrada, salida, garitas y visitantes`**  
  Colas visibles, estado de las 5 garitas, vehículo atendido y registro
  temporal de visitantes.

- [x] **12. `feat(vista): historial lifo y actualizacion invokeLater`**  
  Panel de eventos con los más recientes primero y actualización de la interfaz
  desde los hilos con `SwingUtilities.invokeLater`.

- [x] **13. `feat(integracion): simulacion completa y flujo general`**  
  Integración llegada → cola → garita → espacio → salida → pila, botones de
  simulación y mensajes de validación.

- [x] **14. `chore: jar ejecutable y datos de ejemplo`**  
  `dist/Proyecto2_Parqueo.jar`, archivos `datos/residentes.txt` y
  `datos/vehiculos.txt`, verificación de compilación desde el repositorio.

- [x] **15. `docs: manual tecnico y diagrama de clases`**
  Arquitectura, diagrama de clases, estructuras, hilos, formato de archivos y
  decisiones de diseño.

- [x] **16. `docs: manual de usuario, informe y README final`**
  Capturas reales de cada panel, guía de uso, plan por etapas, problemas y
  soluciones, pruebas reproducibles y README final. El reporte externo de
  AI Code Detector aún está pendiente; la guía no sustituye el resultado real.

## Pendientes de entrega externos a los 16 commits

- [ ] Obtener y adjuntar el reporte real de AI Code Detector.
- [ ] Confirmar que `EduardoAjsivinac` esté agregado como colaborador.
- [ ] Revisar con el docente la asistencia de IA y preparar la defensa del código.

## Regla de trabajo

Cada commit debe:

1. Compilar con `build.bat` o `build.sh`.
2. Tener un mensaje semántico (`feat:`, `fix:`, `docs:`, `chore:`).
3. Incluir solo los archivos de la etapa correspondiente.
4. Subirse al repositorio antes de pasar a la siguiente tanda.
