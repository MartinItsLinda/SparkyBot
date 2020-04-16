package uk.sparkydiscordbot.api.event.server.role;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;

/**
 * Called when a {@link Role} is deleted from a {@link Guild}
 */
public class ServerRoleDeleteEvent extends ServerRoleEvent {

    /**
     * @param server The {@link Guild} the {@link Role} was deleted from
     * @param role   The {@link Role} deleted
     * @param jda    The {@link JDA} instance
     */
    public ServerRoleDeleteEvent(final Guild server,
                                 final Role role,
                                 final JDA jda) {
        super(server, role, jda);
    }

}
