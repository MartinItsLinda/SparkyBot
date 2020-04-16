package uk.sparkydiscordbot.listeners;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.StatusChangeEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.sparkydiscordbot.Bot;
import uk.sparkydiscordbot.api.entities.messaging.Messaging;

import java.awt.*;

public class JdaStatusListener extends BotListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(JdaStatusListener.class);

    public JdaStatusListener(@NotNull(value = "bot cannot be null") final Bot bot) {
        super(bot);
    }

    @Override
    public void onStatusChange(final StatusChangeEvent event) {

        final JDA jda = event.getJDA();
        final JDA.Status oldStatus = event.getOldStatus();
        final JDA.Status newStatus = event.getNewStatus();

        switch (newStatus) {
            case INITIALIZING:
            case INITIALIZED:
            case LOGGING_IN:
            case LOADING_SUBSYSTEMS:
            case AWAITING_LOGIN_CONFIRMATION:
            case IDENTIFYING_SESSION:
            case FAILED_TO_LOGIN:
            case RECONNECT_QUEUED:
            case WAITING_TO_RECONNECT:
                return;
        }

        LOGGER.info(String.format("Status for shard %d changed from %s to %s (ping %d)", jda.getShardInfo().getShardId(), oldStatus.name(), newStatus.name(), jda.getGatewayPing()));

        final Color color;
        final String status;
        switch (newStatus) {
            case CONNECTING_TO_WEBSOCKET:
                status = "CONNECTING TO WEB SOCKET";
                color = Color.ORANGE;
                break;
            case ATTEMPTING_TO_RECONNECT:
                status = "ATTEMPTING TO RECONNECT";
                color = Color.ORANGE;
                break;
            case CONNECTED:
                status = "CONNECTED";
                color = Color.GREEN;
                break;
            case DISCONNECTED:
                status = "DISCONNECTED";
                color = Color.RED;
                break;
            case SHUTDOWN:
                status = "SHUTDOWN";
                color = Color.RED;
                break;
            default:
                return;
        }

        final EmbedBuilder embed = Messaging.getLocalEmbedBuilder();
        embed.setColor(color);
        embed.setTitle("Status Change");
        embed.setDescription(String.format("**Shard** %d/%d | **Status**: %s", jda.getShardInfo().getShardId(), jda.getShardInfo().getShardTotal(), status.toUpperCase()));

//        getWebhookClient().send(new WebhookMessageBuilder().addEmbeds(embed.build()).build());

    }

}
