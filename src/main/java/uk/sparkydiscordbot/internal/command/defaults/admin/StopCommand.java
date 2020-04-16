package uk.sparkydiscordbot.internal.command.defaults.admin;

import uk.sparkydiscordbot.ExitCode;
import uk.sparkydiscordbot.api.entities.command.Arguments;
import uk.sparkydiscordbot.api.entities.command.CommandContext;
import uk.sparkydiscordbot.api.entities.command.CommandExecutor;
import uk.sparkydiscordbot.api.entities.utils.BotUtils;

public class StopCommand implements CommandExecutor {

    @Override
    public boolean execute(final CommandContext context,
                           final Arguments args) {

        if (!BotUtils.isBotAdmin(context.getMember())) {
            context.reply(context.i18n("notBotAdmin"));
        } else {
            context.reply("Shutting down...");
            if (args.hasFlag("-no-reboot")) {
                System.exit(ExitCode.EXIT_CODE_NORMAL);
            } else {
                System.exit(ExitCode.EXIT_CODE_RESTART);
            }
        }

        return true;
    }

}
