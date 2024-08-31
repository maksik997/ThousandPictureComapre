package pl.magzik.base.interfaces;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.function.Predicate;

/**
 * Represents a predicate (boolean-valued function) that can throw an {@link IOException}.
 * <p>
 * This functional interface extends the {@link Predicate} interface to handle checked exceptions,
 * specifically {@link IOException}. The {@link #test(Object)} method wraps any checked exception
 * in an {@link UncheckedIOException}, allowing it to be used in contexts where only unchecked exceptions are expected.
 * </p>
 * <p>
 * Implementing classes must provide the {@link #checkedTest(Object)} method, which contains
 * the logic that may throw an {@link IOException}. The {@link #test(Object)} method
 * automatically wraps this exception, ensuring the predicate can be used in environments
 * that expect a regular {@link Predicate}.
 * </p>
 *
 * @param <T> the type of the input to the predicate
 */
@FunctionalInterface
public interface CheckedPredicate <T> extends Predicate<T> {

    /**
     * Evaluates this predicate on the given argument, potentially throwing an {@link IOException}.
     *
     * @param t the input argument
     * @return {@code true} if the input argument matches the predicate, otherwise {@code false}
     * @throws IOException if an I/O error occurs
     */
    boolean checkedTest(T t) throws IOException;

    @Override
    default boolean test(T t) {
        try {
            return checkedTest(t);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
