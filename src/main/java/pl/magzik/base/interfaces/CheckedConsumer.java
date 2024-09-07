package pl.magzik.base.interfaces;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.function.Consumer;

/**
 * Functional interface for a consumer that handles checked exceptions.
 * Allows operations that can throw {@link IOException} to be used in lambda expressions.
 *
 * @param <T> the type of the input to the operation
 */
@FunctionalInterface
public interface CheckedConsumer <T> extends Consumer<T> {

    /**
     * Performs an operation that can throw {@link IOException}.
     *
     * @param t the input argument
     * @throws IOException if an I/O error occurs
     */
    void checkedAccept(T t) throws IOException;

    @Override
    default void accept(T t) {
        try {
            checkedAccept(t);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
