# Module 7 — Inventory Desktop App (Swing)

The two project threads merge here: the console Inventory app (Modules
1-5) gets a full Swing GUI, and Module 6's VU meter is embedded as a
live "Activity" widget. Module 5's background scan is rebuilt on
SwingWorker for proper EDT-safe progress reporting.

## Build & Run
```
javac *.java
java Main
```
Requires JDK 17+ and a display (or a virtual one, e.g. Xvfb, for
headless environments). Creates/appends to `inventory-audit.log`.

## Files
- Category.java, Identifiable.java, Resistor.java, IntegratedCircuit.java, Relay.java,
  Component.java, DuplicateIdException.java, EntryNotFoundException.java, AuditLog.java,
  Repository.java, Inventory.java, InventoryReport.java, InventoryFileStore.java,
  InventoryScanner.java, ScanProgressListener.java
    – unchanged from Module 5
- SignalGenerator.java, GaugeRenderer.java, VuMeterPanel.java
    – unchanged from Module 6
- DarkTheme.java          – NEW: lightweight dark palette via UIManager
- InventoryTableModel.java – NEW: adapts Inventory data for JTable
- ComponentFormDialog.java – NEW: modal add-component dialog (CardLayout + GridBagLayout)
- ScanWorker.java          – NEW: SwingWorker wrapping InventoryScanner for EDT-safe progress
- InventoryPanel.java      – NEW: the application's real content
- MainFrame.java           – NEW: JFrame chrome + menu bar
- Main.java                – NEW: theme setup, audit log lifecycle, EDT launch

See the accompanying Word document (Module7-Swing-Desktop-UI.docx)
for the full theory writeup and design rationale, including how this
GUI chapter was actually tested (via a virtual display and pixel
sampling of a real screenshot).
