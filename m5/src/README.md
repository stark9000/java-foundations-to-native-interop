# Module 5 — Persistent Inventory + Background Scan

## Build & Run
```
javac *.java
java Main
```
Requires JDK 17+. Creates/appends to `inventory-audit.log` in the working directory.

Try option 12 (start scan) then immediately option 1 or 13 - the menu
stays responsive while the scan runs in the background. Try option 14
partway through to cancel it.

## Files
- Category.java, Identifiable.java, Resistor.java, IntegratedCircuit.java, Relay.java,
  Component.java, DuplicateIdException.java, EntryNotFoundException.java, AuditLog.java,
  Inventory.java, InventoryReport.java
    – unchanged from Module 4
- Repository.java          – one addition: clear(), used by load()
- InventoryFileStore.java  – NEW: plain-text save/load (deliberately not Serializable)
- ScanProgressListener.java – NEW: functional interface for scan progress callbacks
- InventoryScanner.java    – NEW: background scan using volatile + AtomicInteger
- Main.java                – NEW: save/load menu, ExecutorService-backed scan controls

See the accompanying Word document (Module5-IO-Concurrency-Serialization.docx)
for the full theory writeup and design rationale.
