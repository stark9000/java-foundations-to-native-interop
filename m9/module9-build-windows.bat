@echo off
REM ============================================================
REM Module 9 build script for Windows.
REM
REM Prerequisites:
REM   - JDK 17+ installed, JAVA_HOME set to its install folder
REM   - MinGW-w64 (via MSYS2) or another gcc on PATH
REM
REM Place this file in the Module 9 project root (next to src\ and
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
if not exist out mkdir out

set SOURCES=
for %%f in (src\*.java) do set SOURCES=!SOURCES! %%f

javac -h native !SOURCES! -d out
if errorlevel 1 goto :error

echo == 2. Compiling the native library ==
REM No -fPIC (meaningless on Windows). Output has NO "lib" prefix and
REM a .dll extension - System.loadLibrary("coursenative") on Windows
REM looks for exactly coursenative.dll, not libcoursenative.dll.
gcc -shared -o native\coursenative.dll native\nativebridge.c ^
    -I"%JAVA_HOME%\include" -I"%JAVA_HOME%\include\win32" -lm
if errorlevel 1 goto :error

echo == 3. Running NativeDemo ==
java -cp out -Djava.library.path=native NativeDemo
if errorlevel 1 goto :error

echo.
echo Done.
goto :eof

:error
echo.
echo Build failed - see the error above.
exit /b 1
