package pl.magzik.modules.gallery.operations;

import pl.magzik.base.interfaces.FileUtils;
import pl.magzik.modules.base.Module;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Implementation of {@link GalleryOperations} that handles gallery-related operations.
 * <p>
 * This class provides functionality for normalizing file names and opening image files.
 * It includes options for setting and getting file name templates and controlling whether
 * file extensions should be normalized.
 * </p>
 */
public class GalleryOperationsModule implements Module, GalleryOperations, GalleryPropertyAccess {

    private boolean normalizedFileExtensions;

    private String normalizedNameTemplate;

    /**
     * Constructs a {@code GalleryOperationsModule} with default settings.
     * <p>
     * Initializes the file extension normalization flag to {@code false} and sets the
     * default name template to {@code "img_"}.
     * </p>
     */
    public GalleryOperationsModule() {
        normalizedFileExtensions = false;
        normalizedNameTemplate = "img_";
    }

    /**
     * Normalizes the names of the specified list of files.
     * <p>
     * This method renames each file in the list according to a predefined template, which
     * may include options such as appending a timestamp or sequential index. If the
     * {@link #isNormalizedFileExtensions()} flag is set to {@code true}, file extensions
     * will be converted to lowercase.
     * </p>
     *
     * @param files the list of files to normalize
     * @return a list of renamed files with the new, normalized names
     */
    @Override
    public List<File> normalizeNames(List<File> files) {
        long timestamp = System.currentTimeMillis();

        return IntStream.range(0, files.size())
                        .mapToObj(i -> renameFile(files.get(i), i, timestamp))
                        .toList();
    }

    private File renameFile(File file, int idx, long timestamp) {
        String ext = FileUtils.getExtension(file);
        if (normalizedFileExtensions) ext = ext.toLowerCase();
        String name = String.format("%s%d_%d.%s", normalizedNameTemplate, idx+1, timestamp, ext);

        return new File(file.getParent(), name);
    }

    /**
     * Opens the specified image file using the system's default image viewer.
     * <p>
     * This method utilizes the {@link Desktop#getDesktop()} method to open the file.
     * If the file cannot be opened, an {@link IOException} will be thrown.
     * </p>
     *
     * @param file the image file to open
     * @throws IOException if an I/O error occurs when attempting to open the file
     *                     or if the default image viewer is not available
     */
    @Override
    public void openImage(File file) throws IOException {
        Desktop.getDesktop().open(file);
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
