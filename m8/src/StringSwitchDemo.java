public class StringSwitchDemo {

    /**
     * Switching on a String isn't a special case the JVM understands
     * natively - the bytecode verifier has no concept of "string
     * equality" as a branch condition. javap -c shows what the
     * compiler actually does instead: call hashCode() to pick a
     * candidate branch quickly, then equals() to confirm it.
     */
    public static String demo(String day) {
        return switch (day) {
            case "MON", "TUE", "WED", "THU", "FRI" -> "weekday";
            case "SAT", "SUN" -> "weekend";
            default -> "unknown";
        };
    }
}
