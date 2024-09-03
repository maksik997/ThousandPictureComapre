package pl.magzik.modules.base;

import pl.magzik.base.interfaces.Command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

/**
 * The {@code Package} class is an implementation of the {@link PackageInterface} that manages a collection of
 * {@link Module} instances. It provides mechanisms to load these modules, update their state, and retrieve the
 * number of modules loaded.
 * This class supports module initialization through the {@code loadModules} method, which executes a series
 * of operations on each module and allows for custom actions during and after module loading.
 */
public class Package implements PackageInterface {

    private final List<Module> modules;

    /**
     * Constructs an empty {@code Package} with no modules.
     */
    public Package() {
        modules = new ArrayList<>();
    }

    /**
     * Constructs a {@code Package} with a collection of modules.
     *
     * @param modules A collection of {@link Module} instances to be managed by this package.
     */
    public Package(Collection<Module> modules) {
        this.modules = new ArrayList<>(modules);
    }

    /**
     * Loads all modules in this package, applies the provided {@code Consumer} to update
     * the name of each module during loading, and executes a {@link Command} at each step.
     *
     * @param nameUpdateConsumer A {@code Consumer} that processes the name of each module being loaded.
     * @param stepUpCommand A {@code Command} that can be executed after each module is loaded.
     * @throws ModuleLoadException If any module fails to load correctly.
     */
    @Override
    public final void loadModules(Consumer<String> nameUpdateConsumer, Command stepUpCommand) throws ModuleLoadException {
        for (Module module : modules) {
            nameUpdateConsumer.accept(module.getClass().getSimpleName());
            try {
                module.postConstruct();
            } catch (Exception e) {
                throw new ModuleLoadException(e);
            } finally {
                stepUpCommand.execute();
            }
        }

        onModulesLoaded();
    }

    /**
     * Returns the total number of modules that this package manages.
     *
     * @return The number of modules in this package.
     */
    @Override
    public int getModuleCount() {
        return modules.size();
    }
}
