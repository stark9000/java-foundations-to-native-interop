import java.util.Comparator;
import java.util.List;

/**
 * Every manual for-loop from Module 3's Inventory is replaced here with
 * a Stream pipeline. The behavior is identical - only the mechanism
 * changed, which is the point: Streams are a different way to express
 * the same "walk a collection, do something" operations, not a
 * different set of capabilities.
 */
public class Inventory extends Repository<Component> {

    public static final Comparator<Component> BY_NAME =
            Comparator.comparing(Component::getName);

    public static final Comparator<Component> BY_QUANTITY_DESC =
            Comparator.comparingInt(Component::getQuantity).reversed();

    public static final Comparator<Component> BY_CATEGORY_THEN_NAME =
            Comparator.comparing(Component::getCategory)
                    .thenComparing(Component::getName);

    public List<Component> allSortedBy(Comparator<Component> comparator) {
        // .sorted(comparator) on the stream, then .toList() to collect
        // into an unmodifiable List - no manual copy-then-sort needed.
        return ordered.stream()
                .sorted(comparator)
                .toList();
    }

    public List<Component> findByCategory(Category category) {
        // filter() takes a Predicate<Component> - here, a lambda that
        // captures 'category' from the enclosing method.
        return ordered.stream()
                .filter(c -> c.getCategory() == category)
                .toList();
    }

    /**
     * Module 3 used an explicit Iterator here because mutating a List
     * during a for-each loop isn't safe. List.removeIf() is the
     * functional-style equivalent - it takes a Predicate<Component> and
     * handles the "safely remove while walking the list" problem
     * internally, so neither a manual Iterator nor a stream pipeline is
     * needed for the removal itself.
     */
    public int removeByCategory(Category category) {
        List<Integer> idsToRemove = ordered.stream()
                .filter(c -> c.getCategory() == category)
                .map(Component::getId) // method reference: Component -> its id
                .toList();

        ordered.removeIf(c -> c.getCategory() == category);
        idsToRemove.forEach(byId::remove); // method reference bound to the byId map

        return idsToRemove.size();
    }

    public Stats summarize() {
        return new Stats(this);
    }

    public static class Stats {
        private final int totalItems;
        private final int totalQuantity;

        private Stats(Inventory inventory) {
            this.totalItems = inventory.ordered.size();
            // mapToInt avoids boxing every quantity into an Integer just
            // to add them up - IntStream.sum() works on primitive ints
            // directly. Compare with InventoryReport, which uses the
            // boxed Collectors.summingInt() equivalent for grouped totals.
            this.totalQuantity = inventory.ordered.stream()
                    .mapToInt(Component::getQuantity)
                    .sum();
        }

        public int getTotalItems() { return totalItems; }
        public int getTotalQuantity() { return totalQuantity; }

        @Override
        public String toString() {
            return "%d distinct components, %d units total".formatted(totalItems, totalQuantity);
        }
    }
}
