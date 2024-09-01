package pl.magzik.modules.gallery.table;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * A table model that represents a collection of gallery entries for use in a JTable.
 * This model extends {@link AbstractTableModel} and implements {@link GalleryTableModelHandler}.
 * It provides methods for managing gallery entries, including adding, removing, and updating entries,
 * as well as managing tags associated with each entry.
 */
public class GalleryTableModel extends AbstractTableModel implements GalleryTableModelHandler, TablePropertyAccess {

    private final List<GalleryEntry> entries;

    private static final String[] columnNames = {
        "table.gallery.column.name",
        "table.gallery.column.size",
        "table.gallery.column.modification_date",
        "table.gallery.column.tags"
    };

    /**
     * Constructs a new {@code GalleryTableModel} with an empty list of gallery entries.
     */
    public GalleryTableModel() {
        this.entries = new ArrayList<>();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public void setColumnName(int column, String name) {
        columnNames[column] = name;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex >= entries.size()) {
            System.out.println("row: " + rowIndex + ", column: " + columnIndex + " at: " + entries.size());

            throw new IndexOutOfBoundsException("Row index out of bounds.");
        }

        GalleryEntry galleryEntry = entries.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> galleryEntry.getName();
            case 1 -> galleryEntry.getSize();
            case 2 -> galleryEntry.getModificationDate();
            case 3 -> String.join(", ", galleryEntry.getTags());
            default -> throw new IndexOutOfBoundsException("Column index out of bounds");
        };
    }

    @Override
    public void addEntries(Collection<GalleryEntry> entries) {
        if (entries.isEmpty()) return;

        int idx = this.entries.size();

        this.entries.addAll(
            entries.stream()
            .filter(image -> !this.entries.contains(image))
            .toList()
        );

        SwingUtilities.invokeLater(() -> fireTableRowsInserted(idx, this.entries.size() - 1));
    }

    @Override
    public List<GalleryEntry> removeEntries(Collection<Integer> rows) {
        List<GalleryEntry> entries = rows.stream()
                                    .map(this.entries::get)
                                    .toList();
        try {
            SwingUtilities.invokeAndWait(() -> {
                entries.forEach(this.entries::remove);
                fireTableDataChanged();
            });
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println(e.getCause() + ": " + e.getMessage()); // Todo, change for other more advance logging
        } catch (InvocationTargetException e) {
            System.err.println(e.getCause() + ": " + e.getMessage()); // Todo, change for other more advance logging
            throw new RuntimeException(e);
        }

        return entries;
    }

    @Override
    public void addTag(int row, String tag) {
        SwingUtilities.invokeLater(() -> entries.get(row).addTag(tag));
    }

    @Override
    public void removeTag(int row, String tag) {
        SwingUtilities.invokeLater(() -> entries.get(row).removeTag(tag));
    }

    @Override
    public void refresh() {
        SwingUtilities.invokeLater(this::fireTableStructureChanged);
    }

    @Override
    public int indexOf(GalleryEntry galleryEntry) {
        return entries.indexOf(galleryEntry);
    }

    @Override
    public GalleryEntry getEntry(int index) {
        return entries.get(index);
    }

    @Override
    public List<GalleryEntry> getEntries() {
        return Collections.unmodifiableList(entries);
    }

    @Override
    public int getRowCount() {
        return entries.size();
    }
}
