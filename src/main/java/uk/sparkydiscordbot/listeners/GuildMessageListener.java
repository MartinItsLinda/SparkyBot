package uk.sparkydiscordbot.listeners;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.internal.utils.PermissionUtil;
import org.jetbrains.annotations.NotNull;
import uk.sparkydiscordbot.Bot;
import uk.sparkydiscordbot.Environment;
import uk.sparkydiscordbot.api.entities.command.Arguments;
import uk.sparkydiscordbot.api.entities.command.Command;
import uk.sparkydiscordbot.api.entities.command.CommandContext;
import uk.sparkydiscordbot.api.entities.command.argument.RawArguments;
import uk.sparkydiscordbot.api.entities.configuration.GuildSettings;
import uk.sparkydiscordbot.api.entities.language.I18n;
import uk.sparkydiscordbot.api.entities.messaging.Messaging;
import uk.sparkydiscordbot.api.entities.utils.BotUtils;
import uk.sparkydiscordbot.api.event.server.member.MemberChatEvent;
import uk.sparkydiscordbot.api.exception.command.CommandSyntaxException;
import uk.sparkydiscordbot.api.exception.parser.ParserException;
import uk.sparkydiscordbot.util.RateLimiter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GuildMessageListener extends BotListener {

    private static final Pattern FLAG_PATTERN = Pattern.compile("-([\\w-]+)(?:\\s+(\\w+))?");

    private static final int REQUESTS = 10; //the maximum amount of commands executable (within the period)
    private static final int REFRESH = 15; //how many second before a member can execute another ? requests
    private static final long COMMAND_TIMEOUT_AFTER = 5_000L;


    private final Map<Long, RateLimiter> rateLimits;

    public GuildMessageListener(@NotNull(value = "parent cannot be null") final Bot bot) {
        super(bot);
        this.rateLimits = Maps.newHashMap();
    }

    @Override
    public void onGuildMessageReceived(final GuildMessageReceivedEvent event) {

        final Member member = event.getMember();
        final User user = event.getAuthor();
        final Guild guild = event.getGuild();
        final TextChannel channel = event.getChannel();
        final Message message = event.getMessage();
        final JDA jda = event.getJDA();

        final String raw = message.getContentRaw().trim();

        if (raw.isEmpty() || user.isBot() || message.isWebhookMessage() || !PermissionUtil.checkPermission(channel, guild.getSelfMember(), Permission.MESSAGE_WRITE, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_READ, Permission.MESSAGE_EXT_EMOJI)) {
            return;
        }

        final Future<?> future = Bot.CACHED_EXECUTOR_SERVICE.submit(() -> { //this is done async so as we get bigger we don't end up with bigger delays on commands

            final GuildSettings config = GuildSettings.getConfig(guild);
            final I18n i18n = I18n.getI18n(guild);

            //get the prefix, if this is a dev environment then the prefix is hard coded

            final String prefix = Environment.getEnvironment().isRelease() ? config.getString("prefix") : Environment.getEnvironment().getPrefix(); //prefix shouldn't return null but if it does /shrug

            String content = raw;

            //check if this is a command, if it isn't then call the chat event
            if (content.startsWith(prefix)) {
                content = content.substring(prefix.length());
            } else if (content.startsWith(guild.getSelfMember().getAsMention())) {
                content = content.substring(guild.getSelfMember().getAsMention().length());
                if (content.trim().isEmpty()) { //@Mention for prefix
                    Messaging.send(channel, i18n.tl("commandPrefixMention", prefix));
                }
            } else {
                this.getEventManager().callEvent(new MemberChatEvent(event.getMember(), user, channel, guild, message, jda));
                return;
            }

            content = content.trim();
            if (!content.isEmpty()) {

                final String[] split = content.split("\\s+"); //whitespace

                final String trigger = split[0].toLowerCase();
                final Command command = this.getCommandManager().getCommandByNameOrAlias(trigger);

                //we rate limit commands so that people can't get the bot rate limited HA, well at least we try to anyway
                final RateLimiter limiter = this.rateLimits.computeIfAbsent(user.getIdLong(), id -> new RateLimiter(REQUESTS, REFRESH, TimeUnit.SECONDS));
                if (command == null || !BotUtils.isBotAdmin(user) && !limiter.tryAcquire()) {
                    this.getEventManager().callEvent(new MemberChatEvent(event.getMember(), user, channel, guild, message, jda));
                } else {

                    final Matcher matcher = FLAG_PATTERN.matcher(content);

                    final String[] args = this.getArgs(split);
                    final List<Command.Flag> flags = this.getFlags(matcher);

                    final CommandContext context = new CommandContext(channel, member, guild, config, trigger, command, message, i18n, jda);
                    final RawArguments rawArgs = new RawArguments(args);

                    try {
                        command.getExecutor().execute(context, new Arguments(context, rawArgs, flags));
                    } catch (final CommandSyntaxException ex) {
                        context.reply(context.i18n("commandCorrectUsage", ex.getCommand(), ex.getSyntax()));
                    } catch (final ParserException ex) {
                        context.reply(ex.getMessage());
                    } catch (final Exception ex) {
                        context.reply(context.i18n("commandUncaughtError", ex.getMessage()));
                        ex.printStackTrace();
                    }

                }

            }

        });

        //This is just a watchdog, so in the event I messed up a command doesn't slowly start killing the bot
        //Insurance is wonderful when you need it
        Bot.CACHED_EXECUTOR_SERVICE.submit(() -> {

            try {
                future.get(COMMAND_TIMEOUT_AFTER, TimeUnit.MILLISECONDS);
            } catch (final InterruptedException | TimeoutException ignored) {
                message.getChannel().sendMessage(String.format("Task exceeded maximum execution time of %d and has been cancelled.", COMMAND_TIMEOUT_AFTER)).queue();
            } catch (final Exception ex) {
                message.getChannel().sendMessage(String.format("An error occurred whilst attempting to execute that command, if this" +
                        "persists please contact the developers: %s", ex.getMessage())).queue();
            }

            if (!future.isDone()) {
                future.cancel(true);
            }

        });


    }

    private String[] getArgs(final String[] args) {

        final List<String> newArgs = Lists.newArrayList();

        for (int i = 1; i < args.length; i++) {

            final String argument = args[i];
            if (FLAG_PATTERN.matcher(argument).matches()) {
                break;
            }

            newArgs.add(argument);

        }

        return newArgs.toArray(new String[0]);

    }

    private List<Command.Flag> getFlags(final Matcher matcher) {

        final List<Command.Flag> matches = Lists.newArrayList();
        while (matcher.find()) {

            final String flag = matcher.group(1);

            String value = null;
            if (matcher.groupCount() > 1) {
                value = matcher.group(2);
            }

            matches.add(new Command.Flag(flag, value));

        }

        return matches;
    }

}
