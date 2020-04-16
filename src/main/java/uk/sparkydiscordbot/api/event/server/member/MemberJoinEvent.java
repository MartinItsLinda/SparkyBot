package uk.sparkydiscordbot.api.event.server.member;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import org.jetbrains.annotations.NotNull;

public class MemberJoinEvent extends MemberEvent {

    public MemberJoinEvent(@NotNull(value = "guild cannot be null") final Guild server,
                           @NotNull(value = "member cannot be null") final Member member,
                           @NotNull(value = "jda cannot be null") final JDA jda) {
        super(member, member.getUser(), server, jda);
    }

}
