package uk.sparkydiscordbot.api.event.server.member.moderation;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import uk.sparkydiscordbot.api.event.server.ServerEvent;

/**
 * Called whenever a moderation action is performed on a {@link User}
 *
 * <p>These actions include</p>
 * <ul>
 *     <li>Kick</li>
 *     <li>Ban</li>
 *     <li>Un-ban</li>
 * </ul>
 */
public class ModerationEvent extends ServerEvent {

    private final User issuer;
    private final User target;
    private final AuditLogEntry entry;

    public ModerationEvent(final Guild server,
                           final User issuer,
                           final User target,
                           final AuditLogEntry entry,
                           final JDA jda) {
        super(server, jda);
        this.issuer = issuer;
        this.target = target;
        this.entry = entry;
    }

    public User getUser() {
        return this.issuer;
    }

    public User getTarget() {
        return this.target;
    }

    public AuditLogEntry getAuditEntry() {
        return this.entry;
    }

}
