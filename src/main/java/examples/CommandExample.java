package examples;

import uk.sparkydiscordbot.api.entities.command.Arguments;
import uk.sparkydiscordbot.api.entities.command.CommandContext;
import uk.sparkydiscordbot.api.entities.command.CommandExecutor;

public class CommandExample implements CommandExecutor {

    @Override
    public boolean execute(final CommandContext context,
                           final Arguments args) {

        context.reply(String.format("Hello, %s!", context.getMember().getAsMention()));

        return true;
    }

}
