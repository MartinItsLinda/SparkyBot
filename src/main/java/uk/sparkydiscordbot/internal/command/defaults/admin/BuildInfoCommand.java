package uk.sparkydiscordbot.internal.command.defaults.admin;

import net.dv8tion.jda.api.JDAInfo;
import uk.sparkydiscordbot.BotInfo;
import uk.sparkydiscordbot.api.entities.command.Arguments;
import uk.sparkydiscordbot.api.entities.command.CommandContext;
import uk.sparkydiscordbot.api.entities.command.CommandExecutor;
import uk.sparkydiscordbot.api.entities.messaging.Messaging;
import uk.sparkydiscordbot.api.entities.utils.BotUtils;

import java.util.StringJoiner;

public class BuildInfoCommand implements CommandExecutor {

    @Override 
    public boolean execute(final CommandContext context,
                           final Arguments args) {
        if (!BotUtils.isBotAdmin(context.getMember())) {
            context.reply(context.i18n("notBotAdmin"));
        } else if (BotInfo.VERSION.equals("Unknown")) {
            context.reply("There is no build information available, running inside an IDE?");
        } else {
            final StringJoiner joiner = new StringJoiner("\n");

            joiner.add("--------------- Discord ---------------");
            joiner.add(String.format("Rest API Version: %d", JDAInfo.DISCORD_REST_VERSION));
            joiner.add(String.format("Audio Gateway Version: %d", JDAInfo.AUDIO_GATEWAY_VERSION));
            joiner.add("----------------- JDA -----------------");
            joiner.add(String.format("Repository: %s", JDAInfo.GITHUB));
            joiner.add(String.format("JDA Version: %s", JDAInfo.VERSION));
            joiner.add("----------------- Bot -----------------");
            joiner.add(String.format("Bot Version: %s", BotInfo.VERSION));
            joiner.add(String.format("Commit Long: %s", BotInfo.GIT_COMMIT));
            joiner.add(String.format("Branch: %s", BotInfo.GIT_BRANCH));
            joiner.add(String.format("Build Date: %s", BotInfo.BUILD_DATE));
            joiner.add("---------------------------------------");

            context.reply(Messaging.getLocalMessageBuilder().appendCodeBlock(joiner.toString(), "text").build());
        }
        return true;
    }

}
