import java.util.Scanner;

/**
 * Console front-end for the inventory. As in Module 1, Main only
 * handles talking to the user - all real behavior lives in Inventory
 * and the Component hierarchy.
 */
public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Inventory inventory = new Inventory();
        seedSampleData(inventory);

        boolean running = true;
        System.out.println("=== Electronics Inventory (Module 2 Project) ===");

        while (running) {
            printMenu();
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> listAll(inventory);
                case "2" -> addComponent(scanner, inventory);
                case "3" -> searchByCategory(scanner, inventory);
                case "4" -> removeComponent(scanner, inventory);
                case "5" -> System.out.println(inventory.summarize());
                case "6" -> running = false;
                default -> System.out.println("Not a valid option, try again.\n");
            }
        }
        System.out.println("Goodbye!");
        scanner.close();
    }

    private static void printMenu() {
        System.out.println("""
                ------------------------------
                1) List all components
                2) Add a component
                3) Search by category
                4) Remove a component by id
                5) Show summary stats
                6) Exit
                ------------------------------""");
    }

    private static void listAll(Inventory inventory) {
        // Polymorphism in action: this loop doesn't know or care whether
        // each element is a Resistor, IntegratedCircuit, or Relay - it
        // just calls summary(), and each object's real, dynamic type
        // decides what specs() (called inside summary()) actually prints.
        for (Component c : inventory.all()) {
            System.out.println(c);
        }
        System.out.println();
    }

    private static void addComponent(Scanner scanner, Inventory inventory) {
        System.out.print("Category (1=Resistor, 2=IC, 3=Relay): ");
        String catChoice = scanner.nextLine().trim();

        System.out.print("Id (integer): ");
        int id = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("Name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Quantity: ");
        int qty = Integer.parseInt(scanner.nextLine().trim());

        Component component = switch (catChoice) {
            case "1" -> {
                System.out.print("Resistance (ohms): ");
                double ohms = Double.parseDouble(scanner.nextLine().trim());
                System.out.print("Tolerance (%): ");
                double tol = Double.parseDouble(scanner.nextLine().trim());
                yield new Resistor(id, name, qty, ohms, tol);
            }
            case "2" -> {
                System.out.print("Part number: ");
                String pn = scanner.nextLine().trim();
                System.out.print("Pin count: ");
                int pins = Integer.parseInt(scanner.nextLine().trim());
                yield new IntegratedCircuit(id, name, qty, pn, pins);
            }
            case "3" -> {
                System.out.print("Coil voltage: ");
                double v = Double.parseDouble(scanner.nextLine().trim());
                System.out.print("Contact rating (A): ");
                double amps = Double.parseDouble(scanner.nextLine().trim());
                yield new Relay(id, name, qty, v, amps);
            }
            default -> null;
        };

        if (component == null) {
            System.out.println("Unknown category, nothing added.\n");
            return;
        }
        inventory.add(component);
        System.out.println("Added: " + component + "\n");
    }

    private static void searchByCategory(Scanner scanner, Inventory inventory) {
        System.out.print("Category (1=Resistor, 2=IC, 3=Relay): ");
        String choice = scanner.nextLine().trim();
        Category category = switch (choice) {
            case "1" -> Category.RESISTOR;
            case "2" -> Category.INTEGRATED_CIRCUIT;
            case "3" -> Category.RELAY;
            default -> null;
        };
        if (category == null) {
            System.out.println("Unknown category.\n");
            return;
        }
        Component[] matches = inventory.findByCategory(category);
        if (matches.length == 0) {
            System.out.println("No components in that category.\n");
            return;
        }
        for (Component c : matches) {
            System.out.println(c);
        }
        System.out.println();
    }

    private static void removeComponent(Scanner scanner, Inventory inventory) {
        System.out.print("Id to remove: ");
        int id = Integer.parseInt(scanner.nextLine().trim());
        boolean removed = inventory.removeById(id);
        System.out.println(removed ? "Removed.\n" : "No component with that id.\n");
    }

    private static void seedSampleData(Inventory inventory) {
        inventory.add(new Resistor(1, "1/4W Carbon Film", 200, 220.0, 5.0));
        inventory.add(new Resistor(2, "1/4W Carbon Film", 150, 4700.0, 5.0));
        inventory.add(new IntegratedCircuit(3, "74HC595 Shift Register", 12, "74HC595", 16));
        inventory.add(new Relay(4, "5V SPDT Relay", 30, 5.0, 10.0));
    }
}
