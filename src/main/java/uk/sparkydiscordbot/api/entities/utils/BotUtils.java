package uk.sparkydiscordbot.api.entities.utils;

import com.google.common.collect.Lists;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;
import uk.sparkydiscordbot.Bot;
import uk.sparkydiscordbot.Environment;
import uk.sparkydiscordbot.api.entities.configuration.GuildSettings;
import uk.sparkydiscordbot.api.entities.messaging.Messaging;

import java.awt.*;
import java.util.regex.Pattern;

public class BotUtils {

    /**
     * Turn a hex colour into its RGB value
     *
     * @param hex The hex
     *
     * @return The {@link Color}
     *
     * @throws IllegalArgumentException If {@code hex} is empty or not equal to 6 in length
     * @throws NumberFormatException    If the provided {@code hex} is not convertible to an integer
     */
    public static Color getColor(@NotNull(value = "hex cannot be null") final String hex) {
        Objects.checkArgument(!hex.isEmpty() && hex.length() == 6, "Invalid hex value: " + hex);

        final int red = Integer.valueOf(hex.substring(0, 2), 16);
        final int green = Integer.valueOf(hex.substring(2, 4), 16);
        final int blue = Integer.valueOf(hex.substring(4, 6), 16);

        return new Color(red, green, blue);
    }

    /**
     * Check whether a {@link Member} is a bot admin
     *
     * @param member The {@link Member}
     *
     * @return {@code true} if the provided {@link Member} is an administrator, {@code false} otherwise
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isBotAdmin(@NotNull(value = "member cannot be null") final Member member) {
        return isBotAdmin(member.getUser());
    }

    /**
     * Check whether a {@link Member} is a bot admin
     *
     * @param user The {@link User}
     *
     * @return {@code true} if the provided {@link Member} is an administrator, {@code false} otherwise
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isBotAdmin(@NotNull(value = "user cannot be null") final User user) {
        for (final long id : Bot.getInstance().getConfig().<Long>getList("client.bot_admins", Lists.newArrayListWithCapacity(0))) {
            if (id == user.getIdLong()) {
                return true;
            }
        }
        return false;
    }

    public static String checkForPlurals(final boolean wantPlural,
                                         final String line) {
        StringBuilder newString = new StringBuilder();
        final Pattern pattern = Pattern.compile("\\[+[\\w]*+\\|+[\\w]*+]");
        for (String s : line.split(" ")) {
            if (pattern.matcher(s).matches()) {
                s = s.replaceAll("\\[", "");
                s = s.replaceAll("]", "");
                final String[] split = s.split("\\|");
                newString.append(split[(wantPlural ? 1 : 0)]).append(" ");
            } else {
                newString.append(s).append(" ");
            }
        }
        return newString.toString();
    }

    public static String getPrefix(final long server_id) {
        if (Environment.getEnvironment() == Environment.DEVELOPMENT) {
            return "!>";
        }
        return GuildSettings.getConfig(Long.toString(server_id)).getString("prefix");
    }

    public static EmbedBuilder generateMessage(String message, Color accent){
        return Messaging.getLocalEmbedBuilder().setColor(accent).setDescription(message);
    }


}
