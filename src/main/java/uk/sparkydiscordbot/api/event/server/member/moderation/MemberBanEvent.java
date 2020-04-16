package uk.sparkydiscordbot.api.event.server.member.moderation;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

/**
 * Called whenever a {@link User} is banned from a {@link Guild}
 */
public class MemberBanEvent extends ModerationEvent {

    /**
     * @param server The {@link Guild} this event was triggered in
     * @param issuer The {@link User} issuer of this event
     * @param target The {@link User} target of this event
     * @param entry  The {@link AuditLogEntry}
     * @param jda    The {@link JDA} instance
     */
    public MemberBanEvent(final Guild server,
                          final User issuer,
                          final User target,
                          final AuditLogEntry entry,
                          final JDA jda) {
        super(server, issuer, target, entry, jda);
    }

}
