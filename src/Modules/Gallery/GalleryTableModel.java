package Modules.Gallery;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class GalleryTableModel extends AbstractTableModel {

    private final List<Entry> images;

    private static final String[] columnNames = { "Name", "Size", "Modification date", "Tags" };

    public GalleryTableModel() {
        this.images = new ArrayList<>();
    }

    @Override
    public int getRowCount() {
        return images.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Entry entry = images.get(rowIndex);

        return switch (columnIndex) {
            case 0 -> entry.getName();
            case 1 -> entry.getSize();
            case 2 -> entry.getTags();
            case 3 -> entry.getModificationDate();
            default -> throw new IndexOutOfBoundsException();
        };
    }

    public void addEntry(Entry entry) {
        images.add(entry);
        fireTableRowsInserted(images.size() - 1, images.size() - 1);
    }

    public void removeEntry(Entry entry) {
        images.remove(entry);
        fireTableRowsDeleted(images.size() - 1, images.size() - 1);
    }


}
