import javax.swing.table.AbstractTableModel;
import java.util.List;

/**
 * Adapts Inventory's data to the shape JTable needs. JTable never
 * touches Component directly - it only ever calls getValueAt() /
 * getColumnCount() / getRowCount() on this model, which is what lets
 * the same table widget display any tabular data at all.
 */
public class InventoryTableModel extends AbstractTableModel {

    private static final String[] COLUMNS = {"ID", "Name", "Category", "Quantity", "Specs"};

    private List<Component> rows;

    public InventoryTableModel(List<Component> initialRows) {
        this.rows = initialRows;
    }

    public void setRows(List<Component> newRows) {
        this.rows = newRows;
        fireTableDataChanged(); // tells every JTable using this model to redraw from scratch
    }

    public Component getComponentAt(int rowIndex) {
        return rows.get(rowIndex);
    }

    @Override
    public int getRowCount() {
        return rows.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMNS.length;
    }

    @Override
    public String getColumnName(int column) {
        return COLUMNS[column];
    }

    /**
     * Returning Integer.class (not the default Object/String) for the
     * numeric columns is what lets JTable's automatic row sorter compare
     * values numerically instead of as text - without this, sorting by
     * quantity would put "10" before "9", because "1" < "9" as strings.
     */
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return switch (columnIndex) {
            case 0, 3 -> Integer.class;
            default -> String.class;
        };
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Component c = rows.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> c.getId();
            case 1 -> c.getName();
            case 2 -> c.getCategory().getLabel();
            case 3 -> c.getQuantity();
            case 4 -> c.specs();
            default -> "";
        };
    }
}
