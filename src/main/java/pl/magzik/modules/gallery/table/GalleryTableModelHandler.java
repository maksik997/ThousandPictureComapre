package pl.magzik.modules.gallery.table;

import java.util.Collection;
import java.util.List;

/**
 * Interface representing a model for managing gallery entries in a table.
 * Provides methods for adding, removing, and manipulating gallery entries
 * as well as managing tags and refreshing the table view.
 */
public interface GalleryTableModelHandler {

    /**
     * Adds a collection of gallery entries to the table model.
     *
     * @param entries the collection of gallery entries to be added.
     *                 If an entry already exists in the model, it will not be added again.
     */
    void addEntries(Collection<GalleryEntry> entries);

    /**
     * Removes a collection of gallery entries identified by their row indices from the table model.
     *
     * @param rows a collection of row indices identifying the entries to be removed.
     *             The entries are removed in the order of their indices.
     * @return a list of gallery entries that were removed from the table model.
     */
    List<GalleryEntry> removeEntries(Collection<Integer> rows);

    /**
     * Adds a tag to a specific gallery entry.
     *
     * @param row the row index of the gallery entry to which the tag will be added.
     * @param tag the tag to be added to the gallery entry.
     */
    void addTag(int row, String tag);

    /**
     * Removes a tag from a specific gallery entry.
     *
     * @param row the row index of the gallery entry from which the tag will be removed.
     * @param tag the tag to be removed from the gallery entry.
     */
    void removeTag(int row, String tag);

    /**
     * Returns the index of a specific gallery entry in the table model.
     *
     * @param entry the gallery entry whose index is to be found.
     * @return the row index of the specified gallery entry, or -1 if the entry is not found.
     */
    int indexOf(GalleryEntry entry);

    /**
     * Retrieves a gallery entry at a specific row index.
     *
     * @param row the row index of the gallery entry to be retrieved.
     * @return the gallery entry located at the specified row index.
     * @throws IndexOutOfBoundsException if the row index is out of range.
     */
    GalleryEntry getEntry(int row);

    /**
     * Returns a list of all gallery entries currently in the table model.
     *
     * @return an unmodifiable list of all gallery entries in the table model.
     */
    List<GalleryEntry> getEntries();

    /**
     * Returns the number of rows in the table model.
     *
     * <p>This method returns the size of the list of gallery entries, which represents
     * the number of rows in the table. If the list is empty, it returns 0.</p>
     *
     * @return the number of rows in the table model.
     */
    int getRowCount();
}
