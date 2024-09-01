package pl.magzik.modules;

import pl.magzik.modules.gallery.Entry;
import pl.magzik.modules.gallery.GalleryPropertyAccess;
import pl.magzik.modules.gallery.GalleryTableModel;
import pl.magzik.modules.gallery.table.GalleryManagementInterface;
import pl.magzik.modules.loader.Module;

import java.io.*;
import java.nio.file.Path;
import java.util.*;
import java.util.List;

public class GalleryModule implements Module, GalleryManagementInterface, GalleryPropertyAccess {

    private final GalleryTableModel galleryTableModel;

    private boolean normalizedFileExtensions;

    private String normalizedNameTemplate;

    public GalleryModule() {
        galleryTableModel = new GalleryTableModel();
        normalizedFileExtensions = false;
        normalizedNameTemplate = "tp_img_";
    }

    @Override
    public void openImage(int idx) throws IOException {
        galleryTableModel.openEntry(idx);
    }

    @Override
    public void normalizeNames() throws IOException {
        galleryTableModel.unifyNames(normalizedNameTemplate, normalizedFileExtensions);
    }

    public GalleryTableModel getGalleryTableModel() {
        return galleryTableModel;
    }

    @Override
    public void addItems(List<File> files) {
        List<Entry> entries = files.stream()
                                    .map(File::toPath)
                                    .map(Entry::new)
                                    .toList();

        galleryTableModel.addEntries(entries);
    }

    @Override
    public List<File> removeItems(List<Integer> indexes) {
        return galleryTableModel.removeEntries(indexes).stream()
                                                        .map(Entry::getPath)
                                                        .map(Path::toFile)
                                                        .toList();
    }

    @Override
    public void removeItem(File file) {
        galleryTableModel.getImages().remove(new Entry(file.toPath()));
    }

    @Override
    public void removeElements(List<File> files) {
        List<Integer> indexes = files.stream()
                                        .map(File::toPath)
                                        .map(Entry::new)
                                        .map(galleryTableModel::indexOf)
                                        .toList();
        removeItems(indexes);
    }

    @Override
    public File getFile(int index) {
        return galleryTableModel.getImage(index).getPath().toFile();
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
        return galleryTableModel.getImage(index).getTags().stream().toList();
    }

    @Override
    public List<String> getAllTags() {
        return galleryTableModel.getImages().stream()
                                            .map(Entry::getTags)
                                            .flatMap(Collection::stream)
                                            .distinct()
                                            .toList();
    }

    @Override
    public String getNormalizedNameTemplate() {
        return normalizedNameTemplate;
    }

    @Override
    public void setNormalizedNameTemplate(String normalizedNameTemplate) {
        this.normalizedNameTemplate = normalizedNameTemplate;
    }

    @Override
    public boolean isNormalizedFileExtensions() {
        return normalizedFileExtensions;
    }

    @Override
    public void setNormalizedFileExtensions(boolean normalizedFileExtensions) {
        this.normalizedFileExtensions = normalizedFileExtensions;
    }
}
