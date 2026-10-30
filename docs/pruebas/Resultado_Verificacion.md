# Resultado de verificación de la entrega

**Fecha:** 29/10/2026, aproximadamente 21:11, UTC−06:00.

**Entorno:** Windows, OpenJDK y javac 17.0.20.1.

## Artefacto y procedimiento

1. `cmd /c build.bat`: compilación y empaquetado correctos de los fuentes actuales.
2. Para verificar el artefacto ya publicado, se extrajo a una carpeta temporal
   el binario de `12d01ba:dist/Proyecto2_Parqueo.jar` sin modificarlo.
3. Se compilaron `VerificarEntrega.java` y `CapturarPaneles.java` contra ese
   JAR y se ejecutaron con la carpeta `datos/` como entrada de solo lectura.
4. Las pruebas de persistencia escribieron exclusivamente en una subcarpeta
   temporal nueva. Las capturas no guardaron el estado de su sesión.

SHA-256 del **JAR publicado** utilizado:

```text
54c460c3593867597aa5769db088e9bd20cb53ed02cf72f01d2b9c9b361a3851
```

Un JAR recompilado puede tener otro hash por las fechas del contenedor ZIP.
La verificación de clases no se basó únicamente en ese hash.

## Salida de las comprobaciones

La ejecución terminó sin `AssertionError` ni excepciones:

```text
Java: 17.0.20.1
OK Lista doble: recorrido inverso
OK Lista doble: enlaces tras eliminar intermedio
OK Lista doble: vaciado
OK Lista simple: eliminar ultimo
OK Cola FIFO
OK Cola vacia: wait sin busy-wait
OK notifyAll despierta consumidor
OK Consulta LIFO no destructiva
OK Desapilar LIFO
OK Rechazo de cuarto vehiculo
OK Placa duplicada sin distinguir mayusculas
OK Rechazo de solicitud de entrada duplicada
OK No eliminar residente con vehiculo activo
OK Socio asignado a A-C
OK Visitante solo en E-I
OK Visitante eliminado al salir
OK Ultimo evento primero
OK Distribucion 150 espacios, 75 por zona
OK Enlaces verticales reciprocos
OK Zona general llena no invade socios
OK Liberacion recupera disponibilidad
OK Carga de archivos de ejemplo
OK Guardado y reconstruccion de residentes
OK Visitantes no persistidos
OK Reinicio sin ocupacion ni historial
OK Archivo vehiculos excluye visita temporal
OK Ingresos concurrentes
OK Tres garitas de entrada y dos de salida iniciadas
OK Nueve ingresos sin espacios duplicados
OK Salidas concurrentes
OK Salidas concurrentes liberan espacios y visitantes
RESULTADO: 31 comprobaciones OK.
```

Se omite de la transcripción únicamente la ruta local de las copias temporales.

## Evidencia gráfica

`CapturarPaneles` cargó ocho residentes y ocho vehículos. Generó siete archivos
PNG y terminó sin errores. Se revisaron visualmente las siete imágenes; incluyen
las colas con garitas atendiendo, los espacios asignados y eventos de salida.
Ver [capturas y procedencia](../capturas/README.md).

## Alcance y límites

- Es una ejecución controlada, no una prueba de estrés prolongada ni una prueba
  formal de ausencia de carreras en cualquier intercalado de hilos.
- Las pruebas del modelo no equivalen a probar exhaustivamente formularios y botones.
- Las capturas consultan/refrescan la vista: no garantizan que todos los estados
  intermedios sean notificados automáticamente en la operación habitual.
- El caso de zona llena prueba el rechazo de asignación de `Parqueo`; no satura
  150 vehículos mediante los cinco hilos ni comprueba todos los casos de rechazo.
- No se ejecutaron pruebas en Linux/macOS ni con todas las versiones de Java.
- No se probó recuperación ante fallos de disco ni caída durante el guardado.
- No incluye ningún análisis de AI Code Detector ni certificación académica.

Los problemas conocidos están detallados en el [informe](../Informe_Desarrollo.md).
Comandos para repetir las pruebas: [herramientas](../herramientas/README.md).

## Regresión posterior: refresco de residentes

Tras corregir las notificaciones de alta y edición, se recompilaron los fuentes
y se compararon sus clases con las del JAR actualizado: no hubo diferencias.
Se repitieron las 31 comprobaciones generales usando copias de los datos
versionados y se ejecutó `VerificarRefrescoResidentes` contra el nuevo JAR.

```text
OK Selector inicialmente vacio
OK R015 aparece en Vehiculos sin reiniciar ni refrescar manualmente
OK Tabla de Residentes actualizada tras alta
OK Alta con ID emite una notificacion
OK Alta con ID automatico tambien refresca
OK Se conserva la seleccion del residente al agregar otro
OK Edicion refresca tabla y selector
OK Vehiculo asociado a R015 y visible en su tabla
OK Vehiculo listo para solicitar entrada
OK Edicion inexistente no notifica ni altera el sistema
RESULTADO: 10 comprobaciones de refresco OK.
```

La regresión usa los paneles reales con `invokeLater`, sin ventanas de diálogo,
lectura ni escritura de datos personales. Los resultados y el hash inicial de
este documento corresponden al JAR histórico de la etapa 14; esta sección
registra la verificación de la corrección posterior, no sustituye aquel hash.
