package pl.magzik.modules.comparer;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Interface defining file handling operations including loading, deleting,
 * and moving files.
 */
public interface FileHandlerInterface {

    /**
     * Loads files based on the given list.
     *
     * @param input a list of input files to be processed
     * @return a list of loaded files
     * @throws IOException if an I/O error occurs during file loading
     */
    List<File> loadFiles(List<File> input) throws IOException;

    /**
     * Deletes the specified files.
     *
     * @param files a list of files to be deleted
     * @throws IOException if an I/O error occurs during file deletion
     */
    void deleteFiles(List<File> files) throws IOException;

    /**
     * Deletes the specified files.
     * This method is a convenience overload for deleting files by varargs.
     *
     * @param files files to be deleted
     * @throws IOException if an I/O error occurs during file deletion
     */
    default void deleteFiles(File... files) throws IOException {
        deleteFiles(Arrays.asList(files));
    }

    /**
     * Moves the specified files to the given destination directory.
     *
     * @param destination the target directory where files should be moved
     * @param files a list of files to be moved
     * @throws IOException if an I/O error occurs during file moving
     */
    void moveFiles(File destination, List<File> files) throws IOException;

    /**
     * Moves the specified files to the given destination directory.
     * This method is a convenience overload for moving files by varargs.
     *
     * @param destination the target directory where files should be moved
     * @param files files to be moved
     * @throws IOException if an I/O error occurs during file moving
     */
    default void moveFiles(File destination, File... files) throws IOException {
        moveFiles(destination, Arrays.asList(files));
    }
}
