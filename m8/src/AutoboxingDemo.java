public class AutoboxingDemo {

    /**
     * Nothing here looks unusual as Java source - that's the point.
     * javap -c reveals that "Integer boxed = 5" and "int unboxed = boxed"
     * are not free: the compiler silently inserted real method calls
     * (Integer.valueOf / Integer.intValue) that this source never
     * mentions.
     */
    public static int demo() {
        Integer boxed = 5;    // autoboxing
        int unboxed = boxed;  // auto-unboxing
        return unboxed + 1;
    }
}
