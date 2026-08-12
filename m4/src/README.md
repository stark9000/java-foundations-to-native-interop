# Module 4 — Inventory Reports (Streams, Optional, Collectors)

## Build & Run
```
javac *.java
java Main
```
Requires JDK 17+. Creates/appends to `inventory-audit.log` in the working directory.

## Files
- Category.java, Identifiable.java, Resistor.java, IntegratedCircuit.java, Relay.java,
  Component.java, DuplicateIdException.java, EntryNotFoundException.java, AuditLog.java
    – unchanged from Module 3
- Repository.java   – findById() now returns Optional<T>; removeById() uses orElseThrow()
- Inventory.java    – every manual loop replaced with a Stream pipeline
- InventoryReport.java – NEW: groupingBy-based reports, low-stock filter, name joining
- Main.java         – NEW "view by id" (Optional-based) and "Reports" menu options

See the accompanying Word document (Module4-Functional-Stream-Style-Java.docx)
for the full theory writeup and design rationale.
