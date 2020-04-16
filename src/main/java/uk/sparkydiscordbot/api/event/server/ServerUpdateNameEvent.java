package uk.sparkydiscordbot.api.event.server;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a {@link Guild} changes its names
 */
public class ServerUpdateNameEvent extends ServerEvent {

    private final String oldName;
    private final String newName;

    /**
     * @param oldName The old names
     * @param newName The new names
     * @param server  The {@link Guild} that triggered this event
     * @param jda     The {@link JDA} instance
     */
    public ServerUpdateNameEvent(@NotNull(value = "old names cannot be null") final String oldName,
                                 @NotNull(value = "new names cannot be null") final String newName,
                                 @NotNull(value = "server cannot be null") final Guild server,
                                 @NotNull(value = "jda instance cannot be null") final JDA jda) {
        super(server, jda);
        this.oldName = oldName;
        this.newName = newName;
    }

    /**
     * @return The old names of the {@link Guild}
     */
    public String getOldName() {
        return this.oldName;
    }

    /**
     * @return The new names of the {@link Guild}
     */
    public String getNewName() {
        return this.newName;
    }
}
