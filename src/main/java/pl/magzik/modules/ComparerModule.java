package pl.magzik.modules;

import pl.magzik.modules.comparer.ComparerProcessor;
import pl.magzik.modules.loader.Module;

import javax.swing.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class ComparerModule implements Module, ComparerProcessor {
    private String outputPath;

    private List<File> input;

    private List<File> output;

    private Mode mode;

    private boolean pHash, pixelByPixel, processing;

    private final DefaultListModel<String> duplicateListModel, mappedListModel;

    private final PropertyChangeSupport pcs;

    private final ReentrantLock lock;

    public ComparerModule() {
        this.outputPath = System.getProperty("user.home");
        this.input = new LinkedList<>();
        this.output = new LinkedList<>();
        this.mode = Mode.NOT_RECURSIVE;
        this.pHash = false;
        this.pixelByPixel = false;
        this.duplicateListModel = new DefaultListModel<>();
        this.mappedListModel = new DefaultListModel<>();
        this.pcs = new PropertyChangeSupport(this);
        this.lock = new ReentrantLock();
    }

    @Override
    public void lock() {
        lock.lock();
        try {
            firePropertyChange("comparer-processing", processing, (processing = true));
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void release(){
        lock.lock();
        try {
            input = new LinkedList<>();
            output = new LinkedList<>();
            duplicateListModel.removeAllElements();
            mappedListModel.removeAllElements();
            firePropertyChange("comparer-processing", processing, (processing = false));
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void handle(List<File> output) {
        this.output = output;
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    @Override
    public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        pcs.firePropertyChange(propertyName, oldValue, newValue);
    }

    @Override
    public String getOutputPath() {
        return outputPath;
    }

    @Override
    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
    }

    @Override
    public List<File> getInput() {
        return input;
    }

    @Override
    public void setInput(List<File> input) {
        Objects.requireNonNull(input);
        this.input = input;
    }

    public int getInputElementsCount() {
        return input.size();
    }

    @Override
    public List<File> getOutput() {
        return output;
    }

    public int getOutputElementsCount() {
        return output.size();
    }

    @Override
    public boolean isPerceptualHash() {
        return pHash;
    }

    @Override
    public boolean isPixelByPixel() {
        return pixelByPixel;
    }

    @Override
    public void setPerceptualHash(boolean pHash) {
        this.pHash = pHash;
    }

    @Override
    public void setPixelByPixel(boolean pixelByPixel) {
        this.pixelByPixel = pixelByPixel;
    }

    @Override
    public Mode getMode() {
        return mode;
    }

    @Override
    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public DefaultListModel<String> getDuplicateListModel() {
        return duplicateListModel;
    }

    public DefaultListModel<String> getMappedListModel() {
        return mappedListModel;
    }
}
