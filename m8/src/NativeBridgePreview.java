public class NativeBridgePreview {

    /**
     * Declared, never implemented in Java, and never called anywhere in
     * this chapter - there's no native library backing it yet, and
     * calling it would throw UnsatisfiedLinkError. Its only purpose here
     * is to be disassembled: javap shows it as a method with the
     * "native" modifier and NO Code attribute at all, unlike every other
     * method in this project. The JVM is trusting that SOME future
     * System.loadLibrary() call will supply a real implementation for
     * this exact method signature - which is precisely the mechanism
     * Module 9 covers in full.
     */
    private native int nativeAdd(int a, int b);

    /** An ordinary method, compiled normally, for direct contrast. */
    public int javaAdd(int a, int b) {
        return a + b;
    }
}
