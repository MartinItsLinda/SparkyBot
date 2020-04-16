package uk.sparkydiscordbot.listeners;

import net.dv8tion.jda.api.events.guild.voice.*;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.sparkydiscordbot.Bot;
import uk.sparkydiscordbot.api.event.server.member.voice.*;
import uk.sparkydiscordbot.api.event.server.voice.VoiceChannelCreateEvent;
import uk.sparkydiscordbot.api.event.server.voice.VoiceChannelDeleteEvent;

public class GuildMemberVoiceListener extends BotListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(GuildMemberVoiceListener.class);

    public GuildMemberVoiceListener(@NotNull(value = "parent cannot be null") final Bot bot) {
        super(bot);
    }

    @Override
    public void onGuildVoiceJoin(final GuildVoiceJoinEvent event) {
        this.getEventManager().callEvent(new MemberVoiceJoinEvent(event.getMember(), event.getGuild(), event.getChannelJoined(), event.getJDA()));

    }

    @Override
    public void onGuildVoiceMove(final GuildVoiceMoveEvent event) {
        this.getEventManager().callEvent(new MemberVoiceMoveEvent(event.getMember(), event.getGuild(), event.getChannelJoined(), event.getChannelLeft(), event.getJDA()));
    }

    @Override
    public void onGuildVoiceLeave(final GuildVoiceLeaveEvent event) {
        this.getEventManager().callEvent(new MemberVoiceLeaveEvent(event.getMember(), event.getGuild(), event.getChannelLeft(), event.getJDA()));
    }

    @Override
    public void onGuildVoiceMute(final GuildVoiceMuteEvent event) {
        this.getEventManager().callEvent(new MemberVoiceMuteEvent(event.getMember(), event.getGuild(), event.getVoiceState().getChannel(), event.getJDA()));
    }

    @Override
    public void onGuildVoiceDeafen(final GuildVoiceDeafenEvent event) {
        this.getEventManager().callEvent(new MemberVoiceDeafenEvent(event.getMember(), event.getGuild(), event.getVoiceState().getChannel(), event.getJDA()));
    }

    @Override
    public void onVoiceChannelCreate(final net.dv8tion.jda.api.events.channel.voice.VoiceChannelCreateEvent event) {
        this.getEventManager().callEvent(new VoiceChannelCreateEvent(event.getGuild(), event.getChannel(), event.getJDA()));
    }

    @Override
    public void onVoiceChannelDelete(final net.dv8tion.jda.api.events.channel.voice.VoiceChannelDeleteEvent event) {
        this.getEventManager().callEvent(new VoiceChannelDeleteEvent(event.getGuild(), event.getChannel(), event.getJDA()));
    }
}
