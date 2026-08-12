import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Inventory inventory = new Inventory();

        // Multiple resources in one try-with-resources statement: both
        // Scanner and AuditLog implement Closeable/AutoCloseable, so both
        // are guaranteed to be closed, in reverse declaration order, no
        // matter how this block exits (normal exit, return, exception).
        try (Scanner scanner = new Scanner(System.in);
             AuditLog auditLog = new AuditLog(Path.of("inventory-audit.log"))) {

            seedSampleData(inventory, auditLog);
            runMenu(scanner, inventory, auditLog);

        } catch (IOException e) {
            System.err.println("Could not open audit log file: " + e.getMessage());
        }
    }

    private static void runMenu(Scanner scanner, Inventory inventory, AuditLog auditLog) {
        boolean running = true;
        System.out.println("=== Electronics Inventory v2 (Module 3 Project) ===");

        while (running) {
            printMenu();
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine().trim();

            try {
                switch (choice) {
                    case "1" -> listAll(inventory);
                    case "2" -> addComponent(scanner, inventory, auditLog);
                    case "3" -> searchByCategory(scanner, inventory);
                    case "4" -> removeComponent(scanner, inventory, auditLog);
                    case "5" -> removeCategory(scanner, inventory, auditLog);
                    case "6" -> listSorted(scanner, inventory);
                    case "7" -> System.out.println(inventory.summarize());
                    case "8" -> running = false;
                    default -> System.out.println("Not a valid option, try again.\n");
                }
            } catch (NumberFormatException e) {
                // Thrown by Integer/Double.parseInt/parseDouble on bad input.
                // Caught once, here, instead of wrapped around every single
                // parse call - keeps the menu handlers readable.
                System.out.println("That wasn't a valid number - try again.\n");
            } catch (DuplicateIdException e) {
                // Checked exception: the compiler required this to be
                // handled somewhere. This is where we chose to do it.
                System.out.println("Couldn't add: " + e.getMessage() + "\n");
            } catch (EntryNotFoundException e) {
                // Unchecked - we're choosing to catch it here for a clean
                // user-facing message, but nothing forced us to.
                System.out.println(e.getMessage() + "\n");
            }
        }
        System.out.println("Goodbye!");
    }

    private static void printMenu() {
        System.out.println("""
                ------------------------------
                1) List all components
                2) Add a component
                3) Search by category
                4) Remove a component by id
                5) Remove all components in a category
                6) List sorted (name / quantity / category)
                7) Show summary stats
                8) Exit
                ------------------------------""");
    }

    private static void listAll(Inventory inventory) {
        for (Component c : inventory.all()) {
            System.out.println(c);
        }
        System.out.println();
    }

    private static void listSorted(Scanner scanner, Inventory inventory) {
        System.out.print("Sort by (1=name, 2=quantity desc, 3=category): ");
        String choice = scanner.nextLine().trim();
        var comparator = switch (choice) {
            case "1" -> Inventory.BY_NAME;
            case "2" -> Inventory.BY_QUANTITY_DESC;
            case "3" -> Inventory.BY_CATEGORY_THEN_NAME;
            default -> null;
        };
        if (comparator == null) {
            System.out.println("Unknown sort option.\n");
            return;
        }
        List<Component> sorted = inventory.allSortedBy(comparator);
        for (Component c : sorted) {
            System.out.println(c);
        }
        System.out.println();
    }

    private static void addComponent(Scanner scanner, Inventory inventory, AuditLog auditLog)
            throws DuplicateIdException {
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
        inventory.add(component); // may throw DuplicateIdException - handled by caller
        auditLog.log("ADD id=" + id + " name=\"" + name + "\" qty=" + qty);
        System.out.println("Added: " + component + "\n");
    }

    private static void searchByCategory(Scanner scanner, Inventory inventory) {
        System.out.print("Category (1=Resistor, 2=IC, 3=Relay): ");
        String choice = scanner.nextLine().trim();
        Category category = categoryFromChoice(choice);
        if (category == null) {
            System.out.println("Unknown category.\n");
            return;
        }
        List<Component> matches = inventory.findByCategory(category);
        if (matches.isEmpty()) {
            System.out.println("No components in that category.\n");
            return;
        }
        for (Component c : matches) {
            System.out.println(c);
        }
        System.out.println();
    }

    private static void removeComponent(Scanner scanner, Inventory inventory, AuditLog auditLog) {
        System.out.print("Id to remove: ");
        int id = Integer.parseInt(scanner.nextLine().trim());
        Component removed = inventory.removeById(id); // may throw EntryNotFoundException
        auditLog.log("REMOVE id=" + id + " name=\"" + removed.getName() + "\"");
        System.out.println("Removed: " + removed + "\n");
    }

    private static void removeCategory(Scanner scanner, Inventory inventory, AuditLog auditLog) {
        System.out.print("Category to clear (1=Resistor, 2=IC, 3=Relay): ");
        String choice = scanner.nextLine().trim();
        Category category = categoryFromChoice(choice);
        if (category == null) {
            System.out.println("Unknown category.\n");
            return;
        }
        int removed = inventory.removeByCategory(category);
        auditLog.log("REMOVE_CATEGORY category=" + category + " count=" + removed);
        System.out.println("Removed " + removed + " component(s).\n");
    }

    private static Category categoryFromChoice(String choice) {
        return switch (choice) {
            case "1" -> Category.RESISTOR;
            case "2" -> Category.INTEGRATED_CIRCUIT;
            case "3" -> Category.RELAY;
            default -> null;
        };
    }

    private static void seedSampleData(Inventory inventory, AuditLog auditLog) {
        try {
            inventory.add(new Resistor(1, "1/4W Carbon Film", 200, 220.0, 5.0));
            inventory.add(new Resistor(2, "1/4W Carbon Film", 150, 4700.0, 5.0));
            inventory.add(new IntegratedCircuit(3, "74HC595 Shift Register", 12, "74HC595", 16));
            inventory.add(new Relay(4, "5V SPDT Relay", 30, 5.0, 10.0));
            auditLog.log("SEED loaded 4 sample components");
        } catch (DuplicateIdException e) {
            // Can't actually happen with hardcoded unique ids above, but
            // add() is a checked exception, so this catch is mandatory -
            // exactly the compiler forcing us to at least consider it.
            throw new IllegalStateException("Seed data has duplicate ids", e);
        }
    }
}
