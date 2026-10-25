@echo off
setlocal enabledelayedexpansion
cd /d "%~dp0"

echo ============================================
echo  Proyecto 2 - Parque Residencial
echo  Compilando...
echo ============================================

if exist out rmdir /s /q out
if exist dist rmdir /s /q dist
mkdir out\classes
mkdir dist

rem Genera la lista de archivos .java para javac.
rem Se usan barras normales porque javac interpreta "\U" como escape Unicode.
> out\sources.txt (
  for /r "src" %%f in (*.java) do (
    set "archivo=%%f"
    echo !archivo:\=/!
  )
)

javac -encoding UTF-8 -d out\classes @out\sources.txt
if errorlevel 1 (
  echo.
  echo ERROR: no se pudo compilar el proyecto.
  exit /b 1
)

jar --create --file dist\Proyecto2_Parqueo.jar --main-class Main -C out\classes .
if errorlevel 1 (
  echo.
  echo ERROR: no se pudo crear el archivo .jar.
  exit /b 1
)

if exist datos (
  xcopy /E /I /Y datos dist\datos >nul
)

echo.
echo Compilacion exitosa.
echo JAR: dist\Proyecto2_Parqueo.jar
echo Ejecutar con: run.bat
endlocal
