package pl.magzik.modules.base;

import pl.magzik.base.interfaces.CheckedCommand;

import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.*;

/**
 * Manages the loading of a sequence of {@link Module} instances.
 * <p>
 * This class is responsible for iterating through a stack of modules and invoking their
 * {@link Module#postConstruct()} method, or other specified {@link CheckedCommand} instances.
 * It tracks and reports progress through a {@link ModuleLoaderProgress} instance.
 * </p>
 * <h3>Example usage: </h3>
 * <pre>{@code
 *      Module m1 = () -> System.out.println("Loading something...");
 *      Module m2 = () -> System.out.println("Loading something else...");
 *      ModuleLoader ml = ModuleLoader.create(m1)
 *                                    .thenLoad(m2)
 *                                    .ready();
 *      while (ml.hasNext()) ml.loadNext();
 *
 * }</pre>
 *
 * @see Module
 * @see ModuleLoaderProgress
 */
public class ModuleLoader {
    private final List<CheckedCommand> commands;
    private final List<String> names;
    private boolean ready;

    private ModuleLoaderProgress mlp;

    /**
     * Private constructor to initialize a new {@code ModuleLoader} instance with a single command.
     *
     * @param command the command to be executed during module loading
     * @param moduleClass the class of the module being loaded, used for tracking purposes
     */
    private ModuleLoader(CheckedCommand command, Class<?> moduleClass) {
        this.commands = new LinkedList<>();
        this.commands.add(command);

        this.names = new LinkedList<>();
        this.names.add(moduleClass.getSimpleName());

        this.ready = false;
    }

    /**
     * Creates a {@code ModuleLoader} with a single module specified by a {@link CheckedCommand}.
     *
     * @param command the command representing the module's loading action
     * @param moduleClass the class of the module being loaded, used for tracking purposes
     * @return a new instance of {@code ModuleLoader}
     */
    public static ModuleLoader create(CheckedCommand command, Class<?> moduleClass) {
        return new ModuleLoader(command, moduleClass);
    }

    /**
     * Creates a {@code ModuleLoader} with a single module.
     *
     * @param module the module to be loaded
     * @return a new instance of {@code ModuleLoader}
     */
    public static ModuleLoader create(Module module) {
        return new ModuleLoader(module::postConstruct, module.getClass());
    }

    /**
     * Adds a module to the loader with a specified {@link CheckedCommand}.
     *
     * @param command the command representing the module's loading action
     * @param moduleClass the class of the module being loaded, used for tracking purposes
     * @return this {@code ModuleLoader} instance
     */
    public ModuleLoader thenLoad(CheckedCommand command, Class<?> moduleClass) {
        commands.add(command);
        names.add(moduleClass.getSimpleName());

        return this;
    }

    /**
     * Adds a module to the loader.
     *
     * @param module the module to be added
     * @return this {@code ModuleLoader} instance
     */
    public ModuleLoader thenLoad(Module module) {
        commands.add(module::postConstruct);
        names.add(module.getClass().getSimpleName());

        return this;
    }

    /**
     * Prepares the loader for use by initializing the progress tracker.
     * <p>
     * This method should be called after adding all modules to the loader
     * and before starting the loading process.
     * </p>
     *
     * @return this {@code ModuleLoader} instance
     * @throws IllegalStateException if the loader is already marked as ready
     */
    public ModuleLoader ready() {
        if (ready) throw new IllegalStateException("Loader is already ready");

        mlp = new ModuleLoaderProgress(commands.size());
        ready = true;

        return this;
    }

    /**
     * Checks if there are more modules to load.
     *
     * @return true if there are more modules, false otherwise
     * @throws IllegalStateException if the loader isn't marked as ready
     */
    public boolean hasNext() {
        if (!ready)
            throw new IllegalStateException("Loader isn't marked as ready");

        return !commands.isEmpty();
    }

    /**
     * Loads the next module in the sequence.
     * <p>
     * This method executes the next command in the queue, updating the progress
     * tracker accordingly.
     * </p>
     *
     * @throws IOException if an error occurs while loading the module
     * @throws IllegalStateException if the loader isn't ready, or if there are no more modules to load
     */
    public void loadNext() throws IOException {
        if (!ready)
            throw new IllegalStateException("Loader isn't marked as ready");

        if (!hasNext())
            throw new IllegalStateException("No more modules to loadFiles");

        CheckedCommand command = this.commands.removeFirst();
        String name = names.removeFirst();

        mlp.updateCurrentModule(name);
        command.execute();
        mlp.stepUp();
    }

    /**
     * Adds a listener for property change events to the {@link ModuleLoaderProgress}.
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
