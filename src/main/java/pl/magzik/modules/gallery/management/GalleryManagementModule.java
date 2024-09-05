package pl.magzik.modules.gallery.management;

import pl.magzik.modules.base.Module;
import pl.magzik.modules.gallery.table.GalleryEntry;
import pl.magzik.modules.gallery.table.GalleryTableModel;
import pl.magzik.modules.gallery.table.TablePropertyAccess;

import java.io.File;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class GalleryManagementModule implements Module, GalleryManagement {

    private final GalleryTableModel galleryTableModel;

    public GalleryManagementModule() {
        galleryTableModel = new GalleryTableModel();
    }

    @Override
    public TablePropertyAccess getTablePropertyAccess() {
        return galleryTableModel;
    }

    @Override
    public GalleryTableModel getTableModel() {
        return galleryTableModel;
    }

    @Override
    public void addItems(List<File> files) {
        List<GalleryEntry> entries = files.stream()
                                    .map(File::toPath)
                                    .map(GalleryEntry::new)
                                    .toList();

        galleryTableModel.addEntries(entries);
    }

    @Override
    public List<File> removeItems(Collection<Integer> indexes) {
        return galleryTableModel.removeEntries(indexes).stream()
                                                        .map(GalleryEntry::getPath)
                                                        .map(Path::toFile)
                                                        .toList();
    }

    @Override
    public void removeElements(Collection<File> files) {
        List<Integer> indexes = files.stream()
                                        .map(File::toPath)
                                        .map(GalleryEntry::new)
                                        .map(galleryTableModel::indexOf)
                                        .toList();
        removeItems(indexes);
    }

    @Override
    public File getFile(int index) {
        return galleryTableModel.getEntry(index).getPath().toFile();
    }

    @Override
    public void addTagTo(int idx, String tagName) {
        galleryTableModel.addTag(idx, tagName);
    }

    @Override
    public void removeTagFrom(int idx, String tagName) {
        galleryTableModel.removeTag(idx, tagName);
    }

    @Override
    public List<String> getItemTags(int index) {
        return galleryTableModel.getEntry(index).getTags().stream().toList();
    }

    @Override
    public List<String> getAllTags() {
        return galleryTableModel.getEntries().stream()
                                            .map(GalleryEntry::getTags)
                                            .flatMap(Collection::stream)
                                            .distinct()
                                            .toList();
    }

    @Override
    public List<GalleryEntry> getEntries() {
        return Collections.unmodifiableList(galleryTableModel.getEntries());
    }
}
