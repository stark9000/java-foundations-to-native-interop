/**
 * Implemented entirely in Java, but CALLED from native C code via
 * JNIEnv's CallVoidMethod - the reverse direction of every other
 * native method in this chapter. The native side never needs to know
 * this is an interface implemented by a lambda; it just needs the
 * method's name and JNI type signature, "(ID)V", to find it.
 */
public interface ProgressCallback {
    void onProgress(int index, double value);
}
