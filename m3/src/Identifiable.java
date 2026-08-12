/**
 * A pure contract: anything Identifiable promises to have an id, with
 * no shared implementation at all. This is deliberately kept separate
 * from Component (the abstract class below) to make the interface-vs-
 * abstract-class distinction concrete:
 *
 *   - interface  = "here is what you must be able to do"     (no state)
 *   - abstract class = "here is what you must do, PLUS shared code/state
 *                        every subclass gets for free"
 */
public interface Identifiable {
    int getId();
}
