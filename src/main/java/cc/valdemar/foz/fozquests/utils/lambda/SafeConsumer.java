package cc.valdemar.foz.fozquests.utils.lambda;

import cc.valdemar.foz.fozquests.utils.Errors;

import java.util.function.Consumer;

/* Credit: https://github.com/knightzmc/mittenlib/tree/master/core/src/main/java/me/bristermitten/mittenlib/util/lambda */

/**
 * A {@link Consumer} that can throw a checked exception.
 *
 * @param <T> the type of the input
 */
@FunctionalInterface
public interface SafeConsumer<T> {
    /**
     * Consume the input, possibly throwing an exception.
     *
     * @param t the input
     */
    void consume(T t) throws Exception;

    /**
     * Turn this {@link SafeConsumer} into a {@link Consumer} that sneaky throws any exceptions.
     *
     * @return a {@link Consumer} that sneaky throws any exceptions
     */
    default Consumer<T> asConsumer() {
        return t -> {
            try {
                consume(t);
            } catch (Exception e) {
                Errors.sneakyThrow(e);
            }
        };
    }
}
