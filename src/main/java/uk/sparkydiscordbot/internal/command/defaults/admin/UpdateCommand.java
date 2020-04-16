package uk.sparkydiscordbot.internal.command.defaults.admin;

import org.jetbrains.annotations.NotNull;
import uk.sparkydiscordbot.Bot;
import uk.sparkydiscordbot.ExitCode;
import uk.sparkydiscordbot.api.entities.command.Arguments;
import uk.sparkydiscordbot.api.entities.command.CommandContext;
import uk.sparkydiscordbot.api.entities.command.CommandExecutor;
import uk.sparkydiscordbot.api.entities.utils.BotUtils;

public class UpdateCommand implements CommandExecutor {

    private final Bot bot;

    public UpdateCommand(@NotNull(value = "bot cannot be null") final Bot bot) {
        this.bot = bot;
    }

    @Override
    public boolean execute(final CommandContext context,
                           final Arguments args) {

        if (!BotUtils.isBotAdmin(context.getMember())) {
            context.reply(context.i18n("notBotAdmin"));
        } else {

            if (this.bot.getConfig().getBoolean("client.use_beta_builds", false)) {
                context.reply("Updating to latest **beta** build.");
                System.exit(ExitCode.EXIT_CODE_UPDATE_LATEST);
            } else {
                context.reply("Updating to latest **recommended** build.");
                System.exit(ExitCode.EXIT_CODE_UPDATE_RECOMMENDED);
            }

        }

        return true;
    }

}
