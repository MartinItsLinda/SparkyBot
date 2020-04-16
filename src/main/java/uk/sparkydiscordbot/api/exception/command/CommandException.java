package uk.sparkydiscordbot.api.exception.command;

import org.jetbrains.annotations.NotNull;

public class CommandException extends RuntimeException {

    /**
     * @param message A short explanation of what caused this {@link CommandException} to be thrown
     */
    public CommandException(@NotNull(value = "message cannot be null") final String message) {
        super(message);
    }

    /**
     * @param message A short explanation of what caused this {@link CommandException} to be thrown
     * @param cause   The parent cause of this {@link CommandException}
     */
    public CommandException(@NotNull(value = "message cannot be null") final String message,
                            @NotNull(value = "cause cannot be null") final Throwable cause) {
        super(message, cause);
    }
}
