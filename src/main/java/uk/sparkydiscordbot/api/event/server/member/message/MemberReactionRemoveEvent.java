package uk.sparkydiscordbot.api.event.server.member.message;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import org.jetbrains.annotations.NotNull;

public class MemberReactionRemoveEvent extends MemberReactionEvent {

    /**
     * @param server   The {@link Guild} this event was called in
     * @param member   The {@link Member} who triggered the event
     * @param channel  The {@link TextChannel} that this event was called in
     * @param message  The {@link Message} the reaction was placed on
     * @param reaction The {@link MessageReaction}
     * @param jda      The {@link JDA} instance
     */
    public MemberReactionRemoveEvent(@NotNull(value = "guild cannot be null") final Guild server,
                                     @NotNull(value = "member cannot be null") final Member member,
                                     @NotNull(value = "channel cannot be null") final TextChannel channel,
                                     @NotNull(value = "message cannot be null") final Message message,
                                     @NotNull(value = "reaction cannot be null") final MessageReaction reaction,
                                     @NotNull(value = "jda cannot be null") final JDA jda) {
        super(server, member, channel, message, reaction, ReactionType.REMOVE, jda);
    }
}
