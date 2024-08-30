package pl.magzik.modules.comparer;

import pl.magzik.Comparator.FilePredicate;
import pl.magzik.Comparator.ImageFilePredicate;
import pl.magzik.IO.FileOperator;
import pl.magzik.modules.loader.Module;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeoutException;

/**
 * Implementation of a file handling module that supports operations such as
 * loading, deleting, and moving files in the context of file comparison.
 * <p>
 * This class uses {@link FilePredicate} and {@link FileOperator} to handle
 * file operations, and it requires {@link ComparerPropertyAccess} to determine
 * the configuration for file processing.
 */
public class ComparerFileModule implements Module, FileHandlerInterface {

    private final FilePredicate filePredicate;
    private final FileOperator fileOperator;
    private final ComparerPropertyAccess comparerPropertyAccess;

    /**
     * Constructs an instance of {@code ComparerFileModule}.
     *
     * @param comparerPropertyAccess an instance of {@link ComparerPropertyAccess}
     *        to configure file comparison settings
     */
    public ComparerFileModule(ComparerPropertyAccess comparerPropertyAccess) {
        this.filePredicate = new ImageFilePredicate();
        this.fileOperator = new FileOperator();
        this.comparerPropertyAccess = comparerPropertyAccess;
    }

    /**
     * Loads files based on the given list and comparison settings.
     *
     * @param input a list of input files to be processed
     * @return a list of files that were loaded
     * @throws IOException if an I/O error occurs during file loading
     */
    @Override
    public List<File> loadFiles(List<File> input) throws IOException {
        int depth = comparerPropertyAccess.getMode().isRecursive() ? Integer.MAX_VALUE : 1;

        try {
            return fileOperator.loadFiles(depth, filePredicate, input);
        } catch (InterruptedException | TimeoutException e) { // TODO: After PictureComparer update will disappear
            throw new IOException(e);
        }
    }

    /**
     * Deletes the specified files.
     *
     * @param files a list of files to be deleted
     * @throws IOException if an I/O error occurs during file deletion
     */
    @Override
    public void deleteFiles(List<File> files) throws IOException {
        perform(Files::delete, files);
    }

    /**
     * Moves the specified files to the given destination directory.
     *
     * @param destination the target directory where files should be moved
     * @param files a list of files to be moved
     * @throws IOException if an I/O error occurs during file moving
     */
    @Override
    public void moveFiles(File destination, List<File> files) throws IOException {
        perform(p -> Files.move(
            p,
            Path.of(destination.toString(), p.getFileName().toString()),
            StandardCopyOption.REPLACE_EXISTING
        ), files);
    }

    /**
     * Performs the specified file operation on a list of files.
     *
     * @param consumer a {@link CheckedConsumer} that defines the operation to be performed
     * @param files a list of files to which the operation will be applied
     * @throws IOException if an I/O error occurs during the operation
     */
    private void perform(CheckedConsumer<Path> consumer, List<File> files) throws IOException {
        Objects.requireNonNull(consumer);
        Objects.requireNonNull(files);

        if (files.isEmpty()) return;

        try {
            files.parallelStream()
                    .map(File::toPath)
                    .forEach(consumer);
        } catch (UncheckedIOException e) {
            throw e.getCause();
        }
    }
}
