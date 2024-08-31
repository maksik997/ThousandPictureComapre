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
    public void addItem(File file) {
        Path p = file.toPath();
        galleryTableModel.addEntry(new Entry(p));
    }

    @Override
    public File removeItem(int index) {
        System.out.println(index);
        return galleryTableModel.removeEntry(index).getPath().toFile();
    }

    @Override
    public void removeItem(File file) {
        galleryTableModel.getImages().remove(new Entry(file.toPath()));
    }

    @Override
    public List<File> removeItems(List<Integer> indexes) { // TODO
        indexes = new ArrayList<>(indexes); // In case indexes is immutable
        indexes.sort(Integer::compare);
        indexes = indexes.reversed();

        return indexes.stream()
                .map(this::removeItem)
                .toList();
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
