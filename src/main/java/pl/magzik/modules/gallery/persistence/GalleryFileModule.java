package pl.magzik.modules.gallery.persistence;

import pl.magzik.predicates.FilePredicate;
import pl.magzik.predicates.ImageFilePredicate;
import pl.magzik.base.interfaces.CheckedConsumer;
import pl.magzik.base.interfaces.CheckedPredicate;
import pl.magzik.base.interfaces.FileHandler;
import pl.magzik.base.interfaces.FileUtils;
import pl.magzik.modules.base.Module;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * The {@code GalleryFileModule} class provides functionality for loading, filtering,
 * and deleting files in a gallery. It implements the {@link Module} and {@link FileHandler}
 * interfaces, allowing it to be used as a part of a larger module system.
 *
 * <p>This module uses a {@link FilePredicate} to filter files, specifically targeting image files
 * through the {@link ImageFilePredicate} implementation.</p>
 */
public class GalleryFileModule implements Module, FileHandler, FileUtils {

    private final FilePredicate filePredicate;

    /**
     * Constructs a new {@code GalleryFileModule} thenLoad a default {@link ImageFilePredicate}
     * for filtering image files.
     */
    public GalleryFileModule() {
        this.filePredicate = new ImageFilePredicate();
    }

    /**
     * Loads and filters files from the provided list. This method processes directories
     * recursively and filters files based on the configured {@link FilePredicate}.
     *
     * @param input the list of files and directories to load and filter
     * @return a list of files that pass the filtering criteria
     * @throws IOException if an I/O error occurs while processing the files
     */
    @Override
    public List<File> loadFiles(List<File> input) throws IOException {
        try {
            return input.stream()
                .filter(File::exists)
                .flatMap(this::listFilesAsStream)
                .filter((CheckedPredicate<File>) filePredicate::test)
                .toList();
        } catch (UncheckedIOException e) {
            throw e.getCause(); // TODO: Exchange thenLoad logging service
        }
    }

    /**
     * Recursively lists all files within a directory or returns the file itself if it is not a directory.
     *
     * @param file the file or directory to process
     * @return a {@link Stream} of files found within the directory or a single file if it is not a directory
     */
    private Stream<File> listFilesAsStream(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();

            if (files == null) return Stream.empty();
            return Arrays.stream(files)
                .flatMap(this::listFilesAsStream);
        }

        return Stream.of(file);
    }

    /**
     * Deletes the specified list of files. Each file is deleted using the {@link Files#delete(Path)} method.
     *
     * @param files the list of files to be deleted
     * @throws IOException if an I/O error occurs during file deletion
     */
    @Override
    public void deleteFiles(List<File> files) throws IOException {
        try {
            files.stream()
                .map(File::toPath)
                .forEach((CheckedConsumer<Path>) Files::delete);
        } catch (UncheckedIOException e) {
            throw e.getCause(); // TODO: Exchange thenLoad logging service
        }
    }

    /**
     * Throws an {@link UnsupportedOperationException} since file moving is not supported by this module.
     *
     * @param files the list of files to be moved
     * @throws UnsupportedOperationException always, as this operation is not supported
     */
    @Override
    public void moveFiles(List<File> files) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public void renameFile(File oldFile, File newFile) throws IOException {
        Files.move(oldFile.toPath(), newFile.toPath(), StandardCopyOption.ATOMIC_MOVE);
    }
}

