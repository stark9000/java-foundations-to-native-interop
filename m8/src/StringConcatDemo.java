public class StringConcatDemo {

    /**
     * The "+" operator on Strings isn't a real operator at the bytecode
     * level - String has no operator overloading in the JVM. javap -c
     * shows what javac actually compiles this into: on modern JDKs,
     * a single invokedynamic call to a runtime-generated string
     * concatenation helper (older JDKs instead show explicit
     * StringBuilder.append() calls chained together).
     */
    public static String demo(String name, int count) {
        return "Hello " + name + ", you have " + count + " items";
    }
}
