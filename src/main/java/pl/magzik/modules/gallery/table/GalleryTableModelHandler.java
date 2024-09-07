package pl.magzik.modules.gallery.table;

import java.util.Collection;

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

}
