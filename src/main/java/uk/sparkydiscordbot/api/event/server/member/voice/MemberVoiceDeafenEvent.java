package uk.sparkydiscordbot.api.event.server.member.voice;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import org.jetbrains.annotations.NotNull;

/**
 * Called whenever a {@link Member} toggles their deafened state
 */
public class MemberVoiceDeafenEvent extends MemberVoiceEvent {

    /**
     * @param member     The {@link Member} that triggered this event
     * @param server     The {@link Guild} this event was triggered in
     * @param channel    The {@link VoiceChannel} joined
     * @param jda        The {@link JDA} instance
     */
    public MemberVoiceDeafenEvent(@NotNull(value = "member cannot be null") final Member member,
                                  @NotNull(value = "guild cannot be null") final Guild server,
                                  @NotNull(value = "channel cannot be null") final VoiceChannel channel,
                                  @NotNull(value = "jda cannot be null") final JDA jda) {
        super(server, member, channel, jda);
    }

}
