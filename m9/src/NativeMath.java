/**
 * Every method here is declared but not implemented - exactly like
 * Module 8's NativeBridgePreview, except this time a real native
 * library actually backs every one of these signatures. The static
 * initializer block runs once, the first time this class is loaded,
 * and must succeed before any native method on it can be called.
 */
public class NativeMath {

    static {
        // Maps to libcoursenative.so (Linux), coursenative.dll (Windows),
        // or libcoursenative.dylib (macOS) - see Module 8, Section 5.
        System.loadLibrary("coursenative");
    }

    /** The simplest possible native method: two primitives in, one primitive out. */
    public static native int add(int a, int b);

    /** Passing an array from Java to native code, and getting a primitive back. */
    public static native double rms(double[] samples);

    /** Passing and returning a String - the trickiest "simple" type to get right. */
    public static native String greet(String name);

    /** Deliberately throws from native code when b == 0, to demonstrate
     *  exceptions crossing the JNI boundary in the native-to-Java direction. */
    public static native int divide(int a, int b);

    /** Calls back into Java (the callback's onProgress method) once per
     *  element while processing an array natively. */
    public static native void processWithProgress(double[] samples, ProgressCallback callback);
}
