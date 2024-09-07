package pl.magzik.base.interfaces;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * An interface for processing tasks that may throw I/O or execution exceptions.
 * <p>
 * This interface defines a single method, {@link #process()}, which must be implemented
 * by any class that performs a processing task. The method is expected to handle potential
 * I/O and execution errors during the processing.
 * </p>
 */
public interface Processor {

    /**
     * Executes the processing task.
     * <p>
     * Implementations of this method should provide the logic for performing the specific
     * processing task. This may include operations such as reading files, performing computations,
     * or handling data transformations.
     * </p>
     * <p>
     * The method is allowed to throw {@link IOException} if there are any I/O errors during the
     * processing, and {@link ExecutionException} if there are issues related to task execution.
     * </p>
     *
     * @throws IOException           If an I/O error occurs during processing.
     * @throws ExecutionException    If an error occurs during execution.
     */
    void process() throws IOException, ExecutionException;
}
