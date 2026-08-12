import javax.swing.SwingUtilities;
import java.io.IOException;
import java.nio.file.Path;

public class Main {

    public static void main(String[] args) {
        // Every Swing component must be created and touched only on the
        // Event Dispatch Thread (EDT) - invokeLater schedules this whole
        // block to run there, rather than on whatever thread ran main().
        SwingUtilities.invokeLater(Main::createAndShowUi);
    }

    private static void createAndShowUi() {
        DarkTheme.apply(); // must run before any component below is constructed

        Inventory inventory = new Inventory();
        AuditLog auditLog;
        try {
            auditLog = new AuditLog(Path.of("inventory-audit.log"));
        } catch (IOException e) {
            System.err.println("Could not open audit log: " + e.getMessage());
            return;
        }

        // AuditLog has no natural try-with-resources scope in a GUI app
        // - the "block" doesn't end until the window closes, arbitrarily
        // far in the future. A shutdown hook is the standard way to still
        // guarantee cleanup runs, however the app eventually exits.
        AuditLog finalAuditLog = auditLog;
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                finalAuditLog.close();
            } catch (IOException ignored) {
                // Best effort on the way out - nothing further to do.
            }
        }));

        seedSampleData(inventory, auditLog);

        InventoryPanel panel = new InventoryPanel(inventory, auditLog);
        MainFrame frame = new MainFrame(panel);
        frame.setVisible(true);
    }

    private static void seedSampleData(Inventory inventory, AuditLog auditLog) {
        try {
            inventory.add(new Resistor(1, "1/4W Carbon Film", 200, 220.0, 5.0));
            inventory.add(new Resistor(2, "1/4W Carbon Film", 150, 4700.0, 5.0));
            inventory.add(new Resistor(3, "1/2W Metal Film", 8, 1000.0, 1.0));
            inventory.add(new IntegratedCircuit(4, "74HC595 Shift Register", 12, "74HC595", 16));
            inventory.add(new IntegratedCircuit(5, "ATmega328P", 0, "ATMEGA328P-PU", 28));
            inventory.add(new Relay(6, "5V SPDT Relay", 30, 5.0, 10.0));
            auditLog.log("SEED loaded 6 sample components");
        } catch (DuplicateIdException e) {
            throw new IllegalStateException("Seed data has duplicate ids", e);
        }
    }
}
