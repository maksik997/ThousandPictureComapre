package Modules;

import pl.magzik.PictureComparer;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

public class ComparerModule {
    private File destDir;

    private Collection<File> sources;

    private final PictureComparer pc;

    private final DefaultListModel<String> duplicateListModel, mappedListModel;

    private SwingWorker<Void, Void> mapObjects, transferObjects;

    public ComparerModule() {
        destDir = new File(System.getProperty("user.dir"));
        pc = new PictureComparer();

        duplicateListModel = new DefaultListModel<>();

        mappedListModel = new DefaultListModel<>();
    }

    public void reset(){
        sources = null;
        pc._reset();

        duplicateListModel.removeAllElements();
        mappedListModel.removeAllElements();
    }

    public void setUp() throws IOException {
        pc._setUp(destDir, sources);
    }

    // This method compares all images checksums
    public void compareAndExtract() {
        if(sources == null || destDir == null)
            throw new RuntimeException("Source directory and destination directory shouldn't be null");

        pc.map();
        pc.compare();
    }

    public void fileTransfer(){
        pc.move();
    }

    public void resetTasks(SwingWorker<Void, Void> mapObjects, SwingWorker<Void, Void> transferObjects) {
        this.mapObjects = mapObjects;
        this.transferObjects = transferObjects;
    }

    public File getDestDir() {
        return destDir;
    }

    public Collection<File> getSources() {
        return sources;
    }

    public PictureComparer getPc() {
        return pc;
    }

    public void setDestDir(File destDir) {
        this.destDir = destDir;
    }

    public void setSources(Collection<File> sources) {
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

    public DefaultListModel<String> getDuplicateListModel() {
        return duplicateListModel;
    }

    public DefaultListModel<String> getMappedListModel() {
        return mappedListModel;
    }

}
