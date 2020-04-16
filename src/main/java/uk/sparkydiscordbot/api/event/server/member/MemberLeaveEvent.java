package uk.sparkydiscordbot.api.event.server.member;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;

public class MemberLeaveEvent extends MemberEvent {

    public MemberLeaveEvent(@NotNull(value = "guild cannot be null") final Guild server,
                            @NotNull(value = "member cannot be null") final User user,
                            @NotNull(value = "member cannot be null") final JDA jda) {
        super(null, user, server, jda);
    }

}
