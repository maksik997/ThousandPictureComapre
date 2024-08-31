package pl.magzik.modules.comparer.processing;

import pl.magzik.modules.loader.Module;

import javax.swing.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * The {@code ComparerModule} class is a part of the file comparison processing system.
 * It handles the management of input and output files, manages processing state, and provides methods
 * to configure comparison settings.
 * <p>
 * This class implements the {@link Module} and {@link ComparerProcessor} interfaces, allowing it to
 * integrate with other components and perform file comparison operations.
 * </p>
 */
public class ComparerModule implements Module, ComparerProcessor {

    private List<File> input;
    private List<File> output;
    private boolean pHash, pixelByPixel;
    private final PropertyChangeSupport pcs;
    private final ReentrantLock lock;
    private boolean processing;

    /**
     * Constructs a new {@code ComparerModule} instance with default values.
     */
    public ComparerModule() {
        this.input = new ArrayList<>();
        this.output = new ArrayList<>();
        this.pHash = false;
        this.pixelByPixel = false;
        this.pcs = new PropertyChangeSupport(this);
        this.lock = new ReentrantLock();
        this.processing = false;
    }

    /**
     * Acquires the lock for processing and sets the processing state to {@code true}.
     * This method should be called before starting a processing task to ensure thread safety.
     * <p>
     * This method uses {@link ReentrantLock} to manage concurrency.
     * </p>
     */
    @Override
    public void lock() {
        lock.lock();
        try {
            firePropertyChange("comparer-processing", processing, (processing = true));
        } finally {
            lock.unlock();
        }
    }

    /**
     * Releases the lock for processing and sets the processing state to {@code false}.
     * <p>
     * This method should be called after completing a processing task to release the lock and update the state.
     * </p>
     */
    @Override
    public void release(){
        lock.lock();
        try {
            input = new ArrayList<>();
            output = new ArrayList<>();
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
    public List<File> getInput() {
        return input;
    }

    @Override
    public void setInput(List<File> input) {
        Objects.requireNonNull(input);
        this.input = input;
    }

    /**
     * Returns the number of input files.
     *
     * @return the number of input files.
     */
    public int getInputElementsCount() {
        return input.size();
    }

    @Override
    public List<File> getOutput() {
        return output;
    }

    /**
     * Returns the number of output files.
     *
     * @return the number of output files.
     */
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
}
