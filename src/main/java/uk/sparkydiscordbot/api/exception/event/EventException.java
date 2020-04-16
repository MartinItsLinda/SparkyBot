package uk.sparkydiscordbot.api.exception.event;

import org.jetbrains.annotations.NotNull;

public class EventException extends Exception {

    /**
     * @param message A short explanation of what caused this {@link EventException} to be thrown
     */
    public EventException(@NotNull(value = "message cannot be null") final String message) {
        super(message);
    }

    /**
     * @param cause The parent cause of this {@link EventException}
     */
    public EventException(@NotNull(value = "cause cannot be null") final Throwable cause) {
        super(cause);
    }

    /**
     * @param message A short explanation of what caused this {@link EventException} to be thrown
     * @param cause   The parent cause of this {@link EventException}
     */
    public EventException(@NotNull(value = "message cannot be null") final String message,
                          @NotNull(value = "cause cannot be null") final Throwable cause) {
        super(message, cause);
    }
}
