import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ItemEvent;
import java.util.Optional;

/**
 * A modal dialog for creating a new Component. Exposes only a static
 * showDialog() factory - there's no public constructor - so it's not
 * possible to forget to actually call setVisible() before reading a
 * result: showDialog() does both, in the right order, every time.
 */
public class ComponentFormDialog extends JDialog {

    private final JTextField idField = new JTextField();
    private final JTextField nameField = new JTextField();
    private final JTextField quantityField = new JTextField();
    private final JComboBox<Category> categoryCombo = new JComboBox<>(Category.values());

    private final JTextField resistanceField = new JTextField();
    private final JTextField toleranceField = new JTextField();
    private final JTextField partNumberField = new JTextField();
    private final JTextField pinCountField = new JTextField();
    private final JTextField coilVoltageField = new JTextField();
    private final JTextField contactRatingField = new JTextField();

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cardPanel = new JPanel(cardLayout);

    private Component result; // stays null unless the user successfully clicks OK

    private ComponentFormDialog(Window owner) {
        super(owner, "Add Component", ModalityType.APPLICATION_MODAL);
        buildUi();
    }

    public static Optional<Component> showDialog(Window owner) {
        ComponentFormDialog dialog = new ComponentFormDialog(owner);
        dialog.setVisible(true); // blocks the caller here - modal - until the dialog closes
        return Optional.ofNullable(dialog.result);
    }

    private void buildUi() {
        setLayout(new BorderLayout(8, 8));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        addRow(form, gbc, 0, "Category:", categoryCombo);
        addRow(form, gbc, 1, "Id:", idField);
        addRow(form, gbc, 2, "Name:", nameField);
        addRow(form, gbc, 3, "Quantity:", quantityField);

        // CardLayout stacks every card in the same space and shows
        // exactly one at a time - the category combo box's listener
        // below is the only thing that decides which.
        cardPanel.add(buildResistorFields(), Category.RESISTOR.name());
        cardPanel.add(buildIcFields(), Category.INTEGRATED_CIRCUIT.name());
        cardPanel.add(buildRelayFields(), Category.RELAY.name());
        cardPanel.setBorder(BorderFactory.createTitledBorder("Type-specific fields"));

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        form.add(cardPanel, gbc);

        categoryCombo.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                Category selected = (Category) e.getItem();
                cardLayout.show(cardPanel, selected.name());
            }
        });

        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");
        okButton.addActionListener(e -> onOk());
        cancelButton.addActionListener(e -> dispose());

        JPanel buttons = new JPanel();
        buttons.add(okButton);
        buttons.add(cancelButton);

        add(form, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(okButton); // Enter key activates OK from anywhere in the dialog
        pack();
        setLocationRelativeTo(getOwner());
    }

    private void addRow(JPanel panel, GridBagConstraints gbc, int row, String label, java.awt.Component field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(field, gbc);
    }

    private JPanel buildResistorFields() {
        JPanel p = new JPanel(new GridLayout(0, 2, 4, 4));
        p.add(new JLabel("Resistance (ohms):"));
        p.add(resistanceField);
        p.add(new JLabel("Tolerance (%):"));
        p.add(toleranceField);
        return p;
    }

    private JPanel buildIcFields() {
        JPanel p = new JPanel(new GridLayout(0, 2, 4, 4));
        p.add(new JLabel("Part number:"));
        p.add(partNumberField);
        p.add(new JLabel("Pin count:"));
        p.add(pinCountField);
        return p;
    }

    private JPanel buildRelayFields() {
        JPanel p = new JPanel(new GridLayout(0, 2, 4, 4));
        p.add(new JLabel("Coil voltage:"));
        p.add(coilVoltageField);
        p.add(new JLabel("Contact rating (A):"));
        p.add(contactRatingField);
        return p;
    }

    private void onOk() {
        try {
            int id = Integer.parseInt(idField.getText().trim());
            String name = nameField.getText().trim();
            int quantity = Integer.parseInt(quantityField.getText().trim());
            Category category = (Category) categoryCombo.getSelectedItem();

            result = switch (category) {
                case RESISTOR -> new Resistor(id, name, quantity,
                        Double.parseDouble(resistanceField.getText().trim()),
                        Double.parseDouble(toleranceField.getText().trim()));
                case INTEGRATED_CIRCUIT -> new IntegratedCircuit(id, name, quantity,
                        partNumberField.getText().trim(),
                        Integer.parseInt(pinCountField.getText().trim()));
                case RELAY -> new Relay(id, name, quantity,
                        Double.parseDouble(coilVoltageField.getText().trim()),
                        Double.parseDouble(contactRatingField.getText().trim()));
            };
            dispose();
        } catch (NumberFormatException e) {
            // Deliberately no dispose() here - the dialog stays open with
            // whatever the user already typed still in place, so they can
            // fix just the bad field instead of starting over.
            JOptionPane.showMessageDialog(this,
                    "Please check the numeric fields.", "Invalid input", JOptionPane.ERROR_MESSAGE);
        }
    }
}
