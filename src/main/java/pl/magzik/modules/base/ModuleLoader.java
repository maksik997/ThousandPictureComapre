package pl.magzik.modules.base;

import java.beans.PropertyChangeListener;
import java.util.*;

/**
 * The {@code ModuleLoader} class manages the sequential loading of {@link Module} instances.
 * It supports adding modules and packages to a queue, tracking loading progress, and notifying
 * listeners of changes during the loading process.
 * <p>
 * This class provides a fluent API for configuring the loading sequence, initializing the loader,
 * and then executing the loading process step by step.
 * </p>
 *
 * @see Module
 * @see Package
 * @see ModuleLoaderProgress
 */
public class ModuleLoader {
    private final Queue<Package> packages;
    private boolean ready;

    private ModuleLoaderProgress mlp;

    /**
     * Private constructor to initialize a new {@code ModuleLoader} instance with a single {@link Package}.
     *
     * @param pkg the initial {@link Package} to be managed by this loader
     */
    private ModuleLoader(Package pkg) {
        this.packages = new LinkedList<>();
        this.packages.add(pkg);

        this.ready = false;
    }

    /**
     * Creates a {@code ModuleLoader} with a single {@link Module}.
     *
     * @param module the {@link Module} to be loaded
     * @return a new instance of {@code ModuleLoader}
     */
    public static ModuleLoader create(Module module) {
        return new ModuleLoader(new Package(Collections.singletonList(module)));
    }

    /**
     * Creates a {@code ModuleLoader} with a single {@link Package}.
     *
     * @param pkg the {@link Package} to be loaded
     * @return a new instance of {@code ModuleLoader}
     */
    public static ModuleLoader create(Package pkg) {
        return new ModuleLoader(pkg);
    }

    /**
     * Adds a {@link Module} to the loader.
     *
     * @param module the {@link Module} to be added
     * @return this {@code ModuleLoader} instance
     */
    public ModuleLoader thenLoad(Module module) {
        packages.offer(new Package(Collections.singletonList(module)));
        return this;
    }

    /**
     * Adds a {@link Package} to the loader.
     *
     * @param pkg the {@link Package} to be added
     * @return this {@code ModuleLoader} instance
     */
    public ModuleLoader thenLoad(Package pkg) {
        packages.offer(pkg);
        return this;
    }

    /**
     * Prepares the loader for use by initializing the progress tracker.
     * <p>
     * This method should be called after all modules or packages have been added to the loader
     * and before starting the loading process.
     * </p>
     *
     * @return this {@code ModuleLoader} instance
     * @throws IllegalStateException if the loader is already marked as ready
     */
    public ModuleLoader ready() {
        if (ready) throw new IllegalStateException("Loader is already ready");

        mlp = new ModuleLoaderProgress(packages.stream().mapToInt(Package::getModuleCount).sum());
        ready = true;

        return this;
    }

    /**
     * Checks if there are more modules to load.
     *
     * @return {@code true} if there are more modules, {@code false} otherwise
     * @throws IllegalStateException if the loader isn't marked as ready
     */
    public boolean hasNext() {
        if (!ready)
            throw new IllegalStateException("Loader isn't marked as ready");

        return !packages.isEmpty();
    }

    /**
     * Loads the next module in the sequence.
     * <p>
     * This method processes the next package in the queue, updating the progress
     * tracker accordingly.
     * </p>
     *
     * @throws ModuleLoadException if an error occurs while loading the module
     * @throws IllegalStateException if the loader isn't ready, or if there are no more modules to load
     */
    public void loadNext() throws ModuleLoadException {
        if (!ready)
            throw new IllegalStateException("Loader isn't marked as ready");

        if (!hasNext())
            throw new IllegalStateException("No more modules to loadFiles");

        Package pkg = packages.remove();
        pkg.loadModules(mlp::updateCurrentModule, mlp::stepUp);
    }

    /**
     * Adds a {@link PropertyChangeListener} to the {@link ModuleLoaderProgress}.
     * <p>
     * This listener will be notified of changes in the loading progress, such as
     * updates to the current module being loaded or the completion percentage.
     * </p>
     *
     * @param listener the listener to add
     * @throws IllegalStateException if the loader isn't marked as ready
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        if (!ready)
            throw new IllegalStateException("Loader isn't marked as ready");

        mlp.addPropertyChangeListener(listener);
    }
}
