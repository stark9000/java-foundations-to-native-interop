/**
 * A checked exception (extends Exception, not RuntimeException).
 *
 * Adding a duplicate id is an expected, routine possibility in this
 * application - user typos a used id, or code tries to re-import data
 * that's already present. Making this checked forces every caller of
 * Repository.add() to consciously decide how to handle it (catch it,
 * or explicitly declare that they pass the problem further up) rather
 * than letting it silently propagate and crash the program somewhere
 * unrelated.
 */
public class DuplicateIdException extends Exception {
    private final int id;

    public DuplicateIdException(int id) {
        super("An entry with id=" + id + " already exists.");
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
