// TODO COMPARER INTERFACE ALLOWING MULTI-USE OF IT WITH LOCKS

package Modules;

import pl.magzik.Comparator.FilePredicate;
import pl.magzik.Comparator.ImageFilePredicate;
import pl.magzik.IO.FileOperator;
import pl.magzik.Structures.ImageRecord;
import pl.magzik.Structures.Record;
import pl.magzik.Utils.LoggingInterface;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

public class ComparerModule {
    private File destination;

    private List<File> sources;

    private List<File> comparerOutput;

    private final DefaultListModel<String> duplicateListModel, mappedListModel;

    private SwingWorker<Void, Void> mapObjects, transferObjects;

    private final FileOperator fileOperator;

    private Mode mode;

    private boolean pHash, pixelByPixel;

    private final static FilePredicate filePredicate = new ImageFilePredicate();

    public final static Function<File, ImageRecord> imageRecordFunction = file -> {
        try {
            return new ImageRecord(file);
        } catch (IOException e) {
            LoggingInterface.staticLog(String.format("Skipping file: %s", file.getName()));
            LoggingInterface.staticLog(e, String.format("Skipping file: %s", file.getName()));
        }
        return null;
    };

    public enum Mode {
        RECURSIVE, NON_RECURSIVE
    }

    public ComparerModule() {
        destination = new File(System.getProperty("user.dir"));
        sources = new LinkedList<>();
        comparerOutput = null;
        fileOperator = new FileOperator();
        mode = Mode.NON_RECURSIVE;

        pHash = false;
        pixelByPixel = false;

        duplicateListModel = new DefaultListModel<>();
        mappedListModel = new DefaultListModel<>();
    }

    public void reset(){
        sources = new LinkedList<>();
        comparerOutput = null;

        duplicateListModel.removeAllElements();
        mappedListModel.removeAllElements();
    }

    // Load images
    public void load() throws IOException, InterruptedException, TimeoutException {
        Objects.requireNonNull(sources);
        sources = fileOperator.loadFiles(mode == Mode.RECURSIVE ? Integer.MAX_VALUE : 1, filePredicate, sources);
    }

    // This method compares all images checksums
    public void compareAndExtract() throws IOException, ExecutionException {
        Objects.requireNonNull(sources);
        Map<?, List<Record<BufferedImage>>> map;

        if (pHash && pixelByPixel)
            map = Record.process(sources, imageRecordFunction, ImageRecord.pHashFunction, ImageRecord.pixelByPixelFunction);
        else if (pHash)
            map = Record.process(sources, imageRecordFunction, ImageRecord.pHashFunction);
        else if (pixelByPixel)
            map = Record.process(sources, imageRecordFunction, ImageRecord.pixelByPixelFunction);
        else
            map = Record.process(sources, imageRecordFunction);

        // Record.process return duplicates (or uniques, entries with more than one element contain duplicates)
        comparerOutput = map
                .values().stream()
                .filter(list -> list.size() > 1)
                .flatMap(Collection::stream)
                .map(Record::getFile)
                .toList();
    }

    public void fileTransfer() throws IOException {
        Objects.requireNonNull(destination);
        Objects.requireNonNull(comparerOutput);

        if (comparerOutput.isEmpty()) return;

        String separator = File.pathSeparator;

        try {
            comparerOutput.parallelStream().forEach(file -> {
                try {
                    Files.move(file.toPath(), Paths.get(destination + separator + file.getName()), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            });
        } catch (UncheckedIOException e) {
            throw e.getCause();
        }
    }

    public File getDestination() {
        return destination;
    }

    public List<File> getSources() {
        return sources;
    }

    public int getSourcesSize() {
        return sources.size();
    }

    public List<File> getComparerOutput() {
        Objects.requireNonNull(comparerOutput);
        return comparerOutput;
    }

    public int getComparerOutputSize() {
        Objects.requireNonNull(comparerOutput);
        return comparerOutput.size();
    }

    public Mode getMode() {
        return mode;
    }

    public boolean getPHash() {
        return pHash;
    }

    public boolean getPixelByPixel() {
        return pixelByPixel;
    }

    public void setPHash(boolean pHash) {
        this.pHash = pHash;
    }

    public void setPixelByPixel(boolean pixelByPixel) {
        this.pixelByPixel = pixelByPixel;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public void setDestination(File destination) {
        this.destination = destination;
    }

    public void setSources(List<File> sources) {
        this.sources = sources;
    }

    public void setSources(File... sources) {
        setSources(Arrays.asList(sources));
    }

    public SwingWorker<Void, Void> getMapObjects() {
        return mapObjects;
    }

    public SwingWorker<Void, Void> getTransferObjects() {
        return transferObjects;
    }

    public void setMapObjects(SwingWorker<Void, Void> mapObjects) {
        this.mapObjects = mapObjects;
    }

    public void setTransferObjects(SwingWorker<Void, Void> transferObjects) {
        this.transferObjects = transferObjects;
    }

    public DefaultListModel<String> getDuplicateListModel() {
        return duplicateListModel;
    }

    public DefaultListModel<String> getMappedListModel() {
        return mappedListModel;
    }
}
