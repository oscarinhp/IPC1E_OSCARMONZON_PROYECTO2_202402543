#!/usr/bin/env bash
# Compilación para Git Bash, Linux o macOS.
set -euo pipefail
cd "$(dirname "$0")"

echo "============================================"
echo " Proyecto 2 - Parque Residencial"
echo " Compilando..."
echo "============================================"

rm -rf out dist
mkdir -p out/classes dist

find src -name "*.java" > out/sources.txt
javac -encoding UTF-8 -d out/classes @out/sources.txt
jar --create --file dist/Proyecto2_Parqueo.jar --main-class Main -C out/classes .

if [ -d datos ]; then
  cp -R datos dist/datos
fi

echo ""
echo "Compilación exitosa."
echo "JAR: dist/Proyecto2_Parqueo.jar"
echo "Ejecutar con: java -jar dist/Proyecto2_Parqueo.jar"
