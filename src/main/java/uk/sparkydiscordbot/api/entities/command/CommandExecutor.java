package uk.sparkydiscordbot.api.entities.command;

public interface CommandExecutor {

    /**
     * @param context The {@link CommandContext} to handle
     * @param args    The {@link Arguments} passed with the {@link CommandContext}
     *
     * @return {@code true} if this {@link Command} was executed in the correct format, or {@code false} if an error
     * occurred during this {@link Command}'s execution or if it was executed improperly
     */
    boolean execute(final CommandContext context,
                    final Arguments args);

}
