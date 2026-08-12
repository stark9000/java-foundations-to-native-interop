import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Saves and loads an Inventory as a small pipe-delimited text file -
 * NOT using Java's built-in Serializable mechanism. See the chapter
 * document for the full reasoning; in short: this format is human-
 * readable, diffable, debuggable with a text editor, and doesn't
 * silently break if a class's fields change shape later.
 *
 * Format: one component per line,
 *   TYPE|id|name|quantity|field4|field5
 */
public class InventoryFileStore {

    private static final String DELIM = "|";
    // A real format would escape delimiter characters inside names.
    // This one doesn't - a deliberate, documented limitation, not an
    // oversight. See the chapter doc's "why" notes.

    public static void save(Inventory inventory, Path path) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            for (Component c : inventory.all()) {
                writer.write(toLine(c));
                writer.newLine();
            }
        }
    }

    /**
     * Reads and parses the entire file into a temporary list FIRST, and
     * only clears/repopulates the live Inventory once every line has
     * parsed successfully. If line 40 of 50 is corrupt, the Inventory
     * the user already has loaded is left completely untouched instead
     * of ending up half-cleared.
     */
    public static void load(Inventory inventory, Path path) throws IOException, DuplicateIdException {
        List<Component> loaded = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.isBlank()) continue;
                try {
                    loaded.add(fromLine(line));
                } catch (RuntimeException e) {
                    throw new IOException("Bad data on line " + lineNumber + ": " + line, e);
                }
            }
        }

        inventory.clear();
        for (Component c : loaded) {
            inventory.add(c);
        }
    }

    private static String toLine(Component c) {
        // Pattern matching for instanceof (Java 16+): each branch both
        // tests the type AND introduces a correctly-typed local variable
        // (r, ic, relay) in one step - no separate cast needed.
        if (c instanceof Resistor r) {
            return String.join(DELIM, "RESISTOR", str(r.getId()), r.getName(),
                    str(r.getQuantity()), str(r.getResistanceOhms()), str(r.getTolerancePercent()));
        } else if (c instanceof IntegratedCircuit ic) {
            return String.join(DELIM, "IC", str(ic.getId()), ic.getName(),
                    str(ic.getQuantity()), ic.getPartNumber(), str(ic.getPinCount()));
        } else if (c instanceof Relay relay) {
            return String.join(DELIM, "RELAY", str(relay.getId()), relay.getName(),
                    str(relay.getQuantity()), str(relay.getCoilVoltage()), str(relay.getContactRatingAmps()));
        } else {
            throw new IllegalArgumentException("Unknown component type: " + c.getClass());
        }
    }

    private static Component fromLine(String line) {
        String[] parts = line.split("\\" + DELIM);
        String type = parts[0];
        int id = Integer.parseInt(parts[1]);
        String name = parts[2];
        int qty = Integer.parseInt(parts[3]);

        return switch (type) {
            case "RESISTOR" -> new Resistor(id, name, qty,
                    Double.parseDouble(parts[4]), Double.parseDouble(parts[5]));
            case "IC" -> new IntegratedCircuit(id, name, qty,
                    parts[4], Integer.parseInt(parts[5]));
            case "RELAY" -> new Relay(id, name, qty,
                    Double.parseDouble(parts[4]), Double.parseDouble(parts[5]));
            default -> throw new IllegalArgumentException("Unknown component type in file: " + type);
        };
    }

    private static String str(double d) { return Double.toString(d); }
    private static String str(int i) { return Integer.toString(i); }
}
