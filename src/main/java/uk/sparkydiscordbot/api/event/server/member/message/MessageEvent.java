package uk.sparkydiscordbot.api.event.server.member.message;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import uk.sparkydiscordbot.api.event.server.ChannelEvent;

public class MessageEvent<T extends TextChannel> extends ChannelEvent<T> {

    private final Member member;
    private final User user;
    private final Message message;

    /**
     * @param member  The {@link Member} who triggered this event
     * @param server  The {@link Guild} this event was triggered in
     * @param channel The {@link TextChannel} this event was triggered in
     * @param message The {@link Message} affected by this event
     * @param jda     The {@link JDA} instance
     */
    public MessageEvent(final Member member,
                        final User user,
                        final Guild server,
                        final T channel,
                        final Message message,
                        final JDA jda) {
        super(server, channel, jda);
        this.member = member;
        this.user = user;
        this.message = message;
    }

    /**
     * @return The {@link User} responsible for this event
     */
    public User getUser() {
        return this.member.getUser();
    }

    /**
     * @return The {@link Member} who triggered this event
     */
    public Member getMember() {
        return this.member;
    }

    /**
     * @return The {@link Message} affected by this event
     */
    public Message getMessage() {
        return this.message;
    }

}
