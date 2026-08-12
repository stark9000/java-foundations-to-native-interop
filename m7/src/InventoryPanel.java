import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * The entire application's real content, deliberately kept separate
 * from MainFrame (the JFrame chrome around it) - the same reasoning as
 * Module 6's GaugeRenderer being separate from VuMeterPanel's Timer.
 * This separation means InventoryPanel could be dropped into any
 * container (a JFrame, a JApplet-equivalent, even a test harness) and
 * exercised without ever creating a real top-level window.
 */
public class InventoryPanel extends JPanel {

    private final Inventory inventory;
    private final AuditLog auditLog;

    private final InventoryTableModel tableModel;
    private final JTable table;
    private final JLabel statusLabel = new JLabel();
    private final JProgressBar progressBar = new JProgressBar();
    private final JButton removeButton = new JButton("Remove Selected");
    private final JButton startScanButton = new JButton("Start Scan");
    private final JButton cancelScanButton = new JButton("Cancel Scan");

    private ScanWorker activeScan;

    public InventoryPanel(Inventory inventory, AuditLog auditLog) {
        super(new BorderLayout(8, 8));
        this.inventory = inventory;
        this.auditLog = auditLog;

        tableModel = new InventoryTableModel(inventory.all());
        table = new JTable(tableModel);
        table.setAutoCreateRowSorter(true); // click any column header to sort by it

        // Only enabled when a row is actually selected - a small but
        // real bit of state-driven UI, using the selection model's own
        // listener rather than polling.
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                removeButton.setEnabled(table.getSelectedRow() != -1);
            }
        });
        removeButton.setEnabled(false);
        cancelScanButton.setEnabled(false);

        add(buildToolbar(), BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buildActivityPanel(), BorderLayout.EAST);
        add(buildStatusBar(), BorderLayout.SOUTH);

        refreshTable();
    }

    private JToolBar buildToolbar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);

        JButton addButton = new JButton("Add Component");
        addButton.addActionListener(e -> addComponent());
        removeButton.addActionListener(e -> removeSelected());
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refreshTable());

        toolBar.add(addButton);
        toolBar.add(removeButton);
        toolBar.add(refreshButton);
        return toolBar;
    }

    /**
     * VuMeterPanel is used here exactly as Module 6 left it - completely
     * unaware it's now living inside a bigger application. It doesn't
     * receive real scan data; it's purely decorative "activity" here,
     * driven by its own SignalGenerator. Module 9 could later wire a
     * real signal into a component like this without changing anything
     * about how it's embedded.
     */
    private JPanel buildActivityPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Activity"));
        panel.add(new VuMeterPanel(), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildStatusBar() {
        JPanel panel = new JPanel(new BorderLayout(8, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));

        panel.add(statusLabel, BorderLayout.WEST);
        panel.add(progressBar, BorderLayout.CENTER);

        startScanButton.addActionListener(e -> startScan());
        cancelScanButton.addActionListener(e -> cancelScan());
        JPanel scanButtons = new JPanel();
        scanButtons.add(startScanButton);
        scanButtons.add(cancelScanButton);
        panel.add(scanButtons, BorderLayout.EAST);

        return panel;
    }

    private void refreshTable() {
        tableModel.setRows(inventory.all());
        updateStatusLabel();
    }

    private void updateStatusLabel() {
        statusLabel.setText(inventory.summarize().toString());
    }

    // ---------- public actions, called by both toolbar buttons and MainFrame's menu ----------

    public void addComponent() {
        ComponentFormDialog.showDialog(SwingUtilities.getWindowAncestor(this))
                .ifPresent(component -> {
                    try {
                        inventory.add(component);
                        auditLog.log("ADD id=" + component.getId() + " name=\"" + component.getName() + "\"");
                        refreshTable();
                    } catch (DuplicateIdException e) {
                        JOptionPane.showMessageDialog(this, e.getMessage(), "Couldn't add", JOptionPane.ERROR_MESSAGE);
                    }
                });
    }

    public void removeSelected() {
        int viewRow = table.getSelectedRow();
        if (viewRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a row first.", "Nothing selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // Essential once a row sorter is attached: the row the user
        // clicked in the (possibly sorted) VIEW is not necessarily the
        // same index in the underlying MODEL. Skipping this conversion
        // is a classic Swing bug - it "removes the wrong row" the moment
        // someone clicks a column header to sort.
        int modelRow = table.convertRowIndexToModel(viewRow);
        Component toRemove = tableModel.getComponentAt(modelRow);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Remove \"" + toRemove.getName() + "\" (id=" + toRemove.getId() + ")?",
                "Confirm removal", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            inventory.removeById(toRemove.getId());
            auditLog.log("REMOVE id=" + toRemove.getId() + " name=\"" + toRemove.getName() + "\"");
            refreshTable();
        } catch (EntryNotFoundException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void saveToFile() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        Path path = chooser.getSelectedFile().toPath();
        try {
            InventoryFileStore.save(inventory, path);
            auditLog.log("SAVE file=" + path + " count=" + inventory.size());
            JOptionPane.showMessageDialog(this, "Saved " + inventory.size() + " component(s).",
                    "Saved", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Failed to save: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void loadFromFile() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;

        Path path = chooser.getSelectedFile().toPath();
        try {
            InventoryFileStore.load(inventory, path);
            auditLog.log("LOAD file=" + path + " count=" + inventory.size());
            refreshTable();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Failed to load: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        } catch (DuplicateIdException e) {
            JOptionPane.showMessageDialog(this, "File contains a duplicate id: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void showFullReport() {
        InventoryReport report = new InventoryReport(inventory);
        StringBuilder sb = new StringBuilder();

        report.countByCategory().forEach((category, count) ->
                sb.append(category.getLabel()).append(": ").append(count).append(" distinct component(s)\n"));
        sb.append("\n");
        report.totalQuantityByCategory().forEach((category, total) ->
                sb.append(category.getLabel()).append(": ").append(total).append(" units total\n"));
        sb.append("\n");
        report.averageQuantity().ifPresentOrElse(
                avg -> sb.append(String.format("Average quantity per component: %.1f%n", avg)),
                () -> sb.append("No components to average.\n"));

        JTextArea textArea = new JTextArea(sb.toString(), 12, 40);
        textArea.setEditable(false);
        JOptionPane.showMessageDialog(this, new JScrollPane(textArea), "Full Report", JOptionPane.PLAIN_MESSAGE);
    }

    public void startScan() {
        if (activeScan != null && !activeScan.isDone()) {
            JOptionPane.showMessageDialog(this, "A scan is already running.", "Scan", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if (inventory.size() == 0) {
            JOptionPane.showMessageDialog(this, "Nothing to scan.", "Scan", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        InventoryScanner scanner = new InventoryScanner(inventory);
        progressBar.setValue(0);
        startScanButton.setEnabled(false);
        cancelScanButton.setEnabled(true);

        activeScan = new ScanWorker(scanner,
                progress -> {
                    // Safe: process() runs on the EDT, guaranteed by SwingWorker.
                    progressBar.setMaximum(progress.total());
                    progressBar.setValue(progress.completed());
                    statusLabel.setText("Scanning: " + progress.justChecked().getName());
                },
                flagged -> {
                    auditLog.log("SCAN complete flagged=" + flagged.size());
                    if (flagged.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Scan complete - nothing flagged.",
                                "Scan complete", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        StringBuilder sb = new StringBuilder("Zero-quantity components:\n");
                        flagged.forEach(c -> sb.append("  ").append(c).append("\n"));
                        JOptionPane.showMessageDialog(this, sb.toString(),
                                "Scan complete", JOptionPane.WARNING_MESSAGE);
                    }
                },
                () -> {
                    startScanButton.setEnabled(true);
                    cancelScanButton.setEnabled(false);
                    updateStatusLabel();
                });
        activeScan.execute(); // schedules doInBackground() on a SwingWorker pool thread
    }

    public void cancelScan() {
        if (activeScan != null && !activeScan.isDone()) {
            // cancel(true) interrupts the worker thread if it's currently
            // running - which is exactly what makes InventoryScanner's
            // Thread.sleep() throw InterruptedException and unwind scan()
            // cleanly. No separate cancel-flag wiring is needed on this
            // side; SwingWorker's own cancellation mechanism is enough.
            activeScan.cancel(true);
        }
    }
}
