import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Inventory inventory = new Inventory();

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
        System.out.println("=== Electronics Inventory v3 (Module 4 Project) ===");

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
                    case "8" -> viewComponent(scanner, inventory);
                    case "9" -> reportsMenu(scanner, inventory);
                    case "10" -> running = false;
                    default -> System.out.println("Not a valid option, try again.\n");
                }
            } catch (NumberFormatException e) {
                System.out.println("That wasn't a valid number - try again.\n");
            } catch (DuplicateIdException e) {
                System.out.println("Couldn't add: " + e.getMessage() + "\n");
            } catch (EntryNotFoundException e) {
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
                8) View one component by id
                9) Reports
                10) Exit
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
        for (Component c : inventory.allSortedBy(comparator)) {
            System.out.println(c);
        }
        System.out.println();
    }

    /**
     * The Module 3 version of this lookup had to null-check. Now that
     * Repository.findById() returns Optional<Component>, the "not
     * found" case is handled declaratively with ifPresentOrElse instead
     * of an if/else on a possibly-null reference.
     */
    private static void viewComponent(Scanner scanner, Inventory inventory) {
        System.out.print("Id: ");
        int id = Integer.parseInt(scanner.nextLine().trim());

        inventory.findById(id).ifPresentOrElse(
                component -> System.out.println(component),
                () -> System.out.println("No component with id=" + id)
        );
        System.out.println();
    }

    private static void reportsMenu(Scanner scanner, Inventory inventory) {
        InventoryReport report = new InventoryReport(inventory);

        System.out.println("""
                --- Reports ---
                1) Full report (counts + totals + average, by category)
                2) Low-stock components (below a threshold)
                3) All component names, comma-separated
                ---------------""");
        System.out.print("Choose a report: ");
        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1" -> report.printFullReport();
            case "2" -> {
                System.out.print("Threshold: ");
                int threshold = Integer.parseInt(scanner.nextLine().trim());
                List<Component> low = report.lowStock(threshold);
                if (low.isEmpty()) {
                    System.out.println("Nothing below that threshold.\n");
                } else {
                    low.forEach(System.out::println); // method reference as a Consumer<Component>
                    System.out.println();
                }
            }
            case "3" -> {
                System.out.println(report.namesJoined());
                System.out.println();
            }
            default -> System.out.println("Unknown report.\n");
        }
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
        inventory.add(component);
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
        matches.forEach(System.out::println);
        System.out.println();
    }

    private static void removeComponent(Scanner scanner, Inventory inventory, AuditLog auditLog) {
        System.out.print("Id to remove: ");
        int id = Integer.parseInt(scanner.nextLine().trim());
        Component removed = inventory.removeById(id);
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
            inventory.add(new Resistor(3, "1/2W Metal Film", 8, 1000.0, 1.0));
            inventory.add(new IntegratedCircuit(4, "74HC595 Shift Register", 12, "74HC595", 16));
            inventory.add(new IntegratedCircuit(5, "ATmega328P", 3, "ATMEGA328P-PU", 28));
            inventory.add(new Relay(6, "5V SPDT Relay", 30, 5.0, 10.0));
            auditLog.log("SEED loaded 6 sample components");
        } catch (DuplicateIdException e) {
            throw new IllegalStateException("Seed data has duplicate ids", e);
        }
    }
}
