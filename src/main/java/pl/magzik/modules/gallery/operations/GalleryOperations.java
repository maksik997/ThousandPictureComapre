package pl.magzik.modules.gallery.operations;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Interface defining operations for gallery management.
 * <p>
 * This interface provides methods for performing common operations
 * on gallery images, such as opening images and normalizing file names.
 * </p>
 */
public interface GalleryOperations {

    /**
     * Opens the specified image file using the system's default image viewer.
     *
     * @param file the image file to open
     * @throws IOException if an I/O error occurs when attempting to open the file
     *                     or if the default image viewer is not available
     */
    void openImage(File file) throws IOException;

    /**
     * Normalizes the names of the specified list of files.
     * <p>
     * This method renames each file in the list according to a predefined template,
     * which may include options like appending a timestamp or sequential index.
     * </p>
     *
     * @param files the list of files to normalize
     * @return a list of renamed files with the new, normalized names
     */
    List<File> normalizeNames(List<File> files);
}
