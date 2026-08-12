import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Simulates a slow, real-world inspection pass over the inventory -
 * imagine each component actually required a physical check (a scale,
 * a barcode scanner, a continuity tester) rather than an instant
 * in-memory read. This class is designed to run on a background
 * thread (see Main, which submits it to an ExecutorService) while the
 * console menu keeps responding on the main thread.
 *
 * Two fields here exist ONLY because this object is touched by two
 * threads at once - the scanning thread that runs scan(), and the
 * main thread that calls cancel() / getCompletedCount() while a scan
 * is in flight:
 */
public class InventoryScanner {

    private final Inventory inventory;

    // volatile: guarantees that when the main thread calls cancel()
    // (setting this to true), the scanning thread's very next read of
    // 'cancelled' is guaranteed to see that new value. Without volatile,
    // the JVM would be free to let the scanning thread keep reading a
    // cached, stale "false" indefinitely - cancel() could be a no-op
    // in practice, not just in theory.
    private volatile boolean cancelled = false;

    // AtomicInteger: the main thread reads this (getCompletedCount())
    // while the scan thread is concurrently writing it
    // (incrementAndGet()). A plain "int completed" field would have the
    // same visibility problem volatile solves above, PLUS a second
    // problem: incrementing isn't a single atomic step (it's a read,
    // then a write), so two threads touching a plain int concurrently
    // can lose updates. AtomicInteger makes the increment itself a
    // single indivisible operation.
    private final AtomicInteger completed = new AtomicInteger(0);

    public InventoryScanner(Inventory inventory) {
        this.inventory = inventory;
    }

    public void cancel() {
        cancelled = true;
    }

    public int getCompletedCount() {
        return completed.get();
    }

    /**
     * Runs on whatever thread calls it - in this project, always a
     * background thread from Main's ExecutorService, never the thread
     * driving the console menu.
     */
    public List<Component> scan(ScanProgressListener listener) throws InterruptedException {
        List<Component> flagged = new ArrayList<>();
        List<Component> items = inventory.all(); // one safe, immutable snapshot up front

        for (Component c : items) {
            if (cancelled) {
                break; // volatile read - sees cancel() from the other thread promptly
            }

            Thread.sleep(150); // stand-in for a slow physical/hardware check

            if (c.getQuantity() == 0) {
                flagged.add(c);
            }

            int done = completed.incrementAndGet();
            if (listener != null) {
                listener.onProgress(done, items.size(), c);
            }
        }
        return flagged;
    }
}
