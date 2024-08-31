package pl.magzik.base.interfaces;

import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * An extension of the {@link Command} interface that allows for checked exceptions.
 * <p>
 * This interface is designed to handle commands that may throw an {@link IOException}.
 * It provides a default implementation of the {@link #execute()} method that catches
 * the checked exception and rethrows it as an {@link UncheckedIOException}.
 * </p>
 *
 * <h3>Usage Example:</h3>
 * <pre>{@code
 * CheckedCommand command = () -> {
 *     // some code that might throw IOException
 * };
 *
 * try {
 *     command.execute();
 * } catch (UncheckedIOException e) {
 *     // handle the exception
 * }
 * }</pre>
 *
 * @see Command
 * @see IOException
 * @see UncheckedIOException
 */
public interface CheckedCommand extends Command {

    /**
     * Executes the command, allowing for checked exceptions.
     *
     * @throws IOException if an I/O error occurs during execution.
     */
    void checkedExecute() throws IOException;

    @Override
    default void execute() {
        try {
            checkedExecute();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
