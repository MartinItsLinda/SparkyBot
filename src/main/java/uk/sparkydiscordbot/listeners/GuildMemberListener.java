package uk.sparkydiscordbot.listeners;

import net.dv8tion.jda.api.events.guild.member.*;
import org.jetbrains.annotations.NotNull;
import uk.sparkydiscordbot.Bot;
import uk.sparkydiscordbot.api.event.server.member.MemberJoinEvent;
import uk.sparkydiscordbot.api.event.server.member.MemberLeaveEvent;
import uk.sparkydiscordbot.api.event.server.member.MemberNicknameChangeEvent;
import uk.sparkydiscordbot.api.event.server.member.role.MemberRolesAddedEvent;
import uk.sparkydiscordbot.api.event.server.member.role.MemberRolesRemovedEvent;

public class GuildMemberListener extends BotListener {

    public GuildMemberListener(@NotNull(value = "parent cannot be null") final Bot bot) {
        super(bot);
    }

    @Override
    public void onGuildMemberJoin(final GuildMemberJoinEvent event) {
        this.getEventManager().callEvent(new MemberJoinEvent(event.getGuild(), event.getMember(), event.getJDA()));
    }

    @Override
    public void onGuildMemberLeave(final GuildMemberLeaveEvent event) {
        this.getEventManager().callEvent(new MemberLeaveEvent(event.getGuild(), event.getUser(), event.getJDA()));
    }

    @Override
    public void onGuildMemberNickChange(final GuildMemberNickChangeEvent event) {
        this.getEventManager().callEvent(new MemberNicknameChangeEvent(event.getMember(), event.getGuild(), event.getPrevNick(), event.getNewNick(), event.getJDA()));
    }

    @Override
    public void onGuildMemberRoleAdd(final GuildMemberRoleAddEvent event) {
        this.getEventManager().callEvent(new MemberRolesAddedEvent(event.getMember(), event.getGuild(), event.getRoles(), event.getJDA()));
    }

    @Override
    public void onGuildMemberRoleRemove(final GuildMemberRoleRemoveEvent event) {
        this.getEventManager().callEvent(new MemberRolesRemovedEvent(event.getMember(), event.getGuild(), event.getRoles(), event.getJDA()));
    }

}
