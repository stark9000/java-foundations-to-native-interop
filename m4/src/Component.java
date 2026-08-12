import java.util.Objects;

/**
 * Same Component as Module 2, with one addition: it now implements
 * Comparable<Component>, giving it a single "natural" ordering (by id).
 * This is deliberately kept separate from the named Comparators defined
 * in Inventory (by name, by quantity, by category) so both mechanisms
 * are visible side by side:
 *
 *   - Comparable = "this type has ONE natural, obvious order"
 *   - Comparator = "here is ONE OF POSSIBLY MANY ways to order this type"
 */
public abstract class Component implements Identifiable, Comparable<Component> {

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

    public abstract String specs();

    public String summary() {
        return "#%d %-20s [%s] qty=%d | %s".formatted(
                id, name, category.getLabel(), quantity, specs());
    }

    @Override
    public String toString() {
        return summary();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Component other)) return false;
        return id == other.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // Natural ordering: by id, ascending. Used implicitly whenever
    // Component is placed in a sorted structure (Collections.sort with
    // no explicit Comparator, TreeSet, etc.) without being told how.
    @Override
    public int compareTo(Component other) {
        return Integer.compare(this.id, other.id);
    }
}
