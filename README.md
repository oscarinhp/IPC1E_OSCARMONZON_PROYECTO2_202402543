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
- [ ] Etapas 2 a 16: estructuras, modelo, hilos, persistencia, interfaz y documentación.

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
├── datos/                Archivos de ejemplo (se agregan en la etapa 14)
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

La documentación se agrega en las etapas 15 y 16:

- `docs/Manual_Tecnico.md`
- `docs/Manual_Usuario.md`
- `docs/Informe_Desarrollo.md`
- `docs/diagramas/`
- `docs/capturas/`

---

**Nota:** este repositorio se desarrolla de forma incremental. Los commits se
suben por etapas, mínimo dos por día, para mantener un historial verificable.
