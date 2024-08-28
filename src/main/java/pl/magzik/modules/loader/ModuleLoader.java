package pl.magzik.modules.loader;

import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Manages the loading of a sequence of {@link Module} instances.
 * <p>
 * This class is responsible for iterating through a list of modules and invoking their
 * {@link Module#load()} method. It also tracks and reports progress through a
 * {@link ModuleLoaderProgress} instance.
 * </p>
 * <h3>Example usage: </h3>
 * <pre>{@code
 *      Module m1 = () -> System.out.println("Loading something...");
 *      Module m2 = () -> System.out.println("Loading something else...");
 *      ModuleLoader ml = ModuleLoader.create(m1)
 *                                    .with(m2)
 *                                    .ready();
 *      while (ml.hasNext()) ml.loadNext();
 *
 * }</pre>
 *
 * @see Module
 * @see ModuleLoaderProgress
 */
public class ModuleLoader {
    private final List<Module> modules;
    private boolean ready;

    private ModuleLoaderProgress mlp;
    private Iterator<Module> iterator;


    private ModuleLoader(List<Module> modules) {
        this.modules = modules;
        this.ready = false;
    }

    /**
     * Creates a {@code ModuleLoader} with a single module.
     * @param module the module to be loaded
     * @return a new instance of {@code ModuleLoader}
     */
    public static ModuleLoader create(Module module) {
        List<Module> modules = new LinkedList<>();
        modules.add(module);
        return new ModuleLoader(modules);
    }

    /**
     * Adds a module to the loader.
     * @param module the module to be added
     * @return this {@code ModuleLoader} instance
     */
    public ModuleLoader with(Module module) {
        modules.add(module);

        return this;
    }

    /**
     * Prepares the loader for use by initializing the progress tracker and iterator.
     * @return this {@code ModuleLoader} instance
     * @throws IllegalStateException if the loader is already marked as ready
     */
    public ModuleLoader ready() {
        if (ready) throw new IllegalStateException("Loader is already ready");

        mlp = new ModuleLoaderProgress(modules.size());
        iterator = modules.iterator();
        ready = true;

        return this;
    }

    /**
     * Checks if there are more modules to load.
     * @return true if there are more modules, false otherwise
     * @throws IllegalStateException if the loader isn't marked as ready
     */
    public boolean hasNext() {
        if (!ready)
            throw new IllegalStateException("Loader isn't marked as ready");

        return this.iterator.hasNext();
    }

    /**
     * Loads the next module in the sequence.
     * <p>
     * This method will throw an {@link IllegalStateException} if the loader isn't ready or if
     * there are no more modules to load.
     * </p>
     * @throws IOException if an error occurs while loading the module
     */
    public void loadNext() throws IOException {
        if (!ready)
            throw new IllegalStateException("Loader isn't marked as ready");

        if (!hasNext())
            throw new IllegalStateException("No more modules to load");

        Module module = this.iterator.next();

        mlp.updateCurrentModule(module);
        module.load();
        mlp.stepUp();
    }

    /**
     * Adds a listener for property change events.
     * @param listener the listener to add
     * @throws IllegalStateException if the loader isn't marked as ready
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        if (!ready)
            throw new IllegalStateException("Loader isn't marked as ready");

        mlp.addPropertyChangeListener(listener);
    }
}
