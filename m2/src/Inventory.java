import java.util.Arrays;

/**
 * Inventory deliberately stores components in a plain array that it
 * grows by hand, instead of reaching for ArrayList. Collections are
 * Module 3's subject - introducing them here, before explaining how
 * they work, would trade one lesson for a different, half-taught one.
 * This class gets rebuilt on top of real Collections in Module 3.
 */
public class Inventory {

    private Component[] items = new Component[4]; // small on purpose, to force growth early
    private int size = 0;

    public void add(Component component) {
        if (size == items.length) {
            grow();
        }
        items[size++] = component;
    }

    private void grow() {
        // Manual array growth: allocate bigger, copy everything across.
        // This is exactly what ArrayList does internally - seeing it
        // done by hand once makes ArrayList feel a lot less magical
        // when you meet it properly in Module 3.
        items = Arrays.copyOf(items, items.length * 2);
    }

    public Component findById(int id) {
        for (int i = 0; i < size; i++) {
            if (items[i].getId() == id) {
                return items[i];
            }
        }
        return null;
    }

    public Component[] findByCategory(Category category) {
        // Two-pass approach: count matches first, then allocate exactly
        // the right size. Simple, honest array work - no ArrayList to
        // lean on yet.
        int count = 0;
        for (int i = 0; i < size; i++) {
            if (items[i].getCategory() == category) count++;
        }
        Component[] result = new Component[count];
        int idx = 0;
        for (int i = 0; i < size; i++) {
            if (items[i].getCategory() == category) {
                result[idx++] = items[i];
            }
        }
        return result;
    }

    public Component[] all() {
        return Arrays.copyOf(items, size);
    }

    public int size() {
        return size;
    }

    public boolean removeById(int id) {
        for (int i = 0; i < size; i++) {
            if (items[i].getId() == id) {
                // shift everything after i back by one
                System.arraycopy(items, i + 1, items, i, size - i - 1);
                items[size - 1] = null;
                size--;
                return true;
            }
        }
        return false;
    }

    public Stats summarize() {
        return new Stats(this);
    }

    /**
     * A static nested class: it lives inside Inventory's namespace
     * (Inventory.Stats) because it only makes sense in relation to an
     * Inventory, but it doesn't need a reference to any particular
     * Inventory instance to exist - it's given one explicitly instead.
     * This is the right tool when a helper class is tightly scoped to
     * its outer class but doesn't need to reach into the outer
     * instance's private state implicitly.
     */
    public static class Stats {
        private final int totalItems;
        private final int totalQuantity;

        private Stats(Inventory inventory) {
            int qty = 0;
            for (int i = 0; i < inventory.size; i++) {
                qty += inventory.items[i].getQuantity();
            }
            this.totalItems = inventory.size;
            this.totalQuantity = qty;
        }

        public int getTotalItems() {
            return totalItems;
        }

        public int getTotalQuantity() {
            return totalQuantity;
        }

        @Override
        public String toString() {
            return "%d distinct components, %d units total".formatted(totalItems, totalQuantity);
        }
    }
}
