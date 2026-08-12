#!/bin/bash
# Builds and runs the Module 9 JNI project end to end.
# Assumes gcc and a JDK (with JAVA_HOME set) are available.
# Adjust the include paths / library extension for Windows (MinGW) or
# macOS - see the chapter document's build toolchain section.
set -e

cd "$(dirname "$0")"

echo "== 1. Compiling Java sources and generating JNI headers =="
mkdir -p native
javac -h native src/*.java -d out

echo "== 2. Compiling the native library =="
gcc -shared -fPIC \
    -I"$JAVA_HOME/include" -I"$JAVA_HOME/include/linux" \
    -o native/libcoursenative.so native/nativebridge.c -lm

echo "== 3. Running NativeDemo =="
java -cp out -Djava.library.path=native NativeDemo
