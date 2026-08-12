import java.util.Objects;

/**
 * Component is the shared base for every kind of part in the inventory.
 * It's abstract because "a Component" on its own is meaningless - you
 * always have a specific kind of component (a Resistor, an IC, a Relay).
 * Making that explicit with `abstract` stops anyone from writing
 * `new Component(...)` and forces every subclass to supply specs().
 */
public abstract class Component implements Identifiable {

    // Encapsulation: fields are private. The outside world interacts
    // through getters (and a couple of controlled setters) only - this
    // class decides for itself how its own state can change.
    private final int id;
    private final String name;
    private final Category category;
    private int quantity;

    protected Component(int id, String name, Category category, int quantity) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.quantity = quantity;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Category getCategory() {
        return category;
    }

    public int getQuantity() {
        return quantity;
    }

    public void adjustQuantity(int delta) {
        int newQuantity = quantity + delta;
        if (newQuantity < 0) {
            throw new IllegalArgumentException("Quantity cannot go negative for id=" + id);
        }
        quantity = newQuantity;
    }

    // Every subclass MUST provide its own specs() - this is the abstract
    // method that makes polymorphism useful: Inventory can call
    // component.specs() on ANY Component without knowing or caring
    // which subclass it actually is.
    public abstract String specs();

    // A concrete (non-abstract) method that every subclass inherits
    // as-is. Subclasses don't need to - and shouldn't - reimplement this.
    public String summary() {
        return "#%d %-20s [%s] qty=%d | %s".formatted(
                id, name, category.getLabel(), quantity, specs());
    }

    @Override
    public String toString() {
        return summary();
    }

    // Two components are considered "equal" if they have the same id -
    // id is the one field that uniquely identifies a physical inventory
    // entry, regardless of what its name/quantity happen to be right now.
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Component other)) return false;
        return id == other.id;
    }

    @Override
    public int hashCode() {
        // Must be consistent with equals(): if equals() says two objects
        // are equal, hashCode() MUST return the same value for both, or
        // hash-based collections (HashMap/HashSet, coming in Module 3)
        // will silently misbehave.
        return Objects.hash(id);
    }
}
