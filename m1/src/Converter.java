import java.util.ArrayList;
import java.util.List;

/**
 * All conversion logic lives here, separate from Main (which only handles
 * I/O). Every method is static because a Converter has no state of its
 * own - it's a pure "verb", not a "thing". This is a natural, low-stakes
 * place to first meet method overloading and varargs.
 */
public class Converter {

    // ---------- Temperature ----------
    // Three small methods instead of one giant one: each does exactly
    // one job and reads like a sentence at the call site.

    public static double toCelsius(double value, char fromUnit) {
        return switch (fromUnit) {
            case 'C' -> value;
            case 'F' -> (value - 32) * 5.0 / 9.0;
            case 'K' -> value - 273.15;
            default -> throw new IllegalArgumentException("Unknown unit: " + fromUnit);
        };
    }

    public static double toFahrenheit(double value, char fromUnit) {
        double celsius = toCelsius(value, fromUnit);
        return celsius * 9.0 / 5.0 + 32;
    }

    public static double toKelvin(double value, char fromUnit) {
        double celsius = toCelsius(value, fromUnit);
        return celsius + 273.15;
    }

    // ---------- Distance ----------
    // Normalize everything to meters first, then let the caller derive
    // whatever units it wants. One conversion "hub" instead of N*N
    // direct conversions between every pair of units.

    public static double toMeters(double value, String fromUnit) {
        return switch (fromUnit) {
            case "km" -> value * 1000.0;
            case "mi" -> value * 1609.344;
            case "m"  -> value;
            case "ft" -> value / 3.28084;
            default -> throw new IllegalArgumentException("Unknown unit: " + fromUnit);
        };
    }

    // ---------- Overloading example ----------
    // Two methods, same name, different parameter lists. This is what
    // "overloading" means in practice - the compiler picks the right one
    // based on the arguments you pass.

    public static double average(double a, double b) {
        return (a + b) / 2.0;
    }

    public static double average(double... values) {
        // varargs: lets callers write average(1,2,3,4) without an array.
        // Internally, 'values' IS just a double[].
        if (values.length == 0) return 0.0;
        double sum = 0;
        for (double v : values) {
            sum += v;
        }
        return sum / values.length;
    }

    // ---------- Bitwise permission flag decoder ----------
    // Each flag occupies exactly one bit of a byte. This is a classic,
    // real-world use of bitwise operators: 8 independent booleans packed
    // into a single byte instead of 8 separate boolean fields.

    public static final int READ    = 1;      // 0000_0001
    public static final int WRITE   = 1 << 1; // 0000_0010
    public static final int EXECUTE = 1 << 2; // 0000_0100
    public static final int DELETE  = 1 << 3; // 0000_1000
    public static final int SHARE   = 1 << 4; // 0001_0000

    private static final String[] FLAG_NAMES = {
        "READ", "WRITE", "EXECUTE", "DELETE", "SHARE"
    };
    private static final int[] FLAG_VALUES = {
        READ, WRITE, EXECUTE, DELETE, SHARE
    };

    public static String[] decodePermissions(byte permissionByte) {
        // byte is signed in Java, so a value like (byte) 0b1000_0000 is
        // negative. Masking with 0xFF promotes it to an int and treats
        // it as the unsigned bit pattern we actually mean.
        int bits = permissionByte & 0xFF;

        List<String> result = new ArrayList<>();
        for (int i = 0; i < FLAG_VALUES.length; i++) {
            // The core bitwise test: AND the bits with a single-bit mask.
            // If the result is non-zero, that bit was set.
            if ((bits & FLAG_VALUES[i]) != 0) {
                result.add(FLAG_NAMES[i]);
            }
        }
        return result.toArray(new String[0]);
    }

    public static int encodePermissions(boolean read, boolean write, boolean execute,
                                          boolean delete, boolean share) {
        int bits = 0;
        if (read)    bits |= READ;
        if (write)   bits |= WRITE;
        if (execute) bits |= EXECUTE;
        if (delete)  bits |= DELETE;
        if (share)   bits |= SHARE;
        return bits;
    }
}
