package uk.sparkydiscordbot.listeners;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.GuildBanEvent;
import net.dv8tion.jda.api.events.guild.GuildUnbanEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageUpdateEvent;
import org.jetbrains.annotations.NotNull;
import uk.sparkydiscordbot.Bot;
import uk.sparkydiscordbot.api.event.server.member.MemberLeaveEvent;
import uk.sparkydiscordbot.api.event.server.member.message.MemberMessageUpdateEvent;
import uk.sparkydiscordbot.api.event.server.member.message.MessageBulkDeleteEvent;
import uk.sparkydiscordbot.api.event.server.member.moderation.MemberBanEvent;
import uk.sparkydiscordbot.api.event.server.member.moderation.MemberKickEvent;
import uk.sparkydiscordbot.api.event.server.member.moderation.MemberPardonEvent;

import java.util.function.Consumer;

public class GuildModerationListener extends BotListener {

    public GuildModerationListener(@NotNull(value = "bot cannot be null") final Bot bot) {
        super(bot);
    }

    @Override
    public void onGuildMessageUpdate(final GuildMessageUpdateEvent event) {
        this.getEventManager().callEvent(new MemberMessageUpdateEvent(event.getGuild(), event.getMember(), event.getChannel(), event.getMessage(), event.getJDA()));
    }

    @Override
    public void onGuildBan(final GuildBanEvent event) {

        final Guild server = event.getGuild();
        final User user = event.getUser();

        this.findAuditLogEntry(server, user.getIdLong(), ActionType.BAN, audit -> {
            this.getEventManager().callEvent(new MemberBanEvent(server, audit.getUser(), user, audit, event.getJDA()));
        });

    }

    @Override
    public void onGuildUnban(final GuildUnbanEvent event) {

        final Guild server = event.getGuild();
        final User user = event.getUser();

        this.findAuditLogEntry(server, user.getIdLong(), ActionType.UNBAN, audit -> {
            this.getEventManager().callEvent(new MemberPardonEvent(server, audit.getUser(), user, audit, event.getJDA()));
        });

    }

    @Override
    public void onGuildMemberLeave(final GuildMemberLeaveEvent event) {

        final Guild server = event.getGuild();
        final User user = event.getUser();
        final JDA jda = event.getJDA();

        this.findAuditLogEntry(server, user.getIdLong(), ActionType.KICK, audit -> {
            this.getEventManager().callEvent(new MemberKickEvent(server, audit.getUser(), user, audit, jda));
        });

        this.getEventManager().callEvent(new MemberLeaveEvent(server, event.getUser(), jda));

    }

    @Override
    public void onMessageBulkDelete(final net.dv8tion.jda.api.events.message.MessageBulkDeleteEvent event) {
        this.getEventManager().callEvent(new MessageBulkDeleteEvent(event.getGuild(), event.getChannel(), event.getMessageIds(), event.getJDA()));

    }

    private void findAuditLogEntry(final Guild server,
                                   final long targetIdLong,
                                   final ActionType type,
                                   final Consumer<AuditLogEntry> auditEntry) {
        if (server.getSelfMember().hasPermission(Permission.VIEW_AUDIT_LOGS)) {
            server.retrieveAuditLogs().type(type).queue(entry -> entry.stream().filter(audit -> audit.getTargetIdLong() == targetIdLong).findFirst().ifPresent(auditEntry), __ -> {});
        }
    }

}
