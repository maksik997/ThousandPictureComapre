package Modules;

import pl.magzik.IO.FileOperator;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class ComparerModule implements ComparerInterface {
    private File destination;

    private List<File> sources;

    private List<File> comparerOutput;

    private final DefaultListModel<String> duplicateListModel, mappedListModel;

    private final FileOperator fileOperator;

    private Mode mode;

    private boolean pHash, pixelByPixel;

    private SwingWorker<Void, Void> mapObjects, transferObjects;

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
    @Override
    public void compareAndExtract() throws IOException, ExecutionException {
        comparerOutput = getFromCache(compare(sources));
    }

    @Override
    public void fileTransfer() throws IOException {
        performMove(comparerOutput, destination);
        clearCache();
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

    @Override
    public boolean getPHash() {
        return pHash;
    }

    @Override
    public boolean getPixelByPixel() {
        return pixelByPixel;
    }

    @Override
    public void setPHash(boolean pHash) {
        this.pHash = pHash;
    }

    @Override
    public void setPixelByPixel(boolean pixelByPixel) {
        this.pixelByPixel = pixelByPixel;
    }

    @Override
    public void setMode(Mode mode) {
        this.mode = mode;
    }

    @Override
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
