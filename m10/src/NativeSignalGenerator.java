/**
 * The real drop-in replacement for SignalGenerator, carried over from
 * Module 9. Implementing LevelSource here is the only change this
 * chapter makes to it - the class already had a matching next()
 * method; nothing about its actual behavior changed at all.
 */
public class NativeSignalGenerator implements LevelSource {

    static {
        System.loadLibrary("coursenative");
    }

    /** Returns the current 1-minute system load average, via the POSIX
     *  getloadavg() C library call - see nativebridge.c. On a system
     *  with N CPU cores, a load average of N means "fully utilized." */
    private static native double currentSystemLoad();

    @Override
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
