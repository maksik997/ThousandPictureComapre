package pl.magzik.modules.gallery.table;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Defines an interface for managing a gallery of files.
 * Provides methods to add, remove, and retrieve files, as well as to manage tags associated thenLoad files.
 */
public interface GalleryManagementInterface {

    /**
     * Adds a file to the gallery.
     *
     * @param file The file to be added.
     */
    void addItem(File file);

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
    default void addItems(List<File> files) {
        files.forEach(this::addItem);
    }

    /**
     * Removes a file from the gallery based on the specified index.
     *
     * @param index The index of the file to be removed.
     * @return The removed file.
     */
    File removeItem(int index);


    File removeItem(File file);

    /**
     * Removes multiple files from the gallery based on their indices.
     * The indices are sorted in descending order to avoid index shifting issues.
     *
     * @param indexes The list of indices of the files to be removed.
     * @return A list of removed files.
     */
    default List<File> removeItems(List<Integer> indexes) {
        indexes = new ArrayList<>(indexes); // In case indexes is immutable
        indexes.sort(Integer::compare);
        indexes = indexes.reversed();

        return indexes.stream()
            .map(this::removeItem)
            .toList();
    }

    default List<File> removeItemsByFiles(List<File> files) {
        return files.stream().map(this::removeItem).toList();
    }

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
    default List<File> getFiles(List<Integer> indexes) {
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
    default void addTagToAll(List<Integer> indexes, String tagName) {
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
    default void removeTagFromAll(List<Integer> indexes, String tagName) {
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
}
