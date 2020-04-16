package uk.sparkydiscordbot.api.event.server.member.moderation;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

public class MemberKickEvent extends ModerationEvent {

    public MemberKickEvent(final Guild server,
                           final User issuer,
                           final User target,
                           final AuditLogEntry entry,
                           final JDA jda) {
        super(server, issuer, target, entry, jda);
    }

}
