# Parque Residencial — Sistema de Gestión de Parqueo

Proyecto 2 del curso **Introducción a la Programación y Computación 1, Sección E**  
Facultad de Ingeniería — Universidad de San Carlos de Guatemala

**Estudiante:** Oscar Monzón  
**Carné:** 202402543  
**Repositorio:** <https://github.com/oscarinhp/IPC1E_OSCARMONZON_PROYECTO2_202402543>

---

## Descripción

Aplicación de escritorio en **Java 17 + Swing** para administrar el parqueo de un
parque residencial. El sistema permite registrar residentes y sus vehículos,
controlar 150 espacios de parqueo, atender la entrada y salida de vehículos con
garitas concurrentes, registrar visitantes de forma temporal y consultar el
historial de eventos.

El proyecto se construye por etapas. Cada etapa se registra en un commit
semántico; el plan completo está en [`PLAN_COMMITS.md`](PLAN_COMMITS.md).

## Estado actual

- [x] Etapa 1: estructura inicial, compilación y ventana base.
- [x] Etapa 2: nodos propios, lista doble de residentes y lista simple de vehículos.
- [x] Etapa 3: cola FIFO y pila LIFO propias (sincronizadas para hilos).
- [x] Etapa 4: modelo de residentes y vehículos con reglas de negocio.
- [x] Etapa 5: parqueo con lista doble de listas simples y reglas de zonas.
- [x] Etapa 6: 5 garitas como hilos concurrentes con sincronización.
- [x] Etapa 7: persistencia en `.txt` con el formato del enunciado.
- [x] Etapa 8: ventana principal y panel de residentes.
- [x] Etapa 9: panel de vehículos por residente (máximo 3).
- [x] Etapa 10: mapa visual del parqueo con búsqueda por espacio.
- [x] Etapa 11: paneles de entrada y salida con visitantes y estado de garitas.
- [x] Etapa 12: historial de eventos (pila LIFO) con filtros.
- [x] Etapa 13: simulación completa (llegada → cola → garita → espacio → salida → eventos).
- [x] Etapa 14: JAR ejecutable versionado y datos de ejemplo en `datos/`.
- [x] Etapa 15: [manual técnico](docs/Manual_Tecnico.md) y [diagramas](docs/diagramas/Clases_y_Flujo.md).
- [x] Etapa 16: [manual de usuario](docs/Manual_Usuario.md), siete capturas reales e [informe de desarrollo](docs/Informe_Desarrollo.md).

Las 16 etapas están registradas; esto no significa que todos los requisitos
externos de entrega estén cerrados. Falta adjuntar el reporte real de AI Code
Detector y confirmar al auxiliar como colaborador. Las limitaciones funcionales
conocidas se detallan en los manuales.

## Formato de los archivos de datos

El separador es la barra vertical `|`.

**`datos/residentes.txt`**

```text
ID|NOMBRE|CASA|SOCIO
R001|Ana López|Casa 12|S
R002|Carlos Méndez|Casa 5|N
```

**`datos/vehiculos.txt`**

```text
PLACA|MARCA|MODELO|COLOR|ID_RESIDENTE
P123ABC|Toyota|Corolla|Blanco|R001
P456DEF|Honda|Civic|Rojo|R001
```

Los nombres de campos de los ejemplos son explicativos: los archivos reales
**no llevan encabezado**. Las líneas vacías se ignoran; los errores detectados
de formato o asociación se omiten con un aviso y la carga continúa.
Los visitantes nunca se guardan en los archivos porque son información
temporal. Tampoco se guardan ocupación, colas ni historial. El tipo de vehículo
no figura en este formato: al reiniciar se reconstruye como automóvil.

## Tecnologías y restricciones

- Java 17 o superior.
- Swing codificado manualmente (sin diseñador drag and drop).
- Sin JavaFX, bases de datos, frameworks ni librerías externas.
- Sin `ArrayList`, `LinkedList`, `Stack`, `Queue`, `Deque` ni otras colecciones
  de `java.util` para la lógica del proyecto.
- Listas, cola y pila implementadas con nodos propios.
- Persistencia en archivos `.txt`.

## Cómo compilar y ejecutar

### Windows

```bat
build.bat
run.bat
```

### Git Bash, Linux o macOS

```bash
./build.sh
java -jar dist/Proyecto2_Parqueo.jar
```

El comando `build.bat` genera:

- `out/classes/`: clases compiladas.
- `dist/Proyecto2_Parqueo.jar`: archivo ejecutable.
- `dist/datos/`: copia de los archivos de datos de ejemplo.

### Ejecutar desde VS Code

1. Abrir la carpeta del proyecto.
2. Instalar la extensión **Extension Pack for Java**.
3. Presionar `F5` o usar la configuración **Ejecutar Parque Residencial**.

## Estructura del proyecto

```text
PROYECTO-2/
├── src/
│   ├── Main.java
│   ├── estructuras/      Nodos, listas, cola y pila propias
│   ├── modelo/           Residente, vehículo, espacio, evento y reglas
│   ├── hilos/            Garitas de entrada y salida
│   ├── persistencia/     Lectura y escritura de archivos .txt
│   └── vista/            Ventanas y paneles Swing
├── datos/                Residentes y vehículos de ejemplo
├── docs/                 Manuales, diagramas e informe
├── dist/                 JAR ejecutable (se genera al compilar)
├── build.bat / build.sh
├── run.bat
└── PLAN_COMMITS.md
```

## Reglas principales del sistema

- Un residente puede tener entre 0 y 3 vehículos.
- Las placas son únicas en todo el sistema.
- Los espacios **A–C** son exclusivos para vehículos de residentes socios.
- Los espacios **E–I** son para residentes no socios y visitantes.
- Los visitantes no se guardan en los archivos de persistencia.
- La cola de entrada es FIFO y la atienden 3 garitas.
- La cola de salida es FIFO y la atienden 2 garitas.
- El historial de eventos es una pila LIFO: el evento más reciente se muestra
  primero.

## Documentación

- [Índice y cómo ver los documentos](docs/README.md).
- [Manual técnico](docs/Manual_Tecnico.md).
- [Manual de usuario con capturas](docs/Manual_Usuario.md).
- [Diagramas de clases y flujo](docs/diagramas/Clases_y_Flujo.md).
- [Informe de desarrollo](docs/Informe_Desarrollo.md).
- [Pruebas: 31 comprobaciones correctas](docs/pruebas/Resultado_Verificacion.md)
  y [herramientas para repetirlas](docs/herramientas/README.md).
- [Estado pendiente del reporte de AI Code Detector](docs/Reporte_AI_Code_Detector.md).

Abrir los enlaces en GitHub o los archivos `.md` en VS Code con **Ctrl+Shift+V**.
Los manuales están en Markdown, no en PDF.

## Precauciones y estado de entrega

- Guardar y salir normalmente; no hay guardado automático. Conservar copias
  de `datos/` antes de probar la simulación.
- El alta/edición de residentes puede requerir **Ver todos** para releer la tabla;
  si no aparece en otros selectores, guardar/cerrar/reabrir.
- Los estados de garitas se notifican al encolar y al finalizar la atención,
  no en cada fase intermedia. Ver más limitaciones en el manual técnico.
- Confirmar en GitHub la colaboración de `EduardoAjsivinac`; no se ha verificado
  su aceptación desde esta documentación.
- El código y los documentos se elaboraron con asistencia de IA. La rúbrica
  advierte penalizaciones por uso mayoritario de IA; revisar su admisibilidad
  con el docente y declarar la asistencia real. No se certifica autoría humana
  exclusiva ni se ha obtenido aún el reporte solicitado de AI Code Detector.

---

**Nota:** este repositorio se desarrolla de forma incremental. Los commits se
suben por etapas, mínimo dos por día, para mantener un historial verificable.
