package uk.sparkydiscordbot.api.event.command;

import org.jetbrains.annotations.NotNull;
import uk.sparkydiscordbot.api.entities.command.Command;
import uk.sparkydiscordbot.api.entities.command.CommandContext;
import uk.sparkydiscordbot.api.entities.event.Event;

/**
 * Called after a {@link Command} has finished an error-less execution
 */
public class CommandPostProcessEvent extends Event {

    private final CommandContext context;
    private final boolean success;

    public CommandPostProcessEvent(@NotNull(value = "context cannot be null") final CommandContext context,
                                   final boolean success) {
        this.context = context;
        this.success = success;
    }

    /**
     * @return The {@link CommandContext} executed
     */
    public CommandContext getContext() {
        return this.context;
    }

    /**
     * @return {@code true} if the {@link CommandContext} was successfully executed
     */
    public boolean isSuccess() {
        return this.success;
    }
}
