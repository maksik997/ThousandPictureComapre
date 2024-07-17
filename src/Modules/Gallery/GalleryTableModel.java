package Modules.Gallery;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class GalleryTableModel extends AbstractTableModel {

    private final List<Entry> images;

    private static final String[] columnNames = { "LOC_GALLERY_TABLE_MODEL_COLUMN_NAME", "LOC_GALLERY_TABLE_MODEL_COLUMN_SIZE", "LOC_GALLERY_TABLE_MODEL_COLUMN_MODIFICATION_DATE", "LOC_GALLERY_TABLE_MODEL_COLUMN_TAGS" };

    public GalleryTableModel() {
        this.images = new ArrayList<>();
    }

    public List<Entry> getImages() {
        return images;
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
    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    public void setColumnName(int column, String name) {
        columnNames[column] = name;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Entry entry = images.get(rowIndex);

        return switch (columnIndex) {
            case 0 -> entry.getName();
            case 1 -> entry.getSize();
            case 2 -> entry.getModificationDate();
            case 3 -> String.join(", ", entry.getTags());
            default -> throw new IndexOutOfBoundsException();
        };
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        try {
            modifyName(rowIndex, (String) aValue);
        } catch (IOException e) {
            // todo for now!
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        fireTableCellUpdated(rowIndex, columnIndex);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 0;
    }

    public void addEntry(Entry entry) {
        // This method will add entry to show in table and to easily manage it.
        if (images.contains(entry)) return;

        images.add(entry);
        fireTableRowsInserted(images.size() - 1, images.size() - 1);
    }

    public void addAllEntries(Entry... entries) {
        addAllEntries(Arrays.asList(entries));
    }

    public void addAllEntries(Collection<Entry> entries) {
        if (entries.isEmpty()) return;

        int idx = images.size();

        images.addAll(
            entries.stream()
            .filter(image -> !images.contains(image))
            .toList()
        );

        fireTableRowsInserted(idx, images.size() - 1);
    }

    public void removeEntry(int row) {
        // This method will remove entry from table. But won't delete it from disk.
        images.remove(row);
        fireTableRowsDeleted(images.size() - 1, images.size() - 1);
    }

    public void deleteImage(int row) throws IOException {
        // This method will remove entry from table and will delete image from disk.
        Entry entry = images.get(row);
        removeEntry(row);

        Path path = entry.getPath();
        Files.delete(path);
    }

    public void openEntry(int row) throws IOException {
        File file = images.get(row).getPath().toFile();
        Desktop.getDesktop().open(file);
    }

    public void modifyName(int row, String newName) throws IOException {
        Path oldPath = images.get(row).getPath();

        Path parent = oldPath.getParent();
        Path newPath = new File(parent.toFile(), newName).toPath();

        if (newPath.equals(oldPath)) return;

        Files.move(oldPath, newPath, StandardCopyOption.ATOMIC_MOVE);

        images.set(row, new Entry(newPath));

        fireTableRowsUpdated(row, row);
    }

    public void addTag(int row, String tag) {
        images.get(row).addTag(tag);
    }

    public void removeTag(int row, String tag) {
        images.get(row).removeTag(tag);
    }

    // Group functions
    public void unifyNames(String pattern, boolean lowercaseSuffix) throws IOException {
        int i = 0;

        for (Path path : images.stream().map(Entry::getPath).toList()) {
            String ext = path.toString().substring(path.toString().lastIndexOf("."));
            int idx = images.stream().map(Entry::getPath).toList().indexOf(path);

            if (lowercaseSuffix) ext = ext.toLowerCase();

            modifyName(idx, String.format("%s%s_%s%s", pattern, ++i, System.currentTimeMillis(), ext));
        }
    }

    public void reduction(List<Path> paths) {
        images.removeAll(paths.stream().map(p -> {
            try {
                return new Entry(p);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).toList());

        fireTableDataChanged();
    }

    public void refresh() {
        fireTableStructureChanged();
    }
}
