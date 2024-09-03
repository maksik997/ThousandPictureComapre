package pl.magzik.modules.base;

import java.io.IOException;

/**
 * Represents a module that can be loaded.
 * <p>
 * This functional interface defines a single method {@link #postConstruct()} which should be implemented
 * by classes representing modules that need to be loaded. The method {@link #postConstruct()} throws
 * {@link IOException} to handle any potential I/O errors during the loading process.
 * </p>
 */
public interface Module {

    /**
     * Performs post-construction initialization of the module.
     * <p>
     * This method is intended to execute any necessary operations after the
     * module has been constructed. It should handle potential I/O errors by
     * throwing an {@link IOException}.
     * </p>
     *
     * @throws IOException if an error occurs during the initialization process
     */
    default void postConstruct() throws Exception { }
}
