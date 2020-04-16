package uk.sparkydiscordbot.api.event.server.member.role;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import uk.sparkydiscordbot.api.event.server.member.MemberEvent;

import java.util.Collection;

public class MemberRoleEvent extends MemberEvent {

    private final Collection<Role> roles;

    public MemberRoleEvent(final Member member,
                           final Guild server,
                           final Collection<Role> roles,
                           final JDA jda) {
        super(member, member.getUser(), server, jda);
        this.roles = roles;
    }

    public Collection<Role> getRoles() {
        return this.roles;
    }
}
