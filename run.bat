
@echo off
setlocal

:: Ruta al JDK (opcional si ya está en PATH)
:: set JAVA_HOME=C:\Program Files\Java\jdk-17
:: set PATH=%JAVA_HOME%\bin;%PATH%

:: Carpeta de JavaFX
set JAVAFX_LIB=lib

:: Compilar
javac --module-path %JAVAFX_LIB% --add-modules javafx.controls -d out src\Main.java

:: Ejecutar
java --module-path %JAVAFX_LIB% --add-modules javafx.controls -cp out Main

pause
