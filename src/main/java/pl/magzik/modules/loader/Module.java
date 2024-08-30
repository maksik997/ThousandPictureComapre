package pl.magzik.modules.loader;

import java.io.IOException;

/**
 * Represents a module that can be loaded.
 * <p>
 * This functional interface defines a single method {@link #load()} which should be implemented
 * by classes representing modules that need to be loaded. The method {@link #load()} throws
 * {@link IOException} to handle any potential I/O errors during the loading process.
 * </p>
 */
public interface Module {
    
    /**
     * Loads the module.
     * <p>
     * This method performs the necessary operations to loadFiles the module. It is expected to handle any
     * potential I/O errors by throwing {@link IOException}.
     * </p>
     * @throws IOException if an error occurs during loading
     */
    default void load() throws IOException { }
}
