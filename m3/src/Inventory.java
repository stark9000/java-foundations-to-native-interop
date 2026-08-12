import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * Inventory v2 extends the generic Repository instead of hand-rolling
 * an array. Everything domain-specific (categories, stats, sorting)
 * lives here; everything generic (storage, lookup, duplicate/missing
 * handling) is inherited from Repository.
 */
public class Inventory extends Repository<Component> {

    // Comparator constants: named, reusable orderings. Comparable
    // (on Component itself) gives ONE natural order (by id) - these
    // give the caller a choice of several other orders, picked at the
    // call site instead of baked into the class.
    public static final Comparator<Component> BY_NAME =
            Comparator.comparing(Component::getName);

    public static final Comparator<Component> BY_QUANTITY_DESC =
            Comparator.comparingInt(Component::getQuantity).reversed();

    public static final Comparator<Component> BY_CATEGORY_THEN_NAME =
            Comparator.comparing(Component::getCategory)
                    .thenComparing(Component::getName);

    public List<Component> allSortedBy(Comparator<Component> comparator) {
        // .sorted() would be nice here (Module 4), but for now: copy,
        // then sort the copy in place so we never mutate internal state.
        List<Component> copy = new java.util.ArrayList<>(all());
        copy.sort(comparator);
        return copy;
    }

    public List<Component> findByCategory(Category category) {
        List<Component> result = new java.util.ArrayList<>();
        for (Component c : ordered) {
            if (c.getCategory() == category) {
                result.add(c);
            }
        }
        return result;
    }

    /**
     * Removes every component in a category. This uses an explicit
     * Iterator instead of a for-each loop on purpose: mutating a List
     * while iterating it with for-each throws
     * ConcurrentModificationException. Iterator.remove() is the one
     * safe way to delete elements mid-iteration.
     */
    public int removeByCategory(Category category) {
        int removedCount = 0;
        Iterator<Component> it = ordered.iterator();
        while (it.hasNext()) {
            Component c = it.next();
            if (c.getCategory() == category) {
                it.remove();          // safe: removes from 'ordered' via the iterator
                byId.remove(c.getId()); // keep the lookup map in sync by hand
                removedCount++;
            }
        }
        return removedCount;
    }

    public Stats summarize() {
        return new Stats(this);
    }

    public static class Stats {
        private final int totalItems;
        private final int totalQuantity;

        private Stats(Inventory inventory) {
            int qty = 0;
            for (Component c : inventory.ordered) {
                qty += c.getQuantity();
            }
            this.totalItems = inventory.ordered.size();
            this.totalQuantity = qty;
        }

        public int getTotalItems() { return totalItems; }
        public int getTotalQuantity() { return totalQuantity; }

        @Override
        public String toString() {
            return "%d distinct components, %d units total".formatted(totalItems, totalQuantity);
        }
    }
}
