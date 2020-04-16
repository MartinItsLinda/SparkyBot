package uk.sparkydiscordbot.api.entities.utils;

public class Objects {

    private Objects() {
    }

    /**
     * Checks that the given expression evaluates to {@code true}
     *
     * @param expression The expression to check
     *
     * @throws IllegalArgumentException If the expression returned {@code false}
     */
    public static void checkArgument(final boolean expression) throws IllegalArgumentException {
        Objects.checkArgument(expression, "");
    }

    /**
     * Checks that the given expression evaluates to {@code true}
     *
     * @param expression The expression to check
     * @param message    The exception message to use if the check fails
     *
     * @throws IllegalArgumentException If the expression returned {@code false}
     */
    public static void checkArgument(final boolean expression,
                                     final String message) throws IllegalArgumentException {
        if (!expression) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * @param state The expression to check
     *
     * @throws IllegalStateException If the expression returned {@code false}
     */
    public static void checkState(final boolean state) throws IllegalStateException {
        Objects.checkState(state, "");
    }

    /**
     * @param state The expression to check
     * @param message    The exception message to use if the check fails
     *
     * @throws IllegalStateException If the expression returned {@code false}
     */
    public static void checkState(final boolean state,
                                  final String message) throws IllegalStateException {
        if (!state) {
            throw new IllegalStateException(message);
        }
    }

    /**
     * Checks that the given reference is not null.
     *
     * @param reference The reference to check
     *
     * @return The reference if it was not {@code null}
     *
     * @throws NullPointerException If the reference was {@code null}
     */
    public static <T> T checkNotNull(final T reference) throws NullPointerException {
        return Objects.checkNotNull(reference, "");
    }

    /**
     * Checks that the given reference is not null.
     *
     * @param reference The reference to check
     * @param message   The exception message to use if the check fails
     *
     * @return The reference if it was not {@code null}
     *
     * @throws NullPointerException If the reference was {@code null}
     */
    public static <T> T checkNotNull(final T reference,
                                     final String message) throws NullPointerException {
        if (reference == null) {
            throw new NullPointerException(message);
        }
        return reference;
    }

    /**
     * Checks that the given {@link String} is not null or effectively null (just whitespace).
     *
     * @param reference The reference to check
     *
     * @throws NullPointerException If the reference was {@code null}
     */
    public static void checkNotBlank(final String reference) throws NullPointerException {
        Objects.checkNotBlank(reference, "");
    }

    /**
     * Checks that the given reference is not null or effectively null (just whitespace).
     *
     * @param reference The reference to check
     * @param message   The exception message to use if the check fails
     *
     * @throws NullPointerException If the reference was {@code null}
     */
    public static void checkNotBlank(final String reference,
                                     final String message) throws NullPointerException {
        if (reference == null || reference.trim().isEmpty()) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Checks that the given {@link Number}s integer value is not less than 0
     *
     * @param number The number to check
     */
    public static void checkNotNegative(final Number number) {
        Objects.checkNotNegative(number, "");
    }

    /**
     * Checks that the given {@link Number}s integer value is not less than 0
     *
     * @param number The number to check
     * @param message The exception message to use should the provided number be negative
     */
    public static void checkNotNegative(final Number number, final String message) {
        if (number.longValue() < 0) {
            throw new IllegalArgumentException(message);
        }
    }

}
