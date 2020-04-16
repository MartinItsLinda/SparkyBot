package uk.sparkydiscordbot.listeners;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import org.jetbrains.annotations.NotNull;
import uk.sparkydiscordbot.Bot;
import uk.sparkydiscordbot.api.event.server.member.message.MemberReactionAddEvent;
import uk.sparkydiscordbot.api.event.server.member.message.MemberReactionRemoveEvent;

import java.util.function.Consumer;

public class GuildMemberReactionListener extends BotListener {

    public GuildMemberReactionListener(@NotNull(value = "bot cannot be null") final Bot bot) {
        super(bot);
    }

    @Override
    public void onMessageReactionAdd(final MessageReactionAddEvent event) {

        final Member member = event.getMember();
        final Guild guild = event.getGuild();
        final TextChannel channel = event.getTextChannel();

        if (!guild.getSelfMember().hasPermission(channel, Permission.MESSAGE_READ)) {
            return;
        }

        final MessageReaction reaction = event.getReaction();

        final Consumer<Message> consumer = (message) -> this.getEventManager().callEvent(new MemberReactionAddEvent(guild, member, channel, message, reaction, event.getJDA()));

        event.getChannel().getMessageById(event.getMessageId()).queue(consumer, __ -> {});
    }

    @Override
    public void onMessageReactionRemove(final MessageReactionRemoveEvent event) {

        final Guild guild = event.getGuild();
        final Member member = event.getMember();
        final TextChannel channel = event.getTextChannel();
        final MessageReaction reaction = event.getReaction();

        if (!guild.getSelfMember().hasPermission(channel, Permission.MESSAGE_READ)) {
            return;
        }

        final Consumer<Message> consumer = (message) -> this.getEventManager().callEvent(new MemberReactionRemoveEvent(guild, member, channel, message, reaction, event.getJDA()));

        event.getChannel().getMessageById(event.getMessageId()).queue(consumer, __ -> {});
    }

}
