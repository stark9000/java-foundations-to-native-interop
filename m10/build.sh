#!/bin/bash
# Full pipeline: compile -> native library -> jdeps -> jlink -> jpackage.
# Adjust compiler/include paths for Windows (MinGW) or macOS - see
# Module 9's build toolchain section for what changes per platform.
set -e
cd "$(dirname "$0")"

echo "== 1. Compiling Java sources and generating JNI headers =="
mkdir -p build/classes native
javac -h native src/*.java -d build/classes

echo "== 2. Compiling the native library =="
gcc -shared -fPIC \
    -I"$JAVA_HOME/include" -I"$JAVA_HOME/include/linux" \
    -o native/libcoursenative.so native/nativebridge.c

echo "== 3. Building the JAR =="
cd build
jar --create --file inventory-app.jar --main-class Main -C classes .

echo "== 4. Determining real module dependencies with jdeps =="
MODULES=$(jdeps --print-module-deps --ignore-missing-deps -R classes)
echo "Modules needed: $MODULES"

echo "== 5. Building a custom runtime image with jlink =="
rm -rf custom-runtime
jlink --module-path "$JAVA_HOME/jmods" --add-modules "$MODULES" \
    --strip-debug --no-header-files --no-man-pages \
    --output custom-runtime

echo "== 6. Packaging with jpackage =="
mkdir -p input
cp inventory-app.jar input/
rm -rf dist
jpackage --type app-image \
    --input input \
    --main-jar inventory-app.jar \
    --main-class Main \
    --runtime-image custom-runtime \
    --name InventoryApp \
    --app-content ../native/libcoursenative.so \
    --java-options "-Djava.library.path=\$APPDIR/.." \
    --dest dist

echo "== Done =="
echo "Run with: build/dist/InventoryApp/bin/InventoryApp"
du -sh dist/InventoryApp
