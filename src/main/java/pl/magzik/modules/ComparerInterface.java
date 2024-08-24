package pl.magzik.modules;

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
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;

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
    List<List<File>> cache = new LinkedList<>();
    Lock lock = new ReentrantLock();
    FilePredicate filePredicate = new ImageFilePredicate();

    void setDestination(File destination);
    void setMode(Mode mode);
    void setPHash(boolean pHash);
    boolean getPHash();
    void setPixelByPixel(boolean pixelByPixel);
    boolean getPixelByPixel();
    void compareAndExtract() throws IOException, ExecutionException;
    default void fileTransfer() throws IOException {
        throw new UnsupportedOperationException("Unsupported operation.");
    }
    default void fileDelete() throws IOException {
        throw new UnsupportedOperationException("Unsupported operation.");
    }

    default int compare(List<File> sources) throws IOException, ExecutionException {
        Objects.requireNonNull(sources);

        Map<?, List<Record<BufferedImage>>> map;

        if (getPHash() && getPixelByPixel())
            map = Record.process(sources, imageRecordFunction, ImageRecord.pHashFunction, ImageRecord.pixelByPixelFunction);
        else if (getPHash())
            map = Record.process(sources, imageRecordFunction, ImageRecord.pHashFunction);
        else if (getPixelByPixel())
            map = Record.process(sources, imageRecordFunction, ImageRecord.pixelByPixelFunction);
        else
            map = Record.process(sources, imageRecordFunction);

        int id;

        lock.lock();
        try {
            id = cache.size();
            cache.add(
                map.values().parallelStream()
                .filter(list -> list.size() > 1)
                .map(list -> list.subList(1, list.size()))
                .flatMap(Collection::stream)
                .map(Record::getFile)
                .toList()
            );
        } finally {
          lock.unlock();
        }

        return id;
    }

    default List<File> getFromCache(int id) {
        return cache.get(id);
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

    default void clearCache() {
        lock.lock();
        try {
            cache.clear();
        } finally {
            lock.unlock();
        }
    }

    enum Mode {
        RECURSIVE, NON_RECURSIVE
    }
}
