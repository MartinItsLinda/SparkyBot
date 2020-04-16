package uk.sparkydiscordbot.api.event.server;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import uk.sparkydiscordbot.api.entities.event.Event;

public class ServerEvent extends Event {

    private final Guild server;
    private final JDA jda;

    /**
     * @param server The {@link Guild} this event was triggered in
     * @param jda    The {@link JDA} instance
     */
    public ServerEvent(final Guild server,
                       final JDA jda) {
        super(true);
        this.server = server;
        this.jda = jda;
    }

    /**
     * @return The {@link Guild} this event was triggered in
     */
    public Guild getServer() {
        return this.server;
    }

    /**
     * @return The {@link JDA} instance
     */
    public JDA getJDA() {
        return this.jda;
    }

}
