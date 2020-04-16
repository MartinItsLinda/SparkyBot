package uk.sparkydiscordbot.api.entities.command.argument;

import uk.sparkydiscordbot.api.entities.command.Command;
import uk.sparkydiscordbot.api.exception.command.CommandSyntaxException;

/**
 * A class to determine whether a {@link Command} is expecting a potentially {@code null} value as its default, or to
 * throw a {@link CommandSyntaxException}
 */
public class Argument<T> {

    private final T value;
    private final boolean nullable;

    /**
     * @param value    The value
     * @param nullable Whether to accept {@code null} as a value
     */
    private Argument(final T value,
                     final boolean nullable) {
        this.value = value;
        this.nullable = nullable;
    }

    /**
     * An {@link Argument}
     *
     * @return An empty {@link Argument}
     */
    public static <T> Argument<T> empty() {
        return new Argument<>(null, false);
    }

    /**
     * Create a new {@link Argument} with the provided value
     *
     * <p>Should {@code value} be {@code null} it is assumed that the
     * {@link Command} is expecting a potentially {@code null} value</p>
     *
     * @param value The value
     *
     * @return A new {@link Argument}
     */
    public static <T> Argument<T> create(final T value) {
        return new Argument<>(value, (value == null));
    }

    /**
     * @return The (possibly null) value
     */
    public T getValue() {
        return this.value;
    }

    /**
     * @return {@code true} if this {@link Argument} is allowed to be {@code null}
     */
    public boolean isNullable() {
        return this.nullable;
    }

    @Override
    public String toString() {
        return "Argument{value=" + this.value + ", isNullable=" + this.isNullable() + "}";
    }
}
