import java.util.Scanner;

/**
 * Module 1 Project: CLI Unit Converter
 *
 * This is the entry point. Its only job is to show a menu, read input,
 * and dispatch to the Converter class. Keeping Main "dumb" (no actual
 * conversion math in here) is a habit worth building early: it separates
 * "talking to the user" from "doing the work", which pays off hugely
 * once programs get bigger (Module 2 onward).
 */
public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean running = true; // controls the loop - a plain boolean flag

        System.out.println("=== CLI Unit Converter (Module 1 Project) ===");

        // A classic "menu loop" - runs until the user chooses to exit.
        // This single loop is the backbone of almost every CLI tool you'll write.
        while (running) {
            printMenu();
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine().trim();

            // switch on a String - only possible because Java lets switch
            // work on String, not just int/enum. Each branch is intentionally
            // a one-liner that delegates to a dedicated method below.
            switch (choice) {
                case "1" -> handleTemperature(scanner);
                case "2" -> handleDistance(scanner);
                case "3" -> handlePermissionFlags(scanner);
                case "4" -> running = false;
                default -> System.out.println("Not a valid option, try again.\n");
            }
        }

        System.out.println("Goodbye!");
        scanner.close();
    }

    private static void printMenu() {
        System.out.println("""
                ------------------------------
                1) Temperature (C / F / K)
                2) Distance (km / miles / m / ft)
                3) Decode permission flags (bitwise)
                4) Exit
                ------------------------------""");
    }

    private static void handleTemperature(Scanner scanner) {
        System.out.print("Enter value: ");
        // Parsing user input from String to double - a cast/conversion
        // you will do constantly. Note: this will throw if input isn't
        // numeric - we cover proper exception handling in Module 3.
        double value = Double.parseDouble(scanner.nextLine().trim());

        System.out.print("From unit (C/F/K): ");
        char from = Character.toUpperCase(scanner.nextLine().trim().charAt(0));

        System.out.println("Celsius:    " + Converter.toCelsius(value, from));
        System.out.println("Fahrenheit: " + Converter.toFahrenheit(value, from));
        System.out.println("Kelvin:     " + Converter.toKelvin(value, from));
        System.out.println();
    }

    private static void handleDistance(Scanner scanner) {
        System.out.print("Enter value: ");
        double value = Double.parseDouble(scanner.nextLine().trim());

        System.out.print("From unit (km/mi/m/ft): ");
        String from = scanner.nextLine().trim().toLowerCase();

        double meters = Converter.toMeters(value, from);
        System.out.printf("Kilometers: %.4f%n", meters / 1000.0);
        System.out.printf("Miles:      %.4f%n", meters / 1609.344);
        System.out.printf("Meters:     %.4f%n", meters);
        System.out.printf("Feet:       %.4f%n", meters * 3.28084);
        System.out.println();
    }

    private static void handlePermissionFlags(Scanner scanner) {
        System.out.print("Enter a permission byte (0-255): ");
        int value = Integer.parseInt(scanner.nextLine().trim());

        // Casting int -> byte on purpose, to demonstrate narrowing
        // conversion and why we mask with 0xFF first (see Converter).
        String[] active = Converter.decodePermissions((byte) value);

        System.out.println("Active permissions: " + String.join(", ", active));
        System.out.println();
    }
}
