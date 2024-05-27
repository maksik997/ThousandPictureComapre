package Modules;

import pl.magzik.PictureComparer;
import pl.magzik.Structures.Record;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;

public class ComparerModule {
    private File /*sourceDir,*/ destDir;

    //private List<File> sourceFiles;

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

//        if(sourceDir == null) {
//            pc._setUp(sourceFiles, destDir, Comparer.Modes.NOT_RECURSIVE);
//            return;
//        }
//        pc._setUp(sourceDir, destDir, pc.getMode());
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

    public void removeRedundant() throws IOException {
        AtomicBoolean ifDeletedAll = new AtomicBoolean(true);
        pc.getDuplicates().stream()
            .map(Record::getFile)
            .map(File::toPath)
            .map(p -> {
                try {
                    Files.deleteIfExists(p);
                } catch (IOException ignored) {
                    return false;
                }
                return true;
            }).filter(b -> !b)
            .findAny()
            .ifPresent(b -> ifDeletedAll.set(false));

        if (!ifDeletedAll.get())
            throw new IOException("Couldn't delete all files.");

//        for (File f : pc.getDuplicates().stream().map(Record::getFile).toList()) {
//            Files.deleteIfExists(f.toPath());
//        }
    }

    public void resetTasks(SwingWorker<Void, Void> mapObjects, SwingWorker<Void, Void> transferObjects) {
        this.mapObjects = mapObjects;
        this.transferObjects = transferObjects;
    }

//    public File getSourceDir() {
//        return sourceDir;
//    }

    public File getDestDir() {
        return destDir;
    }

    public Collection<File> getSources() {
        return sources;
    }

    public PictureComparer getPc() {
        return pc;
    }

//    public void setSourceDir(File sourceDir) {
//        this.sourceDir = sourceDir;
//    }

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

    public void setMapObjects(SwingWorker<Void, Void> mapObjects) {
        this.mapObjects = mapObjects;
    }

    public SwingWorker<Void, Void> getTransferObjects() {
        return transferObjects;
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
