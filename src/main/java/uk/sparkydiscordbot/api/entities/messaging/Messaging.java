package uk.sparkydiscordbot.api.entities.messaging;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.sparkydiscordbot.api.entities.utils.Objects;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class Messaging {

    private static final Logger LOGGER = LoggerFactory.getLogger(Messaging.class);

    private static final ThreadLocal<EmbedBuilder> EMBED_BUILDER = ThreadLocal.withInitial(EmbedBuilder::new);
    private static final ThreadLocal<MessageBuilder> MESSAGE_BUILDER = ThreadLocal.withInitial(MessageBuilder::new);

    /**
     * Retrieve a thread local instance of {@link EmbedBuilder}
     *
     * @return A thread local instance of {@link EmbedBuilder}
     */
    public static EmbedBuilder getLocalEmbedBuilder() {
        return EMBED_BUILDER.get().clear();
    }

    /**
     * Retrieve a thread local instance of {@link MessageBuilder}
     *
     * @return A thread local instance of {@link EmbedBuilder}
     */
    public static MessageBuilder getLocalMessageBuilder() {
        return MESSAGE_BUILDER.get().clear();
    }

    /**
     * Convert the input {@code message} into a {@link Message}
     *
     * @param message The message content
     *
     * @return The {@link Message}
     */
    public static Message getAsMessage(@NotNull(value = "message cannot be null") final String message) {
        return getLocalMessageBuilder().setContent(message).build();
    }

    /**
     * Convert a {@link MessageEmbed} into a {@link Message}
     *
     * @param embed The {@link MessageEmbed}
     *
     * @return The {@link Message}
     */
    public static Message getAsMessage(@NotNull(value = "embed cannot be null") final MessageEmbed embed) {
        return getLocalMessageBuilder().setEmbed(embed).build();
    }

    /**
     * Send a message to the provided {@link MessageChannel}
     *
     * @param channel The {@link MessageChannel} to send to
     * @param message The message to send
     */
    public static void send(@NotNull(value = "channel cannot be null") final MessageChannel channel,
                            @NotNull(value = "message cannot be null") final String message) {
        Messaging.message(channel, message).send();
    }

    /**
     * Send a {@link MessageEmbed} to the provided {@link MessageChannel}
     *
     * @param channel The {@link MessageChannel} to send to
     * @param embed   The {@link MessageEmbed} to send
     */
    public static void send(@NotNull(value = "channel cannot be null") final MessageChannel channel,
                            @NotNull(value = "embed cannot be null") final MessageEmbed embed) {
        Messaging.message(channel, embed).send();
    }

    /**
     * Send a {@link Message} to the provided {@link MessageChannel}
     *
     * @param channel The {@link MessageChannel} to send to
     * @param message The {@link Message} to send
     */
    public static void send(@NotNull(value = "channel cannot be null") final MessageChannel channel,
                            @NotNull(value = "message cannot be null") final Message message) {
        Messaging.message(channel, message).send();
    }

    //TODO: More descriptive documentation

    /**
     *
     *
     * @param channel The {@link MessageChannel} to send to
     * @param message The message to send
     *
     * @return A new {@link MessageActionBuilder}
     */
    public static MessageActionBuilder message(@NotNull(value = "channel cannot be null") final MessageChannel channel,
                                               @NotNull(value = "message cannot be null") final String message) {
        return new MessageActionBuilder(channel, Messaging.getAsMessage(message));
    }

    /**
     * <documentation here>
     *
     * @param channel The {@link MessageChannel} to send to
     * @param embed   The {@link MessageEmbed} to send
     *
     * @return A new {@link MessageActionBuilder}
     */
    public static MessageActionBuilder message(@NotNull(value = "channel cannot be null") final MessageChannel channel,
                                               @NotNull(value = "message cannot be null") final MessageEmbed embed) {
        return new MessageActionBuilder(channel, Messaging.getAsMessage(embed));
    }

    /**
     * <documentation here>
     *
     * @param channel The {@link MessageChannel} to send to
     * @param message The {@link Message} to send
     *
     * @return A new {@link MessageActionBuilder}
     */
    public static MessageActionBuilder message(@NotNull(value = "channel cannot be null") final MessageChannel channel,
                                               @NotNull(value = "message cannot be null") final Message message) {
        return new MessageActionBuilder(channel, message);
    }

    /**
     * Delete the {@link Message}
     *
     * @param channel The {@link MessageChannel}
     * @param id      The id of the message to delete
     *
     * @throws NumberFormatException Should the input {@code id} not be convertible to a {@link Long}
     */
    public static void deleteMessageById(@NotNull(value = "channel cannot be null") final MessageChannel channel,
                                         @NotNull(value = "id cannot be null") final String id) throws NumberFormatException {
        Messaging.deleteMessageById(channel, Long.parseLong(id));
    }

    /**
     * Delete a {@link Message}
     *
     * @param channel The {@link MessageChannel}
     * @param id      The id of the message to delete
     */
    public static void deleteMessageById(@NotNull(value = "channel cannot be null") final MessageChannel channel,
                                         final long id) {
        deleteMessageById(channel, id, null);
    }

    /**
     * Delete a {@link Message}
     *
     * @param channel The {@link MessageChannel}
     * @param id      The id of the message to delete
     * @param reason  The reason for deleting the message
     */
    public static void deleteMessageById(@NotNull(value = "channel cannot be null") final MessageChannel channel,
                                         final long id,
                                         final String reason) {
        Objects.checkNotNegative(id, "id cannot be negative");
        channel.deleteMessageById(id).reason(reason).queue();
    }

    /**
     * Delete the {@link Message}
     *
     * @param message The {@link Message} to delete
     */
    public static void deleteMessage(@NotNull(value = "message cannot be null") final Message message) {
        try {
            message.delete().queue(null, __ -> {});
        } catch (final InsufficientPermissionException ex) {
            LOGGER.info("Couldn't delete message from channel %s due to missing permission %s",
                    message.getTextChannel().getId(), ex.getPermission().getName());
        }
    }

    public static final class MessageActionBuilder {

        private MessageChannel channel;
        private Message message;

        private long deleteAfter;
        private TimeUnit unit;
        private Consumer<Message> success;
        private Consumer<Throwable> failure;

        /**
         * Construct a new {@link MessageActionBuilder}
         *
         * @param channel The {@link MessageChannel} to send to
         * @param message The {@link Message} to send
         */
        public MessageActionBuilder(@NotNull(value = "channel cannot be null") final MessageChannel channel,
                                    @NotNull(value = "message cannot be null") final Message message)  {
            this.channel = channel;
            this.message = message;
            this.unit = TimeUnit.MILLISECONDS;
        }

        /**
         * Set the {@link MessageChannel} the message should be sent to
         *
         * @param channel The {@link MessageChannel} to send to
         *
         * @return This {@link MessageActionBuilder}
         */
        public MessageActionBuilder channel(@NotNull(value = "channel cannot be null") final MessageChannel channel) {
            this.channel = channel;
            return this;
        }

        /**
         * Set the {@link Message} to be sent
         *
         * @param message The {@link Message} to be sent
         */
        public MessageActionBuilder message(@NotNull(value = "message cannot be null") final Message message) {
            this.message = message;
            return this;
        }

        /**
         * Set how long to wait before deleting the sent message
         *
         * @param deleteAfter How long to wait before deleting the sent message in millis
         *
         * @return This {@link Messaging.MessageActionBuilder}
         */
        public MessageActionBuilder deleteAfter(final long deleteAfter) {
            return this.deleteAfter(deleteAfter, TimeUnit.MILLISECONDS);
        }

        /**
         * Set how long to wait before deleting the sent message
         *
         * @param deleteAfter How long to wait before deleting the sent message in millis
         * @param unit        The {@link TimeUnit} used to measure how long to wait
         *
         * @return This {@link Messaging.MessageActionBuilder}
         */
        public MessageActionBuilder deleteAfter(final long deleteAfter,
                                                @NotNull(value = "unit cannot be null") final TimeUnit unit) {
            Objects.checkNotNegative(deleteAfter, "deleteAfter cannot be negative");
            this.deleteAfter = deleteAfter;
            this.unit = unit;
            return this;
        }

        /**
         * @param success The {@link Consumer} to call if the request executes successfully
         *
         * @return This {@link Messaging.MessageActionBuilder}
         */
        public MessageActionBuilder success(@NotNull(value = "consumer cannot be null") final Consumer<Message> success) {
            this.success = success;
            return this;
        }

        /**
         * @param failure The {@link Consumer} to call should the message not be sent, or the automatic deletion fail.
         *
         * @return This {@link Messaging.MessageActionBuilder}
         */
        public MessageActionBuilder failure(@NotNull(value = "consumer cannot be null") final Consumer<Throwable> failure) {
            this.failure = failure;
            return this;
        }

        /**
         * Send the message
         */
        public void send() {
            this.send(false);
        }

        /**
         * Send the message
         *
         * @param threadBlocking Whether to block the current thread
         */
        public Message send(final boolean threadBlocking) {

            try {
                final MessageAction action = this.channel.sendMessage(this.message);
                if (threadBlocking) {
                    final Message message = action.complete();
                    if (this.deleteAfter > 0) {
                        message.delete().queueAfter(this.deleteAfter, this.unit, null, this.failure);
                    }
                    return message;
                } else {
                    action.queue(this.success, this.failure);
                }
            } catch (final InsufficientPermissionException ex) {

                if (this.failure != null) {
                    this.failure.accept(ex);
                }

                LOGGER.warn(String.format("Couldn't send message to channel %s due to missing permission %s",
                        this.channel.getId(), ex.getPermission().getName()));

            }

            return null;
        }

    }

}
