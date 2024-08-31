package pl.magzik.base.interfaces;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.function.Function;

/**
 * Represents a function that can throw an {@link IOException}.
 * <p>
 * This functional interface extends the {@link Function} interface to allow
 * handling checked exceptions, specifically {@link IOException}. The {@link #apply(Object)}
 * method wraps the checked exception in an {@link UncheckedIOException}, enabling
 * it to be used in contexts where only unchecked exceptions are expected.
 * </p>
 * <p>
 * Implementing classes must provide the {@link #checkedApply(Object)} method, which
 * contains the logic that may throw an {@link IOException}. The {@link #apply(Object)}
 * method automatically wraps this exception, ensuring the function can be used
 * in environments that expect a regular {@link Function}.
 * </p>
 *
 * @param <T> the type of the input to the function
 * @param <V> the type of the result of the function
 */
@FunctionalInterface
public interface CheckedFunction <T, V> extends Function<T, V> {

    /**
     * Applies this function to the given argument, potentially throwing an {@link IOException}.
     *
     * @param t the function argument
     * @return the function result
     * @throws IOException if an I/O error occurs
     */
    V checkedApply(T t) throws IOException;

    @Override
    default V apply(T t) {
        try {
            return checkedApply(t);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
