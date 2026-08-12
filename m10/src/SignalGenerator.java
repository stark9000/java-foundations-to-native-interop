import java.util.Random;

/**
 * Produces a simulated audio level in [0.0, 1.0] each time next() is
 * called - a stand-in for a real signal. Module 9 replaces this exact
 * role with a native call reading actual audio hardware; nothing else
 * in this project needs to change when that happens, because everything
 * downstream only ever depends on "some class that produces a
 * double in [0,1] on demand," not on where the number came from.
 */
public class SignalGenerator implements LevelSource {

    private final Random random = new Random();
    private double t = 0;

    @Override
    public double next() {
        t += 0.08;
        double swell = 0.5 + 0.42 * Math.sin(t);              // slow musical swell
        double noise = (random.nextDouble() - 0.5) * 0.18;    // jitter
        double level = swell + noise;
        return Math.max(0.0, Math.min(1.0, level));            // clamp to [0,1]
    }
}
