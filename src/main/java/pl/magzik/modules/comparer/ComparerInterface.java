package pl.magzik.modules.comparer;

import pl.magzik.Comparator.FilePredicate;
import pl.magzik.Comparator.ImageFilePredicate;
import pl.magzik.Structures.ImageRecord;
import pl.magzik.Structures.Record;
import pl.magzik.Utils.LoggingInterface;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

@Deprecated
public interface ComparerInterface {
    Function<File, ImageRecord> imageRecordFunction = file -> {
        try {
            return new ImageRecord(file);
        } catch (IOException e) {
            LoggingInterface.staticLog(String.format("Skipping file: %s", file.getName()));
            LoggingInterface.staticLog(e, String.format("Skipping file: %s", file.getName()));
        }
        return null;
    };
    FilePredicate filePredicate = new ImageFilePredicate();

    void setOutputPath(File destination);
    void setMode(Mode mode);
    void setPerceptualHash(boolean pHash);
    void setPixelByPixel(boolean pixelByPixel);

    boolean isPerceptualHash();
    boolean isPixelByPixel();

    void compareAndExtract() throws IOException, ExecutionException;
    default void fileTransfer() throws IOException {
        throw new UnsupportedOperationException("Unsupported operation.");
    }
    default void fileDelete() throws IOException {
        throw new UnsupportedOperationException("Unsupported operation.");
    }

    default List<File> compare(List<File> sources) throws IOException, ExecutionException {
        Objects.requireNonNull(sources);

        Map<?, List<Record<BufferedImage>>> map;

        if (isPerceptualHash() && isPixelByPixel())
            map = Record.process(sources, imageRecordFunction, ImageRecord.pHashFunction, ImageRecord.pixelByPixelFunction);
        else if (isPerceptualHash())
            map = Record.process(sources, imageRecordFunction, ImageRecord.pHashFunction);
        else if (isPixelByPixel())
            map = Record.process(sources, imageRecordFunction, ImageRecord.pixelByPixelFunction);
        else
            map = Record.process(sources, imageRecordFunction);

        return map.values().parallelStream()
                .filter(list -> list.size() > 1)
                .map(list -> list.subList(1, list.size()))
                .flatMap(Collection::stream)
                .map(Record::getFile)
                .toList();
    }

    default void performMove(List<File> files, File destination) throws IOException {
        Objects.requireNonNull(destination);
        Objects.requireNonNull(files);

        if (files.isEmpty()) return;

        String separator = File.separator;

        try {
            files.parallelStream().forEach(file -> {
                try {
                    Files.move(
                        file.toPath(),
                        Paths.get(destination + separator + file.getName()),
                        StandardCopyOption.REPLACE_EXISTING
                    );
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            });
        } catch (UncheckedIOException e) {
            throw e.getCause();
        }

    }

    default void performDelete(List<File> files, File destination) throws IOException {
        Objects.requireNonNull(destination);
        Objects.requireNonNull(files);

        if (files.isEmpty()) return;

        // Removes all the redundant images
        try {
            files.parallelStream().forEach(file -> {
                try {
                    Files.delete(file.toPath());
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            });
        } catch (UncheckedIOException e) {
            throw e.getCause();
        }
    }

    enum Mode {
        RECURSIVE, NON_RECURSIVE
    }
}
