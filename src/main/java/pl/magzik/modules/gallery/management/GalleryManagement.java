package pl.magzik.modules.gallery.management;

import pl.magzik.modules.gallery.table.GalleryEntry;
import pl.magzik.modules.gallery.table.TablePropertyAccess;

import javax.swing.table.TableModel;
import java.io.File;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

/**
 * Defines an interface for managing a gallery of files.
 * Provides methods to add, remove, and retrieve files, as well as to manage tags associated thenLoad files.
 */
public interface GalleryManagement {

    /**
     * Adds a file to the gallery.
     *
     * @param file The file to be added.
     */
    default void addItem(File file) {
        addItems(List.of(file));
    }

    /**
     * Adds a file to the gallery using a {@link Path}.
     *
     * @param file The path of the file to be added.
     */
    default void addItem(Path file) {
        addItem(file.toFile());
    }

    /**
     * Adds a file to the gallery using a file path as a {@link String}.
     *
     * @param file The path of the file to be added.
     */
    default void addItem(String file) {
        addItem(new File(file));
    }

    /**
     * Adds a list of files to the gallery.
     *
     * @param files The list of files to be added.
     */
    void addItems(List<File> files);

    /**
     * Removes a file from the gallery based on the specified index.
     *
     * @param index The index of the file to be removed.
     * @return The removed file.
     */
    default File removeItem(int index) {
        return removeItems(List.of(index)).getFirst();
    }

    /**
     * Removes the specified file from the collection of items.
     * <p>
     * This method searches for the provided {@code File} object in the collection and removes
     * the first occurrence of it. If the file is not found in the collection, the collection remains unchanged.
     * </p>
     *
     * @param file The {@code File} object to be removed from the collection.
     * @throws NullPointerException if the specified file is {@code null}.
     */
    default void removeItem(File file) {
        removeElements(List.of(file));
    }

    /**
     * Removes multiple files from the gallery based on their indices.
     * The indices are sorted in descending order to avoid index shifting issues.
     *
     * @param indexes The list of indices of the files to be removed.
     * @return A list of removed files.
     */
    List<File> removeItems(Collection<Integer> indexes);

    /**
     * Removes multiple {@code File} objects from the collection.
     * <p>
     * This method iterates through the provided list of {@code File} objects and removes each file
     * from the collection. If a file is not found in the collection, it is ignored and the collection
     * remains unchanged for that file.
     * </p>
     *
     * @param files A list of {@code File} objects to be removed from the collection.
     *              This list can
     *              contain zero or more files.
     *              If the list is empty, the collection remains unchanged.
     * @throws NullPointerException if the provided list or any of its elements are {@code null}.
     */
    void removeElements(Collection<File> files);

    /**
     * Retrieves a file from the gallery based on the specified index.
     *
     * @param index The index of the file to retrieve.
     * @return The file at the specified index.
     */
    File getFile(int index);

    /**
     * Retrieves multiple files from the gallery based on their indices.
     *
     * @param indexes The list of indices of the files to retrieve.
     * @return A list of files at the specified indices.
     */
    default List<File> getFiles(Collection<Integer> indexes) {
        return indexes.stream()
            .map(this::getFile)
            .toList();
    }

    /**
     * Adds a tag to the file at the specified index.
     *
     * @param idx The index of the file to tag.
     * @param tagName The tag to be added.
     */
    void addTagTo(int idx, String tagName);

    /**
     * Adds a tag to all files at the specified indices.
     *
     * @param indexes The list of indices of the files to tag.
     * @param tagName The tag to be added.
     */
    default void addTagToAll(Collection<Integer> indexes, String tagName) {
        indexes.forEach(idx -> addTagTo(idx, tagName));
    }

    /**
     * Removes a tag from the file at the specified index.
     *
     * @param idx The index of the file to untag.
     * @param tagName The tag to be removed.
     */
    void removeTagFrom(int idx, String tagName);

    /**
     * Removes a tag from all files at the specified indices.
     *
     * @param indexes The list of indices of the files to untag.
     * @param tagName The tag to be removed.
     */
    default void removeTagFromAll(Collection<Integer> indexes, String tagName) {
        indexes.forEach(idx -> removeTagFrom(idx, tagName));
    }

    /**
     * Retrieves the list of tags associated thenLoad the file at the specified index.
     *
     * @param index The index of the file whose tags are to be retrieved.
     * @return A list of tags associated thenLoad the file.
     */
    List<String> getItemTags(int index);

    /**
     * Retrieves all tags present in the gallery.
     *
     * @return A list of all tags.
     */
    List<String> getAllTags();

    /**
     * Retrieves all entries present in gallery.
     * @return A list of gallery entries.
     * */
    List<GalleryEntry> getEntries();

    /**
     * Retrieves table model
     * @return TableModel
     * */
    TableModel getTableModel();

    TablePropertyAccess getTablePropertyAccess();
}
