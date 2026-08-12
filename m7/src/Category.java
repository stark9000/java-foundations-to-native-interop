/**
 * An enum is a fixed set of named constants - but in Java, unlike C,
 * each constant can carry its own data and even behavior. Here every
 * category knows its own human-readable label, so callers never have
 * to maintain a separate lookup table.
 */
public enum Category {
    RESISTOR("Resistor"),
    INTEGRATED_CIRCUIT("Integrated Circuit"),
    RELAY("Relay");

    private final String label;

    // Enum constructors are implicitly private - you can never write
    // "new Category(...)" from outside. The constants above are the
    // only instances that will ever exist.
    Category(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
