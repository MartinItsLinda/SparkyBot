package uk.sparkydiscordbot.api.event.server.member;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;
import uk.sparkydiscordbot.api.event.server.ServerEvent;

public class MemberEvent extends ServerEvent {

    private final Member member;
    private final User user;
    private final JDA jda;

    /**
     * @param member The {@link Member} responsible for this event
     * @param user   The {@link User} responsible for this event, this is because some JDA events don't get a {@link Member} object
     * @param server The {@link Guild} this event was triggered in
     * @param jda    The {@link JDA} instance
     */
    public MemberEvent(final Member member,
                       @NotNull(value = "member cannot be null") final User user,
                       @NotNull(value = "server cannot be null") final Guild server,
                       @NotNull(value = "jda cannot be null") final JDA jda) {
        super(server, jda);
        this.member = member;
        this.user = user;
        this.jda = jda;
    }

    /**
     * @return The {@link User} responsible for this event
     */
    public User getUser() {
        return this.user;
    }

    /**
     * @return The {@link Member} responsible for this event
     */
    public Member getMember() {
        return this.member;
    }

}
