package pl.magzik.modules.gallery;

import pl.magzik.async.AsyncTaskFactory;
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
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;

public class GalleryCoordinator implements AsyncTaskFactory {

    private final GalleryManagement gm;
    private final FileHandler fh;
    private final FileUtils fu;
    private final GalleryOperations go;
    private final GalleryPropertyAccess gpa;
    private final GalleryPackage gp;

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

    public Package getPackage() {
        return gp;
    }

    // Handle Tasks

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

    public void handleRemoveImages(Collection<Integer> indexes) {
        gm.removeItems(indexes);

        try {
            gp.saveGalleryItems();
        } catch (IOException e) {
            throw new CompletionException(e);
        }
    }

    public void handleRemoveFiles(Collection<File> files) throws IOException {
        gm.removeElements(files);
        gp.saveGalleryItems();
    }

    public void handleDeleteImages(Collection<Integer> integers) {
        List<File> files = gm.removeItems(integers);
        try {
            fh.deleteFiles(files);

            gp.saveGalleryItems();
        } catch (IOException e) {
            throw new CompletionException(e);
        }
    }

    public void handleOpen(Collection<Integer> indexes) throws IOException {
        for (Integer index : indexes) {
            File f = gm.getFile(index);
            go.openImage(f);
        }
    }

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

    public void handleAddTag(Collection<Integer> indexes, String tagName) throws IOException {
        gm.addTagToAll(indexes, tagName);
        gp.saveGalleryItems();
    }

    public void handleRemoveTag(Collection<Integer> indexes, String tagName) throws IOException {
        gm.removeTagFromAll(indexes, tagName);
        gp.saveGalleryItems();
    }

    // Delegated getters

    public List<String> getAllTags() {
        return gm.getAllTags();
    }

    public List<String> getItemTags(int index) {
        return gm.getItemTags(index);
    }

    public Set<String> getAllTagsInSelection(Collection<Integer> indexes) {
        return indexes.stream()
                        .map(gm::getItemTags)
                        .flatMap(List::stream)
                        .collect(Collectors.toSet());
    }

    public List<File> getFiles(Collection<Integer> indexes) {
        return gm.getFiles(indexes);
    }

    public int getRowCount() {
        return gm.getTablePropertyAccess().getRowCount();
    }

    public TableModel getTableModel() {
        return gm.getTableModel();
    }

    public void assignTableModel(JTable table) {
        table.setModel(gm.getTableModel());
    }

    public GalleryPropertyAccess getGalleryPropertyAccess() {
        return gpa;
    }

    public TablePropertyAccess getTablePropertyAccess() {
        return gm.getTablePropertyAccess();
    }
}
