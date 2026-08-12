import javax.swing.UIManager;
import java.awt.Color;

/**
 * A lightweight dark palette applied via UIManager defaults, rather
 * than a full custom LookAndFeel implementation - which is a
 * substantially bigger undertaking (a real LookAndFeel controls borders,
 * icons, and dozens of component behaviors, not just colors) and out of
 * scope here. Every Swing component created AFTER apply() runs picks up
 * these defaults automatically - which is exactly why apply() must run
 * before any component is constructed, not after.
 */
public class DarkTheme {

    public static void apply() {
        Color background = new Color(43, 43, 46);
        Color foreground = new Color(220, 220, 220);
        Color fieldBackground = new Color(60, 60, 64);
        Color selection = new Color(70, 110, 160);

        UIManager.put("Panel.background", background);
        UIManager.put("OptionPane.background", background);
        UIManager.put("OptionPane.messageForeground", foreground);
        UIManager.put("Label.foreground", foreground);
        UIManager.put("Button.background", fieldBackground);
        UIManager.put("Button.foreground", foreground);
        UIManager.put("TextField.background", fieldBackground);
        UIManager.put("TextField.foreground", foreground);
        UIManager.put("TextField.caretForeground", foreground);
        UIManager.put("TextArea.background", fieldBackground);
        UIManager.put("TextArea.foreground", foreground);
        UIManager.put("ComboBox.background", fieldBackground);
        UIManager.put("ComboBox.foreground", foreground);
        UIManager.put("Table.background", fieldBackground);
        UIManager.put("Table.foreground", foreground);
        UIManager.put("Table.selectionBackground", selection);
        UIManager.put("Table.selectionForeground", Color.WHITE);
        UIManager.put("TableHeader.background", background);
        UIManager.put("TableHeader.foreground", foreground);
        UIManager.put("ScrollPane.background", background);
        UIManager.put("Viewport.background", background);
        UIManager.put("MenuBar.background", background);
        UIManager.put("Menu.background", background);
        UIManager.put("Menu.foreground", foreground);
        UIManager.put("MenuItem.background", background);
        UIManager.put("MenuItem.foreground", foreground);
        UIManager.put("ProgressBar.background", fieldBackground);
        UIManager.put("ProgressBar.foreground", selection);
        UIManager.put("ToolBar.background", background);
        UIManager.put("TitledBorder.titleColor", foreground);
    }
}
