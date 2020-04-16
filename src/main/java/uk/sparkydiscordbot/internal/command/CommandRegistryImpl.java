package uk.sparkydiscordbot.internal.command;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.jetbrains.annotations.NotNull;
import uk.sparkydiscordbot.api.entities.command.Command;
import uk.sparkydiscordbot.api.entities.command.RootCommandRegistry;
import uk.sparkydiscordbot.api.entities.module.Module;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CommandRegistryImpl implements RootCommandRegistry {

    private final Map<Module, List<Command>> commandMap;

    public CommandRegistryImpl() {
        this.commandMap = Maps.newHashMap();
    }

    @Override
    public Map<Module, List<Command>> getCommandMap() {
        return this.commandMap;
    }

    @Override
    public Command getCommandByNameOrAlias(@NotNull(value = "alias cannot be null") final String alias) {
        for (final List<Command> commandList : this.commandMap.values()) {
            for (final Command command : commandList) {
                if (command.getAliases().contains(alias.toLowerCase(Locale.ENGLISH))) {
                    return command;
                }
            }
        }
        return null;
    }

    @Override
    public void registerCommand(@NotNull(value = "command cannot be null") final Command command,
                                final Module module) {
        if (this.getCommandByNameOrAlias(command.getName()) != null) {
            throw new IllegalArgumentException("Command '" + command.getName() + "' is already registered");
        }
        final List<Command> commands = this.commandMap.computeIfAbsent(module, __ -> Lists.newArrayList());
        commands.add(command);
    }

    @Override
    public void unregisterCommand(@NotNull(value = "command cannot be null") final Command command) {
        this.commandMap.keySet().forEach(module -> this.commandMap.get(module).removeIf(cmd -> cmd.getName().equalsIgnoreCase(command.getName())));
    }

    @Override
    public void unregisterAll(@NotNull(value = "module cannot be null") final Module module) {
        this.commandMap.remove(module);
    }

    @Override
    public void unregisterAll() {
        this.commandMap.clear();
    }

}
