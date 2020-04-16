package uk.sparkydiscordbot.api.event.server.member.voice;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import org.jetbrains.annotations.NotNull;

public class MemberVoiceMoveEvent extends MemberVoiceEvent {

    private final VoiceChannel left;

    /**
     * @param member The {@link Member} that triggered this event
     * @param server The {@link Guild} this event was triggered in
     * @param joined The {@link VoiceChannel} joined
     * @param left   The {@link VoiceChannel} left
     * @param jda    The {@link JDA} instance
     */
    public MemberVoiceMoveEvent(@NotNull(value = "member cannot be null") final Member member,
                                @NotNull(value = "guild cannot be null") final Guild server,
                                @NotNull(value = "joined cannot be null") final VoiceChannel joined,
                                @NotNull(value = "left cannot be null") final VoiceChannel left,
                                @NotNull(value = "jda cannot be null") final JDA jda) {
        super(server, member, joined, jda);
        this.left = left;
    }

    /**
     * @return The {@link VoiceChannel} the {@link Member} was moved from
     */
    public VoiceChannel getChannelLeft() {
        return this.left;
    }

}
