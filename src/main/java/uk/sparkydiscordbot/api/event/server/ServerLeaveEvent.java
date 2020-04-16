package uk.sparkydiscordbot.api.event.server;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;

public class ServerLeaveEvent extends ServerEvent {

    /**
     * @param server The {@link Guild} left
     */
    public ServerLeaveEvent(@NotNull(value = "guild cannot be null") final Guild server,
                            @NotNull(value = "jda cannot be null") final JDA jda) {
        super(server, jda);
    }

}
