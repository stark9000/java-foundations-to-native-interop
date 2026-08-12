# Module 8 — Bytecode Detective

A short, conceptual bridge chapter - no Inventory or Swing work here.
Runs a handful of small methods for real, then shells out to javap to
disassemble each one, so you can see exactly what the compiler
generates behind ordinary-looking source code.

## Build & Run
```
javac *.java
java BytecodeDetective
```
Requires JDK 17+ with `javap` on PATH (ships with any standard JDK,
not a JRE-only install).

## Files
- ClassLoaderExplorer.java  – prints the class loader chain for a JDK class and a project class
- AutoboxingDemo.java       – reveals Integer.valueOf/intValue calls behind autoboxing
- StringSwitchDemo.java     – reveals hashCode()+equals()-based dispatch behind a String switch
- StringConcatDemo.java     – reveals invokedynamic/StringBuilder behind "+"
- NativeBridgePreview.java  – a native method declaration with no implementation (Module 9 preview)
- BytecodeDetective.java    – runs everything above and disassembles each class via javap

See the accompanying Word document (Module8-JVM-Internals.docx)
for the full theory writeup and design rationale.
