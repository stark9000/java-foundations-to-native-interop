# Module 2 — Electronics Inventory System

## Build & Run
```
javac *.java
java Main
```
Requires JDK 17+.

## Files
- Category.java         – enum with behavior (label field)
- Identifiable.java      – single-method interface
- Component.java         – abstract base class (encapsulation, equals/hashCode/toString)
- Resistor.java           – concrete subclass
- IntegratedCircuit.java  – concrete subclass
- Relay.java              – concrete subclass
- Inventory.java          – array-backed store + nested Inventory.Stats class
- Main.java               – console menu (entry point, I/O only)

See the accompanying Word document (Module2-Object-Oriented-Programming.docx)
for the full theory writeup and design rationale.
