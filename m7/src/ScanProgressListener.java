/**
 * A functional interface, same idea as Module 4's use of Predicate/
 * Function/Consumer - except this one is purpose-built for this
 * domain instead of borrowed from java.util.function, because none of
 * the built-in ones take three parameters (completed, total, item).
 *
 * Whatever implements this gets called from the BACKGROUND SCAN THREAD,
 * not the main thread - worth remembering once this callback starts
 * touching shared state (see InventoryScanner's "why" notes).
 */
public interface ScanProgressListener {
    void onProgress(int completed, int total, Component justChecked);
}
