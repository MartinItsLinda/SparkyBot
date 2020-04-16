package uk.sparkydiscordbot.api.event.server.role;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;

import java.awt.*;

/**
 * Called when a {@link Role}s color is updated
 */
public class ServerRoleUpdateColorEvent extends ServerRoleEvent {

    private final Color oldColor;
    private final Color newColor;

    /**
     * @param server   The {@link Guild} the {@link Role} belongs too
     * @param role     The {@link Role} affected by this action
     * @param oldColor The old {@link Color} of the {@link Role}
     * @param newColor The new {@link Color} of the {@link Role}
     * @param jda      The {@link JDA} instance
     */
    public ServerRoleUpdateColorEvent(final Guild server,
                                      final Role role,
                                      final Color oldColor,
                                      final Color newColor,
                                      final JDA jda) {
        super(server, role, jda);
        this.oldColor = oldColor;
        this.newColor = newColor;
    }

    /**
     * @return The old {@link Role} color
     */
    public Color getOldColor() {
        return this.oldColor;
    }

    /**
     * @return The new {@link Role} color
     */
    public Color getNewColor() {
        return this.newColor;
    }
}
