package uk.sparkydiscordbot.api.event.server.voice;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.VoiceChannel;
import org.jetbrains.annotations.NotNull;

public class VoiceChannelCreateEvent extends VoiceEvent {

    public VoiceChannelCreateEvent(@NotNull(value = "guild cannot be null") final Guild guild,
                                   @NotNull(value = "channel cannot be null") final VoiceChannel channel,
                                   @NotNull(value = "jda cannot be null") final JDA jda) {
        super(guild, channel, jda);
    }

}
