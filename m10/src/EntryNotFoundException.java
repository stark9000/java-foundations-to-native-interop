/**
 * An unchecked exception (extends RuntimeException).
 *
 * Contrast this with DuplicateIdException. Looking up or removing an
 * id that doesn't exist is treated here as closer to a programming/
 * input error than a routine business condition - forcing a
 * try/catch (or throws declaration) at every single call to
 * findById()/removeById() throughout the codebase would be
 * disproportionate boilerplate for something that, in this app, we
 * expect to handle with one catch block at the UI boundary (see Main).
 *
 * This split is a judgment call, not a hard rule - part of the point
 * of this chapter is that "checked vs unchecked" is a design decision,
 * not something the compiler decides for you.
 */
public class EntryNotFoundException extends RuntimeException {
    private final int id;

    public EntryNotFoundException(int id) {
        super("No entry found with id=" + id);
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
