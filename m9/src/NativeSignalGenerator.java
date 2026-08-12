/**
 * A genuine drop-in replacement for Module 6's SignalGenerator: same
 * shape (a next() method returning a double in [0,1]), but backed by
 * an actual OS-level reading - the system's 1-minute load average -
 * instead of a sine wave and some random jitter.
 *
 * VuMeterPanel (Module 6/7) never needed to change to make this swap
 * possible: it was written against "a class with a next() method,"
 * not against SignalGenerator specifically. That decoupling is what
 * this whole chapter has been building toward.
 */
public class NativeSignalGenerator {

    static {
        System.loadLibrary("coursenative");
    }

    /** Returns the current 1-minute system load average, via the POSIX
     *  getloadavg() C library call - see nativebridge.c. On a system
     *  with N CPU cores, a load average of N means "fully utilized." */
    private static native double currentSystemLoad();

    public double next() {
        double load = currentSystemLoad();
        // Normalizing an open-ended "load average" number onto the
        // gauge's fixed [0,1] scale is a judgment call, not a fact -
        // 4.0 is a reasonable assumption for "fully loaded" on a
        // small multi-core machine, tuned for this project, not derived
        // from anything authoritative.
        return Math.max(0.0, Math.min(1.0, load / 4.0));
    }
}
