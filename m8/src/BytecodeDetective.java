import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Runs each demo class for real (proving the source behaves as
 * expected), then shells out to the JDK's own javap tool to disassemble
 * the compiled .class file and print the result - turning "read about
 * bytecode" into "watch your own code become bytecode," in one program.
 */
public class BytecodeDetective {

    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("=== 1. Class Loading ===");
        ClassLoaderExplorer.explore();

        System.out.println();
        System.out.println("=== 2. Autoboxing ===");
        System.out.println("AutoboxingDemo.demo() returns: " + AutoboxingDemo.demo());
        disassemble("AutoboxingDemo");

        System.out.println();
        System.out.println("=== 3. String switch ===");
        System.out.println("StringSwitchDemo.demo(\"SAT\") returns: " + StringSwitchDemo.demo("SAT"));
        disassemble("StringSwitchDemo");

        System.out.println();
        System.out.println("=== 4. String concatenation ===");
        System.out.println("StringConcatDemo.demo(...) returns: " + StringConcatDemo.demo("Alice", 3));
        disassemble("StringConcatDemo");

        System.out.println();
        System.out.println("=== 5. A native method, before any native code exists ===");
        disassemble("NativeBridgePreview");
    }

    private static void disassemble(String className) throws IOException, InterruptedException {
        System.out.println("--- javap -c -p " + className + " ---");

        // Runs the JDK's own javap tool as a subprocess against the
        // already-compiled .class file sitting next to this program.
        // -p includes private methods, so NativeBridgePreview's private
        // native method actually shows up in the output.
        ProcessBuilder pb = new ProcessBuilder("javap", "-c", "-p", className);
        pb.redirectErrorStream(true);
        Process process = pb.start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }
        process.waitFor();
    }
}
