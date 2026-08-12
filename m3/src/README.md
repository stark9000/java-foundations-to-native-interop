# Module 3 — Inventory System v2 (Collections, Generics, Exceptions, I/O)

## Build & Run
```
javac *.java
java Main
```
Requires JDK 17+. Creates/appends to `inventory-audit.log` in the working directory.

## Files
- Category.java, Identifiable.java, Resistor.java, IntegratedCircuit.java, Relay.java
    – unchanged from Module 2
- Component.java        – unchanged except now implements Comparable<Component>
- Repository.java       – NEW: generic, bounded base class (T extends Identifiable)
- DuplicateIdException.java – NEW: checked exception
- EntryNotFoundException.java – NEW: unchecked exception
- AuditLog.java          – NEW: Closeable, java.time-stamped file logger
- Inventory.java         – rewritten: extends Repository<Component>, Comparators, category ops
- Main.java              – rewritten: try-with-resources, sorting menu, exception handling

See the accompanying Word document (Module3-Core-Libraries-Collections.docx)
for the full theory writeup and design rationale.
