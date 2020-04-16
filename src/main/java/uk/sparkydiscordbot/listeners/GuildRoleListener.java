package uk.sparkydiscordbot.listeners;

import net.dv8tion.jda.api.events.role.RoleCreateEvent;
import net.dv8tion.jda.api.events.role.RoleDeleteEvent;
import net.dv8tion.jda.api.events.role.update.RoleUpdateColorEvent;
import net.dv8tion.jda.api.events.role.update.RoleUpdateNameEvent;
import net.dv8tion.jda.api.events.role.update.RoleUpdatePositionEvent;
import org.jetbrains.annotations.NotNull;
import uk.sparkydiscordbot.Bot;
import uk.sparkydiscordbot.api.event.server.role.*;

public class GuildRoleListener extends BotListener {

    public GuildRoleListener(@NotNull(value = "bot cannot be null") final Bot bot) {
        super(bot);
    }

    @Override
    public void onRoleCreate(final RoleCreateEvent event) {
        this.getEventManager().callEvent(new ServerRoleCreateEvent(event.getGuild(), event.getRole(), event.getJDA()));
    }

    @Override
    public void onRoleDelete(final RoleDeleteEvent event) {
        this.getEventManager().callEvent(new ServerRoleDeleteEvent(event.getGuild(), event.getRole(), event.getJDA()));
    }

    @Override
    public void onRoleUpdateColor(final RoleUpdateColorEvent event) {
        this.getEventManager().callEvent(new ServerRoleUpdateColorEvent(event.getGuild(), event.getRole(), event.getOldColor(), event.getNewColor(), event.getJDA()));
    }

    @Override
    public void onRoleUpdateName(final RoleUpdateNameEvent event) {
        this.getEventManager().callEvent(new ServerRoleUpdateNameEvent(event.getGuild(), event.getRole(), event.getOldName(), event.getNewName(), event.getJDA()));
    }

    @Override
    public void onRoleUpdatePosition(final RoleUpdatePositionEvent event) {
        this.getEventManager().callEvent(new ServerRoleUpdatePositionEvent(event.getGuild(), event.getRole(), event.getOldPosition(), event.getNewPosition(), event.getJDA()));
    }



}
