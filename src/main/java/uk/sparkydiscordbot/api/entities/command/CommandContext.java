package uk.sparkydiscordbot.api.entities.command;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import org.jetbrains.annotations.NotNull;
import uk.sparkydiscordbot.api.entities.configuration.GuildSettings;
import uk.sparkydiscordbot.api.entities.language.I18n;
import uk.sparkydiscordbot.api.entities.messaging.Messaging;

public class CommandContext {

    private final TextChannel channel;
    private final Member sender;
    private final Guild server;
    private final GuildSettings settings;
    private final Message message;
    private final String trigger;
    private final Command command;
    private final I18n i18n;
    private final JDA jda;

    /**
     * @param channel  The channel this {@link Command} was executed in
     * @param sender   The {@link User} who executed this {@link Command}
     * @param server   The {@link Guild} this command was executed in
     * @param settings THe {@link GuildSettings}
     * @param trigger  The command trigger
     * @param command  The {@link Command} executed
     * @param message  The whole message that caused this {@link Command} to trigger
     */
    public CommandContext(@NotNull(value = "channel cannot be null") final TextChannel channel,
                          @NotNull(value = "sender cannot be null") final Member sender,
                          @NotNull(value = "server cannot be null") final Guild server,
                          @NotNull(value = "settings cannot be null") final GuildSettings settings,
                          @NotNull(value = "trigger cannot be null") final String trigger,
                          @NotNull(value = "command cannot be null") final Command command,
                          @NotNull(value = "message cannot be null") final Message message,
                          @NotNull(value = "i18n cannot be null") final I18n i18n,
                          @NotNull(value = "jda cannot be null") final JDA jda) {
        this.channel = channel;
        this.sender = sender;
        this.server = server;
        this.settings = settings;
        this.trigger = trigger;
        this.command = command;
        this.message = message;
        this.i18n = i18n;
        this.jda = jda;
    }

    /**
     * @return The {@link TextChannel} this command was executed in (can be null)
     */
    public TextChannel getChannel() {
        return this.channel;
    }

    /**
     * @return The {@link User} who executed this command
     */
    public User getSender() {
        return this.sender.getUser();
    }

    /**
     * @return The executor of this {@link Command} as a {@link Member}
     */
    public Member getMember() {
        return this.sender;
    }

    /**
     * @return The {@link Guild} this command was executed in (can be null)
     */
    public Guild getServer() {
        return this.server;
    }

    /**
     * @return The {@link GuildSettings}
     */
    public GuildSettings getGuildSettings() {
        return this.settings;
    }

    /**
     * @return The command trigger
     */
    public String getLabel() {
        return this.trigger;
    }

    /**
     * @return The {@link Command} executed
     */
    public Command getCommand() {
        return this.command;
    }

    /**
     * @return The whole message that caused this {@link Command} to trigger
     */
    public Message getMessage() {
        return this.message;
    }

    /**
     * @return The {@link JDA} instance
     */
    public JDA getJDA() {
        return this.jda;
    }

    /**
     * @return The {@link I18n} instance
     */
    public I18n getI18n() {
        return this.i18n;
    }

    /**
     * Translate the input string
     *
     * @param key  The message key
     * @param args The arguments
     *
     * @return The translated string
     */
    public String i18n(final String key, final Object... args) {
        return this.i18n.tl(key, args);
    }

    /**
     * Sends a message in the {@link MessageChannel} this command was executed in
     *
     * @param message The message to send
     */
    public void reply(@NotNull(value = "message cannot be null") final String message) {
        Messaging.send(this.channel, message);
    }

    /**
     * Sends a message in the {@link MessageChannel} this command was executed in
     *
     * @param embed The {@link MessageEmbed} to sent
     */
    public void reply(@NotNull(value = "embed cannot be null") final MessageEmbed embed) {
        Messaging.send(this.channel, embed);
    }

    /**
     * Sends a message in the {@link MessageChannel} this command was executed in
     *
     * @param message The {@link Message} to send
     */
    public void reply(@NotNull(value = "message cannot be null") final Message message) {
        Messaging.send(this.channel, message);
    }

}
