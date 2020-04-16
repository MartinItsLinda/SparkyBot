package uk.sparkydiscordbot.api.event.server.member.message;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import org.jetbrains.annotations.NotNull;

public class MemberReactionEvent extends MessageEvent<TextChannel> {

    private final MessageReaction reaction;
    private final ReactionType reactionType;

    /**
     * @param server       The {@link Guild} this event was called in
     * @param member       The {@link Member} who triggered the event
     * @param channel      The {@link TextChannel} that this event was called in
     * @param message      The {@link Message} the reaction was placed on
     * @param reaction     The {@link MessageReaction}
     * @param reactionType The {@link MemberReactionEvent.ReactionType}
     * @param jda          The {@link JDA} instance
     */
    public MemberReactionEvent(@NotNull(value = "guild cannot be null") final Guild server,
                               @NotNull(value = "member cannot be null") final Member member,
                               @NotNull(value = "channel cannot be null") final TextChannel channel,
                               @NotNull(value = "message cannot be null") final Message message,
                               @NotNull(value = "reaction cannot be null") final MessageReaction reaction,
                               @NotNull(value = "reaction type cannot be null") final ReactionType reactionType,
                               @NotNull(value = "jda cannot be null") final JDA jda) {
        super(member, member.getUser(), server, channel, message, jda);
        this.reaction = reaction;
        this.reactionType = reactionType;
    }

    /**
     * @return The {@link MessageReaction}
     */
    public MessageReaction getReaction() {
        return this.reaction;
    }

    /**
     * @return The {@link ReactionType}
     */
    public ReactionType getReactionType() {
        return this.reactionType;
    }

    public enum ReactionType {

        ADD,
        REMOVE,
        REMOVE_ALL

    }
}
