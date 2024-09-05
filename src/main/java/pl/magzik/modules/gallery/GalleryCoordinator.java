package pl.magzik.modules.gallery;

import pl.magzik.base.async.AsyncTaskFactory;
import pl.magzik.base.interfaces.FileHandler;
import pl.magzik.base.interfaces.FileUtils;
import pl.magzik.modules.base.Package;
import pl.magzik.modules.gallery.management.GalleryManagement;
import pl.magzik.modules.gallery.management.GalleryManagementModule;
import pl.magzik.modules.gallery.operations.GalleryOperations;
import pl.magzik.modules.gallery.operations.GalleryOperationsModule;
import pl.magzik.modules.gallery.operations.GalleryPropertyAccess;
import pl.magzik.modules.gallery.persistence.GalleryFileModule;
import pl.magzik.modules.gallery.table.GalleryEntry;
import pl.magzik.modules.gallery.table.TablePropertyAccess;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;

/**
 * The {@code GalleryCoordinator} class manages gallery-related operations,
 * such as adding, removing, renaming, and tagging images.
 * It coordinates
 * between various modules including file handling, management, and operations.
 * This class implements the {@code AsyncTaskFactory} interface.
 */
public class GalleryCoordinator implements AsyncTaskFactory {

    private final GalleryManagement gm;
    private final FileHandler fh;
    private final FileUtils fu;
    private final GalleryOperations go;
    private final GalleryPropertyAccess gpa;
    private final GalleryPackage gp;

    /**
     * Constructs a {@code GalleryCoordinator} and initializes the necessary modules.
     */
    public GalleryCoordinator() {
        GalleryManagementModule gmm = new GalleryManagementModule();
        this.gm = gmm;
        GalleryFileModule gfm = new GalleryFileModule();
        this.fh = gfm;
        this.fu = gfm;
        GalleryOperationsModule gom = new GalleryOperationsModule();
        this.go = gom;
        this.gpa = gom;

        this.gp = new GalleryPackage(gmm, gfm, gom);
    }

    /**
     * Returns the gallery package containing all modules used by the coordinator.
     *
     * @return the gallery package
     */
    public Package getPackage() {
        return gp;
    }

    // Handle Tasks

    /**
     * Adds images to the gallery.
     *
     * @param input a collection of image file paths to add
     */
    public void handleAddImages(Collection<String> input) {
        List<File> files = input.stream()
                                .map(File::new)
                                .toList();

        try {
            files = fh.loadFiles(files);
            gm.addItems(files);
            gp.saveGalleryItems();
        } catch (IOException e) {
            throw new CompletionException(e);
        }
    }

    /**
     * Removes images from the gallery by their indexes.
     *
     * @param indexes the indexes of images to remove
     */
    public void handleRemoveImages(Collection<Integer> indexes) {
        gm.removeItems(indexes);

        try {
            gp.saveGalleryItems();
        } catch (IOException e) {
            throw new CompletionException(e);
        }
    }

    /**
     * Removes specified files from the gallery.
     *
     * @param files the files to remove
     * @throws IOException if an I/O error occurs
     */
    public void handleRemoveFiles(Collection<File> files) throws IOException {
        gm.removeElements(files);
        gp.saveGalleryItems();
    }

    /**
     * Deletes images from the gallery by their indexes and deletes the corresponding files.
     *
     * @param integers the indexes of images to delete
     */
    public void handleDeleteImages(Collection<Integer> integers) {
        List<File> files = gm.removeItems(integers);
        try {
            fh.deleteFiles(files);

            gp.saveGalleryItems();
        } catch (IOException e) {
            throw new CompletionException(e);
        }
    }

    /**
     * Opens images in the default image viewer.
     *
     * @param indexes the indexes of images to open
     * @throws IOException if an I/O error occurs
     */
    public void handleOpen(Collection<Integer> indexes) throws IOException {
        for (Integer index : indexes) {
            File f = gm.getFile(index);
            go.openImage(f);
        }
    }

    /**
     * Unifies the names of the images in the gallery by normalizing them.
     */
    public void handleUnifyNames() {
        List<File> oldFiles = gm.getEntries().stream()
                                                .map(GalleryEntry::getPath)
                                                .map(Path::toFile)
                                                .toList();

        gm.removeElements(oldFiles);
        List<File> newFiles = go.normalizeNames(oldFiles);
        try {
            fu.renameFiles(oldFiles, newFiles);
        } catch (IOException e) {
            throw new CompletionException(e);
        }

        gm.addItems(newFiles);

        try {
            gp.saveGalleryItems();
        } catch (IOException e) {
            throw new CompletionException(e);
        }
    }

    /**
     * Adds a tag to the specified images.
     *
     * @param indexes the indexes of images to tag
     * @param tagName the tag to add
     * @throws IOException if an I/O error occurs
     */
    public void handleAddTag(Collection<Integer> indexes, String tagName) throws IOException {
        gm.addTagToAll(indexes, tagName);
        gp.saveGalleryItems();
    }

    /**
     * Removes a tag from the specified images.
     *
     * @param indexes the indexes of images to untag
     * @param tagName the tag to remove
     * @throws IOException if an I/O error occurs
     */
    public void handleRemoveTag(Collection<Integer> indexes, String tagName) throws IOException {
        gm.removeTagFromAll(indexes, tagName);
        gp.saveGalleryItems();
    }

    // Delegated getters

    /**
     * Returns a list of all tags in the gallery.
     *
     * @return a list of all tags
     */
    public List<String> getAllTags() {
        return gm.getAllTags();
    }

    /**
     * Returns all tags that are associated with the selected gallery items.
     *
     * @param indexes the indexes of the selected gallery items
     * @return a set of all tags in the selection
     */
    public Set<String> getAllTagsInSelection(Collection<Integer> indexes) {
        return indexes.stream()
                        .map(gm::getItemTags)
                        .flatMap(List::stream)
                        .collect(Collectors.toSet());
    }

    /**
     * Returns a list of files associated with the specified gallery items.
     *
     * @param indexes the indexes of the gallery items
     * @return a list of files for the specified items
     */
    public List<File> getFiles(Collection<Integer> indexes) {
        return gm.getFiles(indexes);
    }

    /**
     * Returns the row count of the gallery table.
     *
     * @return the row count
     */
    public int getRowCount() {
        return gm.getTablePropertyAccess().getRowCount();
    }

    /**
     * Returns the table model for the gallery.
     *
     * @return the table model
     */
    public TableModel getTableModel() {
        return gm.getTableModel();
    }

    /**
     * Assigns the gallery's table model to the specified {@code JTable}.
     *
     * @param table the table to assign the model to
     */
    public void assignTableModel(JTable table) {
        table.setModel(gm.getTableModel());
    }

    /**
     * Returns the {@code GalleryPropertyAccess} instance used by this coordinator.
     *
     * @return the gallery property access instance
     */
    public GalleryPropertyAccess getGalleryPropertyAccess() {
        return gpa;
    }

    /**
     * Returns the {@code TablePropertyAccess} instance for the gallery table.
     *
     * @return the table property access instance
     */
    public TablePropertyAccess getTablePropertyAccess() {
        return gm.getTablePropertyAccess();
    }
}
