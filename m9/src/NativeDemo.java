public class NativeDemo {

    public static void main(String[] args) {
        System.out.println("=== 1. Simplest native method ===");
        System.out.println("NativeMath.add(2, 3) = " + NativeMath.add(2, 3));

        System.out.println();
        System.out.println("=== 2. Array in, primitive out ===");
        double[] samples = {0.5, -0.8, 0.3, -0.2, 0.9};
        System.out.println("NativeMath.rms(...) = " + NativeMath.rms(samples));

        System.out.println();
        System.out.println("=== 3. String in, String out ===");
        System.out.println(NativeMath.greet("Module 9"));

        System.out.println();
        System.out.println("=== 4. Exception thrown FROM native code ===");
        try {
            NativeMath.divide(10, 0);
        } catch (ArithmeticException e) {
            System.out.println("Caught in Java, as a real ArithmeticException: " + e.getMessage());
        }
        System.out.println("NativeMath.divide(10, 2) = " + NativeMath.divide(10, 2));

        System.out.println();
        System.out.println("=== 5. Native code calling back into Java ===");
        ProgressCallback callback = (index, value) ->
                System.out.printf("  callback from native code: index=%d value=%.2f%n", index, value);
        NativeMath.processWithProgress(samples, callback);

        System.out.println();
        System.out.println("=== 6. A real OS-level native signal ===");
        NativeSignalGenerator generator = new NativeSignalGenerator();
        for (int i = 0; i < 3; i++) {
            System.out.println("NativeSignalGenerator.next() = " + generator.next());
        }
    }
}
