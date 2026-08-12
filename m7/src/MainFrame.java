import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import java.awt.Dimension;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

/**
 * Just the window chrome: a menu bar wired to InventoryPanel's public
 * methods, plus standard JFrame setup. MainFrame doesn't know how to
 * add a component, save a file, or run a scan - it only knows how to
 * ask InventoryPanel to do those things.
 */
public class MainFrame extends JFrame {

    public MainFrame(InventoryPanel panel) {
        super("Electronics Inventory (Module 7 Project)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setJMenuBar(buildMenuBar(panel));
        setContentPane(panel);
        pack();
        setMinimumSize(new Dimension(750, 480));
        setLocationRelativeTo(null); // center on screen
    }

    private JMenuBar buildMenuBar(InventoryPanel panel) {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        JMenuItem saveItem = new JMenuItem("Save...");
        // A key binding via accelerator - the modern, preferred way to
        // wire keyboard shortcuts to menu actions, instead of a raw
        // KeyListener that would need manual focus-tracking to work
        // correctly from anywhere in the window.
        saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
        saveItem.addActionListener(e -> panel.saveToFile());

        JMenuItem loadItem = new JMenuItem("Load...");
        loadItem.addActionListener(e -> panel.loadFromFile());

        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> dispose());

        fileMenu.add(saveItem);
        fileMenu.add(loadItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        JMenu editMenu = new JMenu("Edit");
        JMenuItem addItem = new JMenuItem("Add Component...");
        addItem.addActionListener(e -> panel.addComponent());
        JMenuItem removeItem = new JMenuItem("Remove Selected");
        removeItem.addActionListener(e -> panel.removeSelected());
        editMenu.add(addItem);
        editMenu.add(removeItem);

        JMenu toolsMenu = new JMenu("Tools");
        JMenuItem scanItem = new JMenuItem("Start Background Scan");
        scanItem.addActionListener(e -> panel.startScan());
        JMenuItem cancelScanItem = new JMenuItem("Cancel Scan");
        cancelScanItem.addActionListener(e -> panel.cancelScan());
        JMenuItem reportItem = new JMenuItem("Full Report...");
        reportItem.addActionListener(e -> panel.showFullReport());
        toolsMenu.add(scanItem);
        toolsMenu.add(cancelScanItem);
        toolsMenu.addSeparator();
        toolsMenu.add(reportItem);

        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Electronics Inventory\nModule 7 - Swing Desktop UI",
                "About", JOptionPane.INFORMATION_MESSAGE));
        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(toolsMenu);
        menuBar.add(helpMenu);
        return menuBar;
    }
}
