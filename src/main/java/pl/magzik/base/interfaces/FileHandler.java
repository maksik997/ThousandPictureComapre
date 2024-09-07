package pl.magzik.base.interfaces;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Interface defining file handling operations including loading, deleting,
 * and moving files.
 */
public interface FileHandler {

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
     * Moves the specified files
     *
     * @param files a list of files to be moved
     * @throws IOException if an I/O error occurs during file moving
     */
    void moveFiles(List<File> files) throws IOException;

}
