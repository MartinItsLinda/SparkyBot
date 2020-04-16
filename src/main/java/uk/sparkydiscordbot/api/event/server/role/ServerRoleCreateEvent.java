package uk.sparkydiscordbot.api.event.server.role;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;

/**
 * Called when a {@link Role} is created in a {@link Guild}
 */
public class ServerRoleCreateEvent extends ServerRoleEvent {

    /**
     * @param server The {@link Guild} the {@link Role} was deleted from
     * @param role   The {@link Role} deleted
     * @param jda    The {@link JDA} instance
     */
    public ServerRoleCreateEvent(final Guild server,
                                 final Role role,
                                 final JDA jda) {
        super(server, role, jda);
    }

}
