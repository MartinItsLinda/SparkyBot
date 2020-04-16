package uk.sparkydiscordbot.api.entities.module;

import org.jetbrains.annotations.NotNull;
import uk.sparkydiscordbot.api.entities.command.Command;
import uk.sparkydiscordbot.api.entities.command.RootCommandRegistry;

import java.util.Locale;
import java.util.Optional;

public final class CommandRegistry {

    private final RootCommandRegistry rootCommandRegistry;
    private final Module module;

    CommandRegistry(@NotNull(value = "rootCommandRegistry cannot be null") final RootCommandRegistry rootCommandRegistry,
                    @NotNull(value = "module cannot be null") final Module module) {
        this.rootCommandRegistry = rootCommandRegistry;
        this.module = module;
    }

    /**
     * @param alias The alias of the {@link Command} to get
     *
     * @return An {@link Optional} containing the {@link Command} requested if it was present
     */
    public Command getCommandByNameOrAlias(@NotNull(value = "command cannot be null") final String alias) {
        return this.rootCommandRegistry.getCommandByNameOrAlias(alias.toLowerCase(Locale.ENGLISH));
    }

    /**
     * @param command The {@link Command} to register
     *
     * @throws IllegalArgumentException If the {@link Command} is already registered
     */
    public void register(@NotNull(value = "command cannot be null") final Command command) throws IllegalArgumentException {
        if (this.rootCommandRegistry.getCommandByNameOrAlias(command.getName()) != null) {
            throw new IllegalArgumentException("Command '" + command.getName() + "' already exists");
        }
        this.rootCommandRegistry.registerCommand(command, this.module);
    }

    /**
     * @param command The {@link Command} to unregisterCommand
     *
     * @throws IllegalArgumentException If the {@link Command} is not registered
     */
    public void unregister(@NotNull(value = "command cannot be null") final Command command) throws IllegalArgumentException {
        if (this.rootCommandRegistry.getCommandByNameOrAlias(command.getName()) != null) {
            throw new IllegalArgumentException("Command '" + command.getName() + "' is not registered");
        }
        this.rootCommandRegistry.unregisterCommand(command);
    }

    /**
     * Unregisters all commands registered by this {@link Module}
     */
    public void unregisterAll() {
        this.rootCommandRegistry.unregisterAll(this.module);
    }
}
