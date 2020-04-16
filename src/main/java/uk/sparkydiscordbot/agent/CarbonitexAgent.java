package uk.sparkydiscordbot.agent;

import net.dv8tion.jda.api.sharding.ShardManager;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.sparkydiscordbot.Bot;

import java.io.IOException;

public class CarbonitexAgent implements Runnable {

    private static final String URL = "https://www.carbonitex.net/discord/data/botdata.php";
    private static final Logger LOGGER = LoggerFactory.getLogger(CarbonitexAgent.class);

    private final ShardManager shardManager;
    private final String token;

    public CarbonitexAgent(@NotNull(value = "bot cannot be null") final Bot bot,
                           @NotNull(value = "key cannot be null") final String token) {
        this.shardManager = bot.getShardManager();
        this.token = token;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void run() {

        if (this.token != null && !this.token.trim().isEmpty()) {

            LOGGER.info("Submitting bot metrics to Carbonitex API...");

            final HttpUrl.Builder builder = HttpUrl.parse(URL).newBuilder();
            builder.addQueryParameter("key", this.token);
            builder.addQueryParameter("servercount", Long.toString(this.shardManager.getGuilds().size()));
            builder.addQueryParameter("shards", Long.toString(this.shardManager.getShards().size()));

            final Request request = new Request.Builder().url(builder.build()).build();

            try {

                final Response response = Bot.HTTP_CLIENT.newCall(request).execute();

                final int code = response.code();
                if (code == 200) {
                    LOGGER.info("Successfully submitted bot metrics to Carbonitex!");
                    LOGGER.info("Response(" + code + "): " + response.body().string());
                } else {
                    LOGGER.info("Failed to submit metrics to Carbonitex");
                    LOGGER.info("Server responded with status code: " + code);
                }

            } catch (final IOException ex) {
                LOGGER.info("An error occurred whilst submitting metrics to Carbonitex");
            }

        }

    }
}
