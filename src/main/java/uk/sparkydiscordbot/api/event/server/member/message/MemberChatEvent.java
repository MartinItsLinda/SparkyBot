package uk.sparkydiscordbot.api.event.server.member.message;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import org.jetbrains.annotations.NotNull;

public class MemberChatEvent extends MessageEvent<TextChannel> {


    /**
     * @param member  The author of the {@link Message} that triggered this event
     * @param channel The {@link TextChannel} that this {@link Message} was sent in
     * @param server  The {@link Guild} that this {@link Message} was sent in
     * @param message The {@link Message} that was sent
     * @param jda     The {@link JDA} instance
     */
    public MemberChatEvent(@NotNull(value = "author cannot be null") final Member member,
                           @NotNull(value = "textChannel cannot be null") final TextChannel channel,
                           @NotNull(value = "guild cannot be null") final Guild server,
                           @NotNull(value = "message cannot be null") final Message message,
                           @NotNull(value = "jda cannot be null") final JDA jda) {
        super(member, member.getUser(), server, channel, message, jda);
    }

}
