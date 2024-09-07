package pl.magzik.modules.base;

/**
 * Exception thrown when a module fails to load.
 * <p>
 * The {@code ModuleLoadException} is a custom exception that acts as a wrapper for exceptions
 * encountered during the loading of a {@link Module}. It allows for the encapsulation of the original
 * exception (the cause) while providing additional context if needed.
 * </p>
 * <p>
 * This exception can be used to propagate errors encountered during the execution of
 * module-loading processes, ensuring that the underlying cause is preserved.
 * </p>
 *
 * @see Module
 * @see Package
 */
public class ModuleLoadException extends Exception {

    /**
     * Constructs a new {@code ModuleLoadException} with the specified cause.
     *
     * @param cause the underlying cause of the exception
     */
    public ModuleLoadException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new {@code ModuleLoadException} with the specified detail message and cause.
     *
     * @param message a detailed message explaining the reason for the exception
     * @param cause   the underlying cause of the exception
     */
    public ModuleLoadException(String message, Throwable cause) {
        super(message, cause);
    }
}
