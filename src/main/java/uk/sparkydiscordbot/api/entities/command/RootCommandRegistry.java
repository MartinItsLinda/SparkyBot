package uk.sparkydiscordbot.api.entities.command;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.sparkydiscordbot.api.entities.module.Module;

import java.util.List;
import java.util.Map;

/**
 * The {@link RootCommandRegistry} is responsible for holding data
 * of all registered {@link Command}'s.
 */
public interface RootCommandRegistry {

    Logger LOGGER = LoggerFactory.getLogger(RootCommandRegistry.class);

    /**
     * @return This servers command map
     */
    Map<Module, List<Command>> getCommandMap();

    /**
     * Retrieve a {@link Command} by names (or alias)
     *
     * @param alias The names (or alias) of the {@link Command}
     *
     * @return The {@link Command}, or {@code null} if no command was found
     */
    Command getCommandByNameOrAlias(@NotNull(value = "alias cannot be null") final String alias);

    /**
     * Register a {@link Command} in this command map
     *
     * @param command The {@link Command} to register
     */
    default void registerCommand(@NotNull(value = "command cannot be null") final Command command) {
        //this is just a convenience method to save having to pull ", null" after every bot command
        this.registerCommand(command, null);
    }

    /**
     * Register a {@link Command} in this {@link RootCommandRegistry}
     *
     * @param command The {@link Command} to register
     * @param module  The owning {@link Module}, or {@code null} if this is a default command.
     */
    void registerCommand(@NotNull(value = "command cannot be null") final Command command,
                         final Module module);

    /**
     * Un-register a {@link Command} from this command map
     *
     * @param command The {@link Command} to un-register
     */
    void unregisterCommand(@NotNull(value = "command cannot be null") final Command command);

    /**
     * Un-register all {@link Command}'s associated with the given {@link Module}
     *
     * @param module The {@link Module} to un-register commands from.
     */
    void unregisterAll(@NotNull(value = "parent module cannot be null") final Module module);

    /**
     * Un-register all {@link Command}'s in this command map
     */
    void unregisterAll();
}
