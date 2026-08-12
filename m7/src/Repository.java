import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Same generic, bounded Repository from Module 3, with one change:
 * findById() now returns Optional<T> instead of a possibly-null T.
 * "Might not be there" is now part of the method's type signature,
 * not something callers have to remember on their own.
 */
public class Repository<T extends Identifiable> {

    protected final Map<Integer, T> byId = new HashMap<>();
    protected final List<T> ordered = new ArrayList<>();

    public void add(T item) throws DuplicateIdException {
        int id = item.getId();
        if (byId.containsKey(id)) {
            throw new DuplicateIdException(id);
        }
        byId.put(id, item);
        ordered.add(item);
    }

    public Optional<T> findById(int id) {
        // Optional.ofNullable wraps a possibly-null value in an Optional.
        // If byId.get(id) is null (not found), this becomes Optional.empty()
        // rather than a bare null escaping into the caller's hands.
        return Optional.ofNullable(byId.get(id));
    }

    public T removeById(int id) {
        // orElseThrow: if the Optional is empty, throw the given
        // exception right here instead of forcing every caller to
        // separately check for "not found" themselves.
        T removed = findById(id).orElseThrow(() -> new EntryNotFoundException(id));
        byId.remove(id);
        ordered.remove(removed);
        return removed;
    }

    public List<T> all() {
        return List.copyOf(ordered);
    }

    /** Removes every entry. Used by InventoryFileStore.load() to replace
     *  the current contents wholesale after a file has been fully read
     *  and parsed successfully - see the "why" note in the chapter doc
     *  about not clearing until the new data is known to be good. */
    public void clear() {
        byId.clear();
        ordered.clear();
    }

    public int size() {
        return ordered.size();
    }
}
