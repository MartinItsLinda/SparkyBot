package uk.sparkydiscordbot.api.event.server;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a {@link Guild} adding the bot.
 * <p>
 * <p>This event can be triggered even when the
 * {@link Guild} was already known of by the bot
 * Due to Discord sometimes firing join events
 * in mass outages.</p>
 * <p>
 * <p>As a result, please call {@link #isNewGuild()} before
 * doing anything that would be specific to new {@link Guild}s
 * to avoid creating duplicate data.</p>
 */
public class ServerJoinEvent extends ServerEvent {

    private final boolean isNew;

    /**
     * @param server The {@link Guild} joined
     * @param jda    The {@link JDA} instance
     * @param isNew  Whether this is a new {@link Guild}
     */
    public ServerJoinEvent(@NotNull(value = "guild cannot be null") final Guild server,
                           @NotNull(value = "jda cannot be null") final JDA jda,
                           final boolean isNew) {
        super(server, jda);
        this.isNew = isNew;
    }

    /**
     * Return whether or not the guild is new to the bot or if it
     * was already known.
     *
     * @return {@code true} if this is a new {@link Guild}
     */
    public boolean isNewGuild() {
        return this.isNew;
    }

}
