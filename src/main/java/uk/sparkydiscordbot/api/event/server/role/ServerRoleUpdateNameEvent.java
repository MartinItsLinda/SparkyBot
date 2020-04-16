package uk.sparkydiscordbot.api.event.server.role;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;

/**
 * Called whenever a {@link Role}s names is updated (changed)
 */
public class ServerRoleUpdateNameEvent extends ServerRoleEvent {

    private final String oldName;
    private final String newName;

    /**
     * @param server  The {@link Guild} the {@link Role} belongs too
     * @param role    The {@link Role} affected
     * @param oldName The old names of the {@link Role}
     * @param newName The new names of the {@link Role}
     * @param jda     The {@link JDA} instance
     */
    public ServerRoleUpdateNameEvent(final Guild server,
                                     final Role role,
                                     final String oldName,
                                     final String newName,
                                     final JDA jda) {
        super(server, role, jda);
        this.oldName = oldName;
        this.newName = newName;
    }

    /**
     * @return The old names of the {@link Role}
     */
    public String getOldName() {
        return this.oldName;
    }

    /**
     * @return The new names of the {@link Role}
     */
    public String getNewName() {
        return this.newName;
    }
}
