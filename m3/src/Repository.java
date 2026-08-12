import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A generic base class: Repository<T> works for any T, but the bound
 * "T extends Identifiable" restricts that to only types that can
 * supply an id - without that bound, byId.put(item.getId(), item)
 * wouldn't compile, because plain T has no getId() method at all.
 *
 * This is the difference between raw generics and BOUNDED generics:
 * bounding a type parameter lets the generic class call real methods
 * on T, instead of only being able to store and return it untouched.
 *
 * Two collections are kept in sync on purpose:
 *   - byId    : HashMap, O(1) average lookup by id
 *   - ordered : ArrayList, preserves insertion order for display
 * Module 2's Inventory grew a plain array by hand to do roughly this
 * job; this class replaces that with the real Collections types it
 * was standing in for.
 */
public class Repository<T extends Identifiable> {

    protected final Map<Integer, T> byId = new HashMap<>();
    protected final List<T> ordered = new ArrayList<>();

    public void add(T item) throws DuplicateIdException {
        int id = item.getId(); // only possible because of the "extends Identifiable" bound
        if (byId.containsKey(id)) {
            throw new DuplicateIdException(id);
        }
        byId.put(id, item);
        ordered.add(item);
    }

    /**
     * Returns null if not found, rather than throwing. This is a
     * deliberate, imperfect choice: it means every caller has to
     * remember to null-check. Module 4 replaces this return type with
     * Optional<T>, which makes "might not be present" part of the type
     * itself instead of something you have to remember.
     */
    public T findById(int id) {
        return byId.get(id); // id is auto-boxed to Integer here
    }

    public T removeById(int id) {
        T removed = byId.remove(id);
        if (removed == null) {
            throw new EntryNotFoundException(id);
        }
        ordered.remove(removed);
        return removed;
    }

    /** An unmodifiable snapshot - callers can't accidentally mutate our internal list. */
    public List<T> all() {
        return List.copyOf(ordered);
    }

    public int size() {
        return ordered.size();
    }
}
