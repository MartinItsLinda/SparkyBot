package uk.sparkydiscordbot.api.event.server.role;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import uk.sparkydiscordbot.api.event.server.ServerEvent;

public class ServerRoleEvent extends ServerEvent {

    private final Guild server;
    private final Role role;
    private final JDA jda;

    /**
     * @param server The {@link Guild} the {@link Role} was deleted from
     * @param role   The {@link Role} deleted
     * @param jda    The {@link JDA} instance
     */
    public ServerRoleEvent(final Guild server,
                           final Role role,
                           final JDA jda) {
        super(server, jda);
        this.server = server;
        this.role = role;
        this.jda = jda;
    }

    /**
     * @return The {@link Role} affected
     */
    public Role getRole() {
        return this.role;
    }

}
