package Modules;

import pl.magzik.Comparator.FilePredicate;
import pl.magzik.Comparator.ImageFilePredicate;
import pl.magzik.IO.FileOperator;
import pl.magzik.Structures.ImageRecord;
import pl.magzik.Structures.Record;
import pl.magzik.Utils.LoggingInterface;

import javax.swing.*;
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

        duplicateListModel = new DefaultListModel<>();
        mappedListModel = new DefaultListModel<>();
    }

    public void reset(){
        sources = new LinkedList<>();
        comparerOutput = null;

        duplicateListModel.removeAllElements();
        mappedListModel.removeAllElements();
    }

    @Deprecated
    public void setUp() throws IOException {}

    // Load images
    public void load() throws IOException, InterruptedException, TimeoutException {
        Objects.requireNonNull(sources);
        sources = fileOperator.loadFiles(mode == Mode.RECURSIVE ? Integer.MAX_VALUE : 1, filePredicate, sources);
    }

    // This method compares all images checksums
    public void compareAndExtract() throws IOException, ExecutionException {
        Objects.requireNonNull(sources);

        // Record.process return duplicates (or uniques, entries with more than one element contain duplicates)
        comparerOutput = Record.process(sources, imageRecordFunction, ImageRecord.pHashFunction, ImageRecord.pixelByPixelFunction)
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

    @Deprecated
    public void resetTasks(SwingWorker<Void, Void> mapObjects, SwingWorker<Void, Void> transferObjects) {
        this.mapObjects = mapObjects;
        this.transferObjects = transferObjects;
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
