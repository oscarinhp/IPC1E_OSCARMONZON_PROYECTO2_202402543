@echo off
cd /d "%~dp0"
if not exist dist\Proyecto2_Parqueo.jar (
  echo No existe el JAR. Ejecute primero build.bat
  pause
  exit /b 1
)
java -jar dist\Proyecto2_Parqueo.jar
