import javax.swing.SwingWorker;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

/**
 * Module 5 ran InventoryScanner through a plain ExecutorService, and
 * its progress listener printed straight to System.out from the
 * background thread - fine for a console app, but printing IS safe
 * from any thread. Updating a Swing component from any thread other
 * than the EDT is NOT safe, and can cause anything from visual
 * glitches to a corrupted UI, unpredictably.
 *
 * SwingWorker solves this by construction: doInBackground() runs on a
 * background thread (same as before), but process() and done() are
 * GUARANTEED to run on the EDT - SwingWorker handles the hand-off
 * itself. InventoryScanner - the actual scanning logic - is completely
 * unchanged from Module 5; only how its progress reaches the UI is new.
 */
public class ScanWorker extends SwingWorker<List<Component>, ScanWorker.Progress> {

    public record Progress(int completed, int total, Component justChecked) {}

    private final InventoryScanner scanner;
    private final Consumer<Progress> onProgress;
    private final Consumer<List<Component>> onSuccess;
    private final Runnable onFinished;

    public ScanWorker(InventoryScanner scanner,
                       Consumer<Progress> onProgress,
                       Consumer<List<Component>> onSuccess,
                       Runnable onFinished) {
        this.scanner = scanner;
        this.onProgress = onProgress;
        this.onSuccess = onSuccess;
        this.onFinished = onFinished;
    }

    /** Runs on a SwingWorker background thread. Never touch a Swing
     *  component directly from in here - that's what publish() is for. */
    @Override
    protected List<Component> doInBackground() throws Exception {
        return scanner.scan((completed, total, checked) ->
                publish(new Progress(completed, total, checked)));
    }

    /**
     * Runs on the EDT. SwingWorker may batch several publish() calls
     * into a single process() call if the background thread produces
     * them faster than the EDT can keep up - so only the LAST chunk in
     * the batch needs to be shown; the intermediate ones are already
     * stale by the time this runs.
     */
    @Override
    protected void process(List<Progress> chunks) {
        onProgress.accept(chunks.get(chunks.size() - 1));
    }

    /** Also runs on the EDT, exactly once, after doInBackground() finishes
     *  - normally, via cancellation, or via an exception. */
    @Override
    protected void done() {
        try {
            if (!isCancelled()) {
                onSuccess.accept(get());
            }
        } catch (CancellationException e) {
            // Expected when cancel(true) was called - nothing further to do.
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            System.err.println("Scan failed: " + e.getCause());
        } finally {
            onFinished.run();
        }
    }
}
