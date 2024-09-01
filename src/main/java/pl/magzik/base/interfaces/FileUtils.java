package pl.magzik.base.interfaces;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.stream.IntStream;

/**
 * FormatUtils interface for file operations.
 *
 * <p>This interface provides methods for renaming files and obtaining file extensions.</p>
 */
public interface FileUtils {

    /**
     * Renames a file from {@code oldFile} to {@code newFile}.
     *
     * <p>If a file with the name {@code newFile} already exists, it will be overwritten.</p>
     *
     * @param oldFile the file to be renamed
     * @param newFile the new file name
     * @throws IOException if an I/O error occurs during renaming
     */
    void renameFile(File oldFile, File newFile) throws IOException;

    /**
     * Renames a list of files from {@code oldFiles} to {@code newFiles}.
     *
     * <p>The method assumes that both lists have the same size and that each file in
     * {@code oldFiles} corresponds to a file in {@code newFiles} at the same index.</p>
     *
     * <p>If an I/O error occurs during renaming, it will be wrapped in an {@link UncheckedIOException}.</p>
     *
     * @param oldFiles the list of files to be renamed
     * @param newFiles the list of new file names
     * @throws IndexOutOfBoundsException if the lists have different sizes
     */
    default void renameFiles(List<File> oldFiles, List<File> newFiles) {
        IntStream.range(0, oldFiles.size())
        .forEach(i -> {
            try {
                renameFile(oldFiles.get(i), newFiles.get(i));
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });
    }

    /**
     * Gets the extension of a file.
     *
     * <p>The extension is the substring after the last dot in the file name. If the file has no
     * extension, an empty string is returned.</p>
     *
     * @param file the file from which to extract the extension
     * @return the file extension, or an empty string if the file has no extension
     */
    static String getExtension(File file) {
        int index = file.toString().lastIndexOf('.');
        return index == -1 ? "" : file.toString().substring(index + 1);
    }
}
