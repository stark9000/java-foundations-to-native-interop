@echo off
REM ============================================================
REM Module 10 build script for Windows.
REM
REM Prerequisites:
REM   - Full JDK 17+ installed, JAVA_HOME set to its install folder
REM     (jlink / jpackage / jdeps must be present - a JRE-only
REM     install won't have them)
REM   - MinGW-w64 (via MSYS2) or another gcc on PATH
REM   - WiX Toolset installed ONLY if you change --type below to
REM     exe or msi to build a real installer instead of an app-image
REM
REM Place this file in the Module 10 project root (next to src\ and
REM native\) and run it from an MSYS2/MinGW shell, or a regular
REM Command Prompt as long as gcc.exe is on PATH.
REM ============================================================

setlocal enabledelayedexpansion
cd /d "%~dp0"

if "%JAVA_HOME%"=="" (
    echo ERROR: JAVA_HOME is not set. Point it at your JDK 17+ install folder.
    exit /b 1
)

echo == 1. Compiling Java sources and generating JNI headers ==
if not exist native mkdir native
if not exist build\classes mkdir build\classes

set SOURCES=
for %%f in (src\*.java) do set SOURCES=!SOURCES! %%f

javac -h native !SOURCES! -d build\classes
if errorlevel 1 goto :error

echo == 2. Compiling the native library ==
REM No -fPIC (meaningless on Windows); output is coursenative.dll,
REM with no "lib" prefix - System.loadLibrary("coursenative") on
REM Windows looks for exactly that file name.
gcc -shared -o native\coursenative.dll native\nativebridge.c ^
    -I"%JAVA_HOME%\include" -I"%JAVA_HOME%\include\win32"
if errorlevel 1 goto :error

echo == 3. Building the JAR ==
cd build
jar --create --file inventory-app.jar --main-class Main -C classes .
if errorlevel 1 goto :error

echo == 4. Determining real module dependencies with jdeps ==
set MODULES=
for /f "delims=" %%m in ('jdeps --print-module-deps --ignore-missing-deps -R classes') do set MODULES=%%m
echo Modules needed: !MODULES!

echo == 5. Building a custom runtime image with jlink ==
if exist custom-runtime rmdir /s /q custom-runtime
jlink --module-path "%JAVA_HOME%\jmods" --add-modules !MODULES! ^
    --strip-debug --no-header-files --no-man-pages ^
    --output custom-runtime
if errorlevel 1 goto :error

echo == 6. Packaging with jpackage ==
if not exist input mkdir input
copy /y inventory-app.jar input\ >nul
if exist dist rmdir /s /q dist

REM $APPDIR is jpackage's OWN token (not a Windows env var) - written
REM literally, with no escaping needed in a batch file. It resolves to
REM the folder holding the jar; --app-content places bundled files one
REM level up, which is why the path below is $APPDIR\.. - see the
REM chapter document for how this was actually discovered.
jpackage --type app-image ^
    --input input ^
    --main-jar inventory-app.jar ^
    --main-class Main ^
    --runtime-image custom-runtime ^
    --name InventoryApp ^
    --app-content ..\native\coursenative.dll ^
    --java-options "-Djava.library.path=$APPDIR\.." ^
    --dest dist
if errorlevel 1 goto :error

cd ..
echo.
echo == Done ==
echo Run with: build\dist\InventoryApp\InventoryApp.exe
goto :eof

:error
cd /d "%~dp0"
echo.
echo Build failed - see the error above.
exit /b 1
