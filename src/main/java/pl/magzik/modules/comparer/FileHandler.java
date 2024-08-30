package pl.magzik.modules.comparer;

import pl.magzik.Comparator.FilePredicate;
import pl.magzik.Comparator.ImageFilePredicate;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

import static java.io.File.separator;

/**
 * The {@code FileHandler} interface provides methods for performing common file operations,
 * such as moving, deleting, and loading files. The interface includes default implementations
 * for these operations, which can be overridden by implementing classes.
 * <p>
 * The operations include:
 * <ul>
 *   <li>File transfer</li>
 *   <li>File deletion</li>
 *   <li>Moving files to a destination directory</li>
 *   <li>Deleting files</li>
 *   <li>Loading files (with support for handling interruptions and timeouts)</li>
 * </ul>
 * </p>
 */
@Deprecated
public interface FileHandler {

    /**
     * Predicate to determine whether a file should be processed, with a default implementation
     * that uses {@link ImageFilePredicate}.
     */
    FilePredicate PREDICATE = new ImageFilePredicate();

    /**
     * Transfer files. This method is not implemented by default and throws
     * {@link UnsupportedOperationException}.
     *
     * @throws IOException if an I/O error occurs.
     */
    default void fileTransfer() throws IOException {
        throw new UnsupportedOperationException("Unsupported operation.");
    }

    /**
     * Deletes files. This method is not implemented by default and throws
     * {@link UnsupportedOperationException}.
     *
     * @throws IOException if an I/O error occurs.
     */
    default void fileDelete() throws IOException {
        throw new UnsupportedOperationException("Unsupported operation.");
    }

    /**
     * Moves a list of files to the specified destination directory.
     * <p>
     * If an I/O error occurs during the moveFiles operation, an {@link UncheckedIOException} is thrown.
     * </p>
     *
     * @param files       the list of files to moveFiles.
     * @param destination the destination directory where the files will be moved.
     * @throws IOException if an I/O error occurs during the moveFiles operation.
     */
    default void performMove(List<File> files, File destination) throws IOException {
        perform(p -> {
            try {
                Files.move(
                    p,
                    Paths.get(destination + separator + p.getFileName()),
                    StandardCopyOption.REPLACE_EXISTING
                );
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }, files, destination);
    }

    /**
     * Deletes a list of files.
     * <p>
     * If an I/O error occurs during the deleteFiles operation, an {@link UncheckedIOException} is thrown.
     * </p>
     *
     * @param files       the list of files to deleteFiles.
     * @param destination the destination directory (unused in this operation).
     * @throws IOException if an I/O error occurs during the deleteFiles operation.
     */
    default void performDelete(List<File> files, File destination) throws IOException {
        perform(p -> {
            try {
                Files.delete(p);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }, files, destination);
    }

    /**
     * Performs a file operation defined by the given consumer on a list of files.
     * <p>
     * This method validates the input parameters, ensuring that neither the list of files
     * nor the destination directory is null, and then applies the given consumer to each file.
     * </p>
     *
     * @param cons        the file operation to perform, defined as a {@link Consumer} of {@link Path}.
     * @param files       the list of files to process.
     * @param destination the destination directory.
     * @throws IOException if an I/O error occurs during the operation.
     */
    private void perform(Consumer<Path> cons, List<File> files, File destination) throws IOException {
        Objects.requireNonNull(destination);
        Objects.requireNonNull(files);

        if (files.isEmpty()) return;

        try {
            files.parallelStream()
            .map(File::toPath)
            .forEach(cons);
        } catch (UncheckedIOException e) {
            throw e.getCause();
        }
    }

    /**
     * Loads' files. This method is not implemented by default and throws
     * {@link UnsupportedOperationException}.
     * <p>
     * The method signature includes exceptions for I/O errors, interruptions,
     * and timeouts, which should be handled by the implementing class.
     * </p>
     *
     * @throws IOException           if an I/O error occurs during the file loadFiles operation.
     * @throws InterruptedException  if the file loadFiles operation is interrupted.
     * @throws TimeoutException      if the file loadFiles operation times out.
     */
    default void fileLoad() throws IOException, InterruptedException, TimeoutException {
        throw new UnsupportedOperationException("Unsupported operation.");
    }
}
