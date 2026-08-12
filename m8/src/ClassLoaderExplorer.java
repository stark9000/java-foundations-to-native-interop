/**
 * Prints the class loader chain for a couple of classes, to make
 * "class loading" concrete instead of abstract. Every class in a
 * running JVM was loaded by SOME ClassLoader - this just makes that
 * fact visible.
 */
public class ClassLoaderExplorer {

    public static void explore() {
        printLoaderChain("A JDK class", String.class);
        printLoaderChain("A class from this project", BytecodeDetective.class);

        System.out.println();
        System.out.println("java.class.path = " + System.getProperty("java.class.path"));
    }

    private static void printLoaderChain(String label, Class<?> type) {
        System.out.println(label + ": " + type.getName());
        ClassLoader loader = type.getClassLoader();

        if (loader == null) {
            // The bootstrap loader (which loads java.lang.*, java.util.*,
            // and the rest of the core JDK) isn't a Java object at all -
            // it's implemented in native code inside the JVM itself, so
            // getClassLoader() reports it as null rather than an instance.
            System.out.println("  loaded by: the bootstrap class loader (native code, represented as null)");
            return;
        }

        int depth = 1;
        while (loader != null) {
            System.out.println("  ".repeat(depth) + "loaded by: " + loader);
            // Parent delegation: every class loader asks its PARENT to
            // try loading a class first, and only tries itself if the
            // parent can't find it. This is why you can't accidentally
            // shadow java.lang.String with your own class on the
            // classpath - the bootstrap loader always gets first refusal.
            loader = loader.getParent();
            depth++;
        }
        System.out.println("  ".repeat(depth) + "loaded by: the bootstrap class loader (null)");
    }
}
