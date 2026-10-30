# Manual técnico

## Parque Residencial: Sistema de Gestión de Parqueo

**Estudiante responsable:** Oscar Monzón · **Carné:** 202402543

**Curso:** IPC1, sección E · USAC · Segundo semestre de 2026

**Versión documentada:** etapa 14, commit `12d01ba`; documentación en etapas 15–16.

**Corrección posterior:** el alta y la edición de residentes ahora llaman a
`notificarCambio()`. El observador agenda el refresco de tabla y selectores en
el EDT sin reiniciar. Prueba: `docs/herramientas/VerificarRefrescoResidentes.java`.

## 1. Alcance y entorno

Aplicación local en Java y Swing para administrar residentes, sus vehículos y
150 espacios de parqueo. Tres garitas de entrada y dos de salida trabajan en
hilos independientes. No utiliza bases de datos, JavaFX ni dependencias externas.

Se necesita JDK 17 o superior para compilar y un entorno gráfico para ejecutar.
Desde la raíz del repositorio en Windows:

```powershell
.\build.bat
.\run.bat
```

En Linux/macOS: `bash build.sh` y `java -jar dist/Proyecto2_Parqueo.jar`.
`build.bat` regenera `out/` y `dist/`, compila `src/`, crea el JAR con `Main`
como punto de entrada y copia `datos/` a `dist/datos/`. Hacer una copia de
archivos personales antes de compilar si se guardaron dentro de esas carpetas.
El programa busca `datos/` relativo al directorio de ejecución, no dentro del JAR.

## 2. Arquitectura y paquetes

Hay **cinco paquetes** y un punto de entrada en el paquete predeterminado:

| Ubicación | Responsabilidad |
|---|---|
| `src/Main.java` | Carga inicial, creación de la ventana, observador y arranque de garitas |
| `estructuras` | Nodos, lista simple, lista doble, cola FIFO y pila LIFO propias |
| `modelo` | Entidades, reglas de negocio, parqueo y estado compartido |
| `hilos` | `GaritaEntrada`, `GaritaSalida` y `AdministradorGaritas` |
| `persistencia` | `GestorArchivos`: reconstrucción y guardado de registros |
| `vista` | Seis paneles Swing, modelos de tabla, tema visual y simulador |

Las acciones de los paneles llaman a `SistemaParqueo`. Este coordina las
estructuras y el parqueo; las garitas usan el mismo sistema. La persistencia
recorre residentes y vehículos mediante sus métodos públicos.

`Main.main()` programa la inicialización mediante `SwingUtilities.invokeLater`:

1. Crea el sistema y carga los archivos.
2. Construye la ventana y muestra los mensajes de carga.
3. Registra `() -> SwingUtilities.invokeLater(ventana::actualizarTodo)` como observador.
4. Asigna el administrador a la ventana, la muestra e inicia las cinco garitas.
5. Ejecuta un primer refresco. Los paneles toleran garitas aún no creadas.

## 3. Estructuras y relación con el dominio

| Estructura propia | Referencias principales | Uso |
|---|---|---|
| `ListaDoble` / `NodoDoble` | inicio, fin; anterior y siguiente | Residentes y filas del parqueo |
| `ListaSimple` / `NodoSimple` | inicio, fin; siguiente | Vehículos por residente, espacios por fila y visitantes |
| Dos instancias de `Cola` | frente, fin; nodos simples | Entrada y salida compartidas |
| `Pila` | cima; nodos simples | Historial de eventos |

Los nodos guardan `Object`; la capa consumidora convierte el dato a su tipo.
Los arreglos que retornan `aArreglo()` y los métodos de consulta son copias
de referencias para visualizar/recorrer información; no sustituyen el
almacenamiento enlazado. Los objetos que contienen siguen siendo mutables.

### Residentes y vehículos

`SistemaParqueo` conserva los residentes en una lista doble. Cada `Residente`
tiene su propia lista simple de entre cero y tres vehículos. El sistema busca
placas recorriendo residentes, sus vehículos y los visitantes temporales.
`listarResidentesInverso()` recorre la lista desde el final para demostrar los
enlaces en ambos sentidos.

### Distribución física del parqueo

`Parqueo` tiene una lista doble de objetos `FilaParqueo`; cada fila contiene
una lista simple de objetos `EspacioParqueo`:

| Zona | Filas | Espacios por fila | Total |
|---|---|---:|---:|
| Socios | A, B, C | 25 | 75 |
| General | E, F, G, H, I | 15 | 75 |
| **Total** | **8 filas; no existe D** | | **150** |

Cada espacio guarda ID (`A12`, `F3`), fila, número, tipo y referencia al
vehículo ocupante, o `null` si está libre. Los enlaces horizontales son los
`siguiente` de los nodos simples. Además, cada espacio guarda referencias
directas `arriba` y `abajo` al espacio de igual columna en filas contiguas.
Se enlazan únicamente columnas comunes: `C15` tiene debajo a `E15`, pero
`C16` no tiene espacio debajo porque E termina en 15.

### Costos orientativos

Encolar, desencolar, apilar y desapilar cuestan O(1), sin contar la espera
bloqueante. Buscar residentes cuesta O(r); buscar una placa cuesta O(r + v).
Buscar el primer espacio libre recorre hasta los 150 espacios: O(e).
Las copias para tablas e historial requieren tiempo y memoria lineales.

## 4. Reglas y estados

- IDs automáticos de residente con formato `R###`; también se permite indicar un ID.
- Placas almacenadas en mayúsculas y únicas entre residentes y visitantes activos.
- Máximo tres vehículos por residente; eliminar un registro activo se rechaza.
- **Regla elegida de zonas:** socios únicamente en A–C; no socios y visitantes
  únicamente en E–I. Un socio **no usa la zona general** si su zona está llena.
- Se asigna el primer espacio libre de la zona permitida, recorriendo las listas.
- Si la zona está llena, se registra rechazo y el vehículo vuelve a `FUERA`;
  si era visitante, se retira del registro temporal.

Transición normal de `EstadoVehiculo`:

```text
FUERA -> EN_COLA_ENTRADA -> EN_PARQUEO -> EN_COLA_SALIDA -> FUERA
                            |
                   conserva el espacio hasta
                   procesar la salida
```

Al solicitar entrada se verifica `FUERA`; al solicitar salida se exige
`EN_PARQUEO`. Un vehículo que ya está en cola no debe volver a solicitarla.
La consulta `vehiculosEstacionados()` incluye vehículos con espacio asignado,
incluso si ya esperan salida; una segunda solicitud se rechaza por estado.

## 5. Flujo, hilos y sincronización

### Entrada

1. Registrar un visitante y luego llamar a `solicitarEntrada(placa)`, o elegir
   un vehículo existente de residente.
2. `solicitarEntrada` cambia el estado y encola en la única cola de entrada.
3. Cualquiera de las tres garitas toma el frente mediante `desencolar()`.
4. Tras una revisión de 700 ms, `procesarEntrada` busca/asigna el espacio.
5. Apila eventos de ingreso y asignación, o rechazo. Completa 900 ms de trámite.
6. En `finally`, la garita se marca libre y notifica el cambio a la vista.

### Salida

1. `solicitarSalida` cambia el estado y encola en la única cola de salida.
2. Una de las dos garitas retira el frente y espera 600 ms de revisión.
3. `procesarSalida` libera el espacio, limpia la referencia del vehículo,
   establece `FUERA` y elimina al visitante temporal si corresponde.
4. Apila liberación y salida. Tras 800 ms de trámite notifica el cambio.

### Protección de recursos

- `Cola` sincroniza sus operaciones. `desencolar()` ejecuta
  `while (frente == null) { wait(); }`; `encolar()` hace `notifyAll()`.
  La espera libera el monitor de **esa cola**, no mantiene bloqueado el sistema.
- El sistema usa bloques `synchronized (this)` para operaciones de negocio;
  las asignaciones/liberaciones de `Parqueo` también están sincronizadas.
- La pila sincroniza sus operaciones porque recibe eventos de distintas garitas.
- Las listas por sí solas no son colecciones concurrentes: el sistema protege
  su uso compartido. No todos los accesores o métodos auxiliares están sincronizados.
- `activo`, la placa atendida y el indicador de atención usan `volatile`.
- FIFO describe el **orden de retiro de la cola**. Con varias garitas, no se
  garantiza el mismo orden de finalización de los trámites.
- Los cinco hilos son daemon; `detener()` baja `activo` e interrumpe esperas.

El observador no cambia Swing desde una garita: agenda `actualizarTodo()` en
el EDT con `invokeLater`. Los avisos actuales se emiten al encolar y al finalizar
la atención; no hay temporizador de refresco continuo ni aviso en cada fase.
Por eso algunos estados intermedios pueden no verse inmediatamente.

## 6. Persistencia

Archivos UTF-8, sin encabezado real; los nombres de campos aquí son explicativos:

```text
residentes.txt: ID|NOMBRE|CASA|SOCIO
R001|Ana Lopez|Casa 12|S

vehiculos.txt: PLACA|MARCA|MODELO|COLOR|ID_RESIDENTE
P123ABC|Toyota|Hilux 2020|Rojo|R001
```

`SOCIO` se escribe como `S` o `N`. Al leer se interpreta `S` como verdadero.
Se cargan residentes primero, luego vehículos asociados por ID. Las líneas
vacías se ignoran y los errores detectados de formato, duplicados o asociación
se reportan mientras la carga continúa. No es un validador exhaustivo de datos.

El cierre confirmado llama a `GestorArchivos.guardar()`, que sobrescribe ambos
archivos mediante `BufferedWriter`, y luego detiene garitas y termina el proceso.
Solo persisten residentes y vehículos registrados: **no** visitantes, colas,
ocupación, estados de circulación ni historial. Al reiniciar, el parqueo está libre.

## 7. Interfaz y simulación

| Panel | Funciones |
|---|---|
| Residentes | Formulario, búsqueda, edición, eliminación y recorridos doblemente enlazados |
| Vehículos | Elegir dueño, registrar/eliminar, tabla y contador de tres vehículos |
| Parqueo | 150 botones por fila, colores, búsqueda de ID y detalles de enlaces |
| Entrada | Visitantes, vehículos listos, cola y estado de tres garitas |
| Salida | Solicitud, cola, dos garitas y listado de visitantes activos |
| Eventos | Pila en texto, filtro y límites 10/25/50 o todos |

`ResidentesTableModel` y `VehiculosTableModel` extienden `AbstractTableModel`.
`Tema` comparte fuentes y colores. `Simulador` usa arreglos de datos y un
generador congruencial propio; genera visitantes/residentes y solicitudes.
La ráfaga de diez pasos encola rápidamente: si todavía no hay estacionados,
las salidas se sustituyen por llegadas. No garantiza diez ciclos completos.

## 8. Diagramas

Ver [diagrama de clases y flujo](diagramas/Clases_y_Flujo.md). Incluye las
relaciones entre nodos, estructuras, entidades, garitas y paneles. GitHub y
la vista previa de VS Code con soporte Mermaid pueden renderizarlo.

## 9. Limitaciones y precauciones de esta versión

- El formato elegido no guarda el tipo de vehículo; al cargar todos se crean
  como `AUTOMOVIL`, aunque su placa comience con M. La placa no define el tipo.
- No hay guardado automático, transacción entre los dos archivos ni copia de
  seguridad. Si falla el guardado se muestra el error, pero el cierre continúa.
- El alta/edición de residentes refresca la interfaz mediante el observador.
  El refresco se agenda con `invokeLater`; si hay una búsqueda activa en la
  tabla, el filtro sigue aplicándose hasta pulsar **Ver todos**.
- El registro de visitantes valida nombre y duplicados, pero no todos sus campos
  obligatorios. Completar el formulario y no usar separadores `|` ni saltos de línea.
- El filtro de eventos se aplica **después** del límite: «Últimos 10» filtra los
  diez eventos más recientes, no busca diez coincidencias en toda la pila.
- El filtro sin tipo aparece como una opción en blanco: significa todos los tipos.
- No se implementan deshacer, pausa/reanudación ni ajuste de velocidad (opcionales).
- Las clases Swing pueden usar colecciones internamente. Las estructuras de negocio
  son propias y el código fuente del proyecto no importa `java.util`.

## 10. Referencias y transparencia

- Enunciado local: `770_Proyecto_2_2S2026_Parqueo_Residencial.pdf`, secciones 4.1–4.4.
- Rúbrica: `IPC1E-HC-Proyecto2-2S-2026.xlsx.pdf`.
- [Swing y EDT](https://docs.oracle.com/javase/tutorial/uiswing/concurrency/).
- [Concurrencia Java](https://docs.oracle.com/javase/tutorial/essential/concurrency/).
- [Entrada/salida Java](https://docs.oracle.com/javase/tutorial/essential/io/).

El código y esta documentación se elaboraron con asistencia de IA durante la
sesión de desarrollo. El estudiante debe revisar, comprender y declarar esa
asistencia conforme a las reglas del curso; este manual no certifica autoría
exclusivamente humana ni sustituye la evaluación académica.
