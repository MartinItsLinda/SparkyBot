package uk.sparkydiscordbot.api.event.server;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a {@link Guild} changes its names
 */
public class ServerUpdateOwnerEvent extends ServerEvent {

    private final Member oldOwner;
    private final Member newOwner;

    /**
     * @param oldOwner The old owner
     * @param newOwner The new owner
     * @param server  The {@link Guild} that triggered this event
     * @param jda     The {@link JDA} instance
     */
    public ServerUpdateOwnerEvent(@NotNull(value = "old owner cannot be null") final Member oldOwner,
                                  @NotNull(value = "new owner cannot be null") final Member newOwner,
                                  @NotNull(value = "server cannot be null") final Guild server,
                                  @NotNull(value = "jda instance cannot be null") final JDA jda) {
        super(server, jda);
        this.oldOwner = oldOwner;
        this.newOwner = newOwner;
    }

    /**
     * @return The old owner
     */
    public Member getOldOwner() {
        return this.oldOwner;
    }

    /**
     * @return the new owner
     */
    public Member getNewOwner() {
        return this.newOwner;
    }
}
