package pl.magzik.modules.loader;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * Tracks and reports the progress of loading modules.
 * <p>
 * This class provides progress updates and the name of the currently loading module.
 * </p>
 */
public class ModuleLoaderProgress {
    private String currentModuleName;
    private double progress;
    private final double step;

    private final PropertyChangeSupport pcs;

    /**
     * Constructs a {@code ModuleLoaderProgress} instance with the specified number of modules.
     * @param moduleCount the total number of modules to be loaded
     */
    public ModuleLoaderProgress(int moduleCount) {
        this.currentModuleName = "";
        this.progress = 0;
        this.step = 1d / ((double) moduleCount);

        this.pcs = new PropertyChangeSupport(this);
    }

    /**
     * Updates the name of the currently loading module.
     * @param module the module that is currently being loaded
     */
    public void updateCurrentModule(Module module) {
        String oldName = currentModuleName;
        currentModuleName = module.getClass().getName();

        pcs.firePropertyChange("module", oldName, currentModuleName);
    }

    /**
     * Increments the progress and notifies listeners.
     */
    public void stepUp() {
        double oldProgress = progress;
        progress = Math.min(1, progress + step);

        pcs.firePropertyChange("progress", oldProgress, progress);
    }

    /**
     * Adds a listener for property change events.
     * @param listener the listener to add
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }
}
