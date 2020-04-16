package uk.sparkydiscordbot.listeners;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.sparkydiscordbot.Bot;
import uk.sparkydiscordbot.api.event.server.ServerJoinEvent;
import uk.sparkydiscordbot.api.event.server.ServerLeaveEvent;
import uk.sparkydiscordbot.util.MongoUtils;

public class GuildJoinQuitListener extends BotListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(GuildJoinQuitListener.class);

    private final MongoCollection<Document> servers;
    public GuildJoinQuitListener(@NotNull(value = "parent cannot be null") final Bot bot) {
        super(bot);
        this.servers = MongoUtils.getCollectionByName("servers", bot.getMongoDatabase());
    }

    @Override
    public void onGuildReady(final GuildReadyEvent event) {

        final Guild guild = event.getGuild();
        final JDA jda = event.getJDA();

        final boolean isNew = (this.servers.count(Filters.eq("server_id", guild.getId())) > 0);

        if (!isNew) {

            LOGGER.info(String.format("Joined guild %s(%s)", guild.getName(), guild.getId()));

            final Document document = new Document();
            document.put("server_id", guild.getId());
            document.put("server_name", guild.getName());
            document.put("server_avatar", guild.getIconUrl());
            document.put("server_owner", guild.getOwnerId());
            document.put("server_region", guild.getRegion().getName());
            document.put("server_region_id", guild.getRegion().getKey());
            document.put("server_is_vip", guild.getRegion().isVip());

            final VoiceChannel afk = guild.getAfkChannel();
            if (afk == null) {
                document.put("server_afk_channel", null);
            } else {
                document.put("server_afk_channel", afk.getId());
            }

            document.put("server_afk_timeout", guild.getAfkTimeout().getSeconds());

            final TextChannel system = guild.getSystemChannel();
            if (system == null) {
                document.put("server_system_channel", null);
            } else {
                document.put("server_system_channel", system.getId());
            }

            //noinspection ConstantConditions
            document.put("server_default_channel", guild.getDefaultChannel().getId());

            document.put("server_verification_level", guild.getVerificationLevel().getKey());
            document.put("server_mfa_level", guild.getRequiredMFALevel().getKey());
            document.put("server_explicit_content_level", guild.getExplicitContentLevel().getKey());

            this.servers.insertOne(document);

        }

        this.getEventManager().callEvent(new ServerJoinEvent(guild, event.getJDA(), isNew));

    }

    @Override
    public void onGuildLeave(final GuildLeaveEvent event) {

        final Guild server = event.getGuild();

        LOGGER.info(String.format("Left guild %s(%s)", server.getName(), server.getId()));

        this.servers.deleteOne(Filters.eq("server_id", server.getId()));

        this.getEventManager().callEvent(new ServerLeaveEvent(server, event.getJDA()));
    }

}
