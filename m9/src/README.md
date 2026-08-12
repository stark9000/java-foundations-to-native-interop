# Module 9 — Native Math & a Real Native Signal (JNI)

## Build & Run
```
./build.sh
```
This compiles the Java sources, generates JNI headers (`javac -h`),
compiles the native library with gcc, and runs NativeDemo, all in one
step. Requires gcc and a JDK (not JRE-only) with JAVA_HOME set.

Manual equivalent:
```
javac -h native src/*.java -d out
gcc -shared -fPIC -I"$JAVA_HOME/include" -I"$JAVA_HOME/include/linux" \
    -o native/libcoursenative.so native/nativebridge.c -lm
java -cp out -Djava.library.path=native NativeDemo
```

On Windows: use MinGW-w64 or MSVC instead of gcc, and
`$JAVA_HOME/include/win32` instead of `.../linux`. On macOS: use
`.../darwin`. See the chapter document's build toolchain section
for what does and doesn't change per platform - note also that
`NativeSignalGenerator`'s `getloadavg()` call is POSIX-only (Linux/macOS);
a Windows native implementation of that one method would need a
different OS API, as explained in the doc.

## Files
- ProgressCallback.java     – interface implemented in Java, called from native code
- NativeMath.java           – 5 native methods: primitives, arrays, Strings, exceptions, callbacks
- NativeSignalGenerator.java – real drop-in replacement for Module 6's SignalGenerator
- NativeDemo.java           – exercises every native method
- native/nativebridge.c     – the C implementation
- build.sh                  – full build + run pipeline

See the accompanying Word document (Module9-JNI.docx) for the full
theory writeup and design rationale.
