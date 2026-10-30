# Herramientas de documentación y verificación

Estas clases están **fuera de `src/`**: no se empaquetan en el JAR de la aplicación.
No requieren bibliotecas externas ni importan colecciones de `java.util`.

## Windows / PowerShell

Desde la raíz del repositorio, con JDK 17 o superior y el JAR disponible:

```powershell
$temporal = Join-Path $env:TEMP 'proyecto2-documentacion'
New-Item -ItemType Directory -Force $temporal | Out-Null
javac -encoding UTF-8 -cp dist/Proyecto2_Parqueo.jar -d $temporal docs/herramientas/VerificarEntrega.java docs/herramientas/CapturarPaneles.java
java -cp "dist/Proyecto2_Parqueo.jar;$temporal" VerificarEntrega datos $temporal
```

`VerificarEntrega` realiza comprobaciones de estructuras, reglas, parqueo,
persistencia y concurrencia. Sus copias de persistencia quedan en una subcarpeta
nueva `persistencia-*` dentro de `$temporal`. Nunca escribe en `datos/`.
Un fallo lanza `AssertionError` y termina con código distinto de cero.
Ver el [resultado registrado](../pruebas/Resultado_Verificacion.md).

## Capturar la interfaz

Se necesita un escritorio visible y desbloqueado, sin mover el ratón ni poner
otras ventanas encima durante la ejecución. Para no reemplazar las imágenes
entregadas, usar una carpeta temporal:

```powershell
java -cp "dist/Proyecto2_Parqueo.jar;$temporal" CapturarPaneles datos "$temporal/capturas"
```

Abre la ventana real de la aplicación, prepara datos temporales, pone solicitudes
en las colas, espera a las garitas y toma siete PNG con `Robot`. Invoca también
el botón real de ráfaga. No guarda los cambios ni altera los fuentes del programa.
Su duración aproximada es de 15–25 segundos, según el equipo.

Estas capturas utilizan `VentanaPrincipal` desde el JAR, con el mismo aspecto
nativo que `Main`. Se omite el aviso modal de carga para automatizar la sesión.
El contenido de la simulación y el orden de las garitas no son deterministas.
No ejecutar en un equipo sin entorno gráfico ni usarlo como prueba exhaustiva
de todos los botones de la aplicación.
