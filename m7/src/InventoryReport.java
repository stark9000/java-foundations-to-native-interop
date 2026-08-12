import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

/**
 * InventoryReport is a new class, not a new method bolted onto
 * Inventory. Same reasoning as AuditLog in Module 3: Inventory owns
 * the data, and reporting is a derived, read-only view over that data.
 * Keeping it separate means Inventory doesn't grow a pile of one-off
 * reporting methods it has no real business owning.
 */
public class InventoryReport {

    private final List<Component> components;

    public InventoryReport(Inventory inventory) {
        this.components = inventory.all();
    }

    /** Groups every component by its category. Collectors.groupingBy is
     *  itself built on top of reduce - it walks the stream once, and for
     *  each element, decides which output bucket (here, a Category key)
     *  it belongs in. */
    public Map<Category, List<Component>> byCategory() {
        return components.stream()
                .collect(Collectors.groupingBy(Component::getCategory));
    }

    /** Same grouping, but collecting a count instead of the components themselves. */
    public Map<Category, Long> countByCategory() {
        return components.stream()
                .collect(Collectors.groupingBy(Component::getCategory, Collectors.counting()));
    }

    /** Same grouping again, this time summing a field (quantity) per group. */
    public Map<Category, Integer> totalQuantityByCategory() {
        return components.stream()
                .collect(Collectors.groupingBy(
                        Component::getCategory,
                        Collectors.summingInt(Component::getQuantity)));
    }

    /** OptionalDouble - the primitive-specialized sibling of Optional<T>,
     *  returned because "average of zero components" is genuinely undefined,
     *  not just inconveniently absent. */
    public OptionalDouble averageQuantity() {
        return components.stream()
                .mapToInt(Component::getQuantity)
                .average();
    }

    public List<Component> lowStock(int threshold) {
        return components.stream()
                .filter(c -> c.getQuantity() < threshold)   // Predicate<Component>
                .sorted(Comparator.comparingInt(Component::getQuantity))
                .toList();
    }

    public String namesJoined() {
        return components.stream()
                .map(Component::getName)          // Function<Component, String>
                .collect(Collectors.joining(", "));
    }

    /** Same total as Inventory.Stats.getTotalQuantity(), computed a
     *  different way - via reduce() instead of sum() - purely to show
     *  reduce() explicitly. In real code, prefer sum()/mapToInt() as
     *  Inventory.Stats does; reduce() is the general-purpose tool
     *  underneath, useful once your combining logic is more than "+". */
    public int totalQuantityViaReduce() {
        return components.stream()
                .map(Component::getQuantity)
                .reduce(0, Integer::sum);          // BinaryOperator<Integer>
    }

    public void printFullReport() {
        System.out.println("=== Inventory Report ===");
        countByCategory().forEach((category, count) ->
                System.out.println(category.getLabel() + ": " + count + " distinct component(s)"));
        System.out.println();

        totalQuantityByCategory().forEach((category, total) ->
                System.out.println(category.getLabel() + ": " + total + " units total"));
        System.out.println();

        averageQuantity().ifPresentOrElse(
                avg -> System.out.printf("Average quantity per component: %.1f%n", avg),
                () -> System.out.println("No components to average."));
        System.out.println();
    }
}
