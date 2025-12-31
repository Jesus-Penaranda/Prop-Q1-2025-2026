#!/usr/bin/env bash
set -e

cd "$(dirname "$0")"

# Función para  navegador
abrir_navegador() 
{
    local url="$1"
    if which xdg-open > /dev/null; then
        xdg-open "$url"
    elif which open > /dev/null; then
        open "$url"
    elif which explorer.exe > /dev/null; then
        explorer.exe "$url"
    else
        echo "Abre manualmente: $url"
    fi
}

clear

echo "========================================================"
echo "   GESTOR DE EJECUCIÓN DEL PROYECTO (PROP)"
echo "========================================================"
echo "Selecciona una opción:"
echo ""
echo "  0) Ejecutar Interfaz Gráfica"
echo "  1) Ejecutar Driver AUTOMÁTICO-1"
echo "  2) Ejecutar Driver AUTOMÁTICO-2"
echo "  3) Ejecutar Driver INTERACTIVO"
echo "  4) Ejecutar TESTS (Reporte web)"
echo "  5) Salir"
echo ""
echo "========================================================"
read -p "Introduce opción: " opcion

case $opcion in
    0)
        echo ""
        ./gradlew classes --console=plain
        echo "Iniciando Interfaz Gráfica..."
        java -cp build/classes/java/main:build/resources/main Presentacion.main
        ;;

    1)
        echo ""
        ./gradlew classes --console=plain
        echo "Ejecutando DriverEjemploCompleto..."
        echo "--------------------------------------------------------"
        java -cp build/classes/java/main drivers.DriverEjemploCompleto
        ;;
    2)
        echo ""
        ./gradlew classes --console=plain
        echo ">> Ejecutando DriverComparativa..."
        echo "--------------------------------------------------------"
        java -cp build/classes/java/main drivers.DriverComparativa
        ;;

    3)
        echo ""
        ./gradlew classes --console=plain
        echo ">> Ejecutando DriverVisual..."
        echo "--------------------------------------------------------"
        java -cp build/classes/java/main drivers.DriverVisual
        ;;

    4)
        echo ""
        ./gradlew clean test
        rc=$?
        echo ""
        echo "Abriendo reporte de tests..."
        if [ -f "build/reports/tests/test/index.html" ]; then
            abrir_navegador "build/reports/tests/test/index.html"
        fi
        exit $rc
        ;;

    5)
        echo "Saliendo..."
        exit 0
        ;;

    *)
        echo "Opción no válida."
        exit 1
        ;;
esac
