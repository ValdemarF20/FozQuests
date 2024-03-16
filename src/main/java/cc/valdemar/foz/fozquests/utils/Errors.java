package cc.valdemar.foz.fozquests.utils;

public class Errors {
    /**
     * Sneakily throw a checked exception as an unchecked exception
     *
     * @param e   the exception to throw
     * @param <E> the type of the exception
     * @throws E the exception
     */
    public static <E extends Throwable> void sneakyThrow(Throwable e) throws E {
        //noinspection unchecked
        throw (E) e;
    }

    /**
     * Sneakily throw a checked exception as an unchecked exception
     *
     * @param comment Comment to add to the exception
     * @throws Exception The exception
     */
    @SuppressWarnings("unchecked")
    public static <E extends Throwable> void sneakyThrow(String comment) throws E {
        throw (E) new Exception(comment);
    }
}
