package pl.magzik.modules.base;

import pl.magzik.base.interfaces.Command;

import java.util.function.Consumer;

/**
 * The {@code PackageInterface} provides a contract for loading modules in an application.
 * It defines methods for loading modules, handling post-loading actions, and retrieving the
 * number of loaded modules.
 * Implementations of this interface are responsible for managing the lifecycle of modules,
 * including their initialization and post-loading processes.
 */
public interface PackageInterface {

    /**
     * Loads the modules and applies the provided consumer to each module's name.
     * The stepUpCommand can be used to perform additional actions during the module loading process.
     *
     * @param nameUpdateConsumer A {@code Consumer} that processes the name of each module being loaded.
     * @param stepUpCommand A {@code Command} that can be executed at each step of the module loading process.
     * @throws ModuleLoadException If an error occurs while loading the modules.
     */
    void loadModules(Consumer<String> nameUpdateConsumer, Command stepUpCommand) throws ModuleLoadException;

    /**
     * Called after all modules have been successfully loaded. This method can be overridden
     * by implementations to perform additional actions after modules are loaded.
     *
     * @throws ModuleLoadException If an error occurs during the post-loading process.
     */
    default void onModulesLoaded() throws ModuleLoadException {}

}
