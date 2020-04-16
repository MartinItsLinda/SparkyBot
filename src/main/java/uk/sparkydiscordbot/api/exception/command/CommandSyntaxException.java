package uk.sparkydiscordbot.api.exception.command;

public class CommandSyntaxException extends RuntimeException {

    private final String command;
    private final String syntax;

    /**
     * @param command The command name
     * @param syntax  The correct syntax for the command
     */
    public CommandSyntaxException(final String command, final String syntax) {
        this.command = command;
        this.syntax = syntax;
    }

    /**
     * @return The command name
     */
    public String getCommand() {
        return this.command;
    }

    /**
     * @return The correct syntax for the command
     */
    public String getSyntax() {
        return this.syntax;
    }
}
