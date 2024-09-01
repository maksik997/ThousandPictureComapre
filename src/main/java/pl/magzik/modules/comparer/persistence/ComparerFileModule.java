package pl.magzik.modules.comparer.persistence;

import pl.magzik.Comparator.FilePredicate;
import pl.magzik.Comparator.ImageFilePredicate;
import pl.magzik.IO.FileOperator;
import pl.magzik.base.interfaces.CheckedConsumer;
import pl.magzik.base.interfaces.FileHandler;
import pl.magzik.modules.base.Module;

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
 * file operations and requires {@link ComparerFilePropertyAccess} to determine
 * the configuration for file processing.
 * </p>
 */
public class ComparerFileModule implements Module, FileHandler, ComparerFilePropertyAccess {

    private Mode mode;
    private String outputPath;
    private final FilePredicate filePredicate;
    private final FileOperator fileOperator;

    /**
     * Constructs an instance of {@code ComparerFileModule}.
     * <p>
     * Initializes the output path to the user's home directory and sets the default mode to non-recursive.
     * </p>
     */
    public ComparerFileModule() {
        this.outputPath = System.getProperty("user.home");
        this.mode = Mode.NOT_RECURSIVE;

        this.filePredicate = new ImageFilePredicate();
        this.fileOperator = new FileOperator();
    }

    @Override
    public List<File> loadFiles(List<File> input) throws IOException {
        int depth = mode.isRecursive() ? Integer.MAX_VALUE : 1;

        try {
            return fileOperator.loadFiles(depth, filePredicate, input);
        } catch (InterruptedException | TimeoutException e) { // TODO: After PictureComparer update will disappear
            throw new IOException(e);
        }
    }

    @Override
    public void deleteFiles(List<File> files) throws IOException {
        perform(Files::delete, files);
    }

    @Override
    public void moveFiles(List<File> files) throws IOException {
        perform(p -> Files.move(
            p,
            Path.of(outputPath, p.getFileName().toString()),
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

    @Override
    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
    }

    @Override
    public void setMode(Mode mode) {
        this.mode = mode;
    }
}
