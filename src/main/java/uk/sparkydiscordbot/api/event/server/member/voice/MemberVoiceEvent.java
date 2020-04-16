package uk.sparkydiscordbot.api.event.server.member.voice;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import org.jetbrains.annotations.NotNull;
import uk.sparkydiscordbot.api.event.server.voice.VoiceEvent;

public class MemberVoiceEvent extends VoiceEvent {

    private final Member member;

    public MemberVoiceEvent(@NotNull(value = "server cannot be null") final Guild server,
                            @NotNull(value = "member cannot be null") final Member member,
                            @NotNull(value = "channel cannot be null") final VoiceChannel channel,
                            @NotNull(value = "jda cannot be null") final JDA jda) {
        super(server, channel, jda);
        this.member = member;
    }

    public Member getMember() {
        return this.member;
    }
}
