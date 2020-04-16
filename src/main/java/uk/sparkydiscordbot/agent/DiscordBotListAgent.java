package uk.sparkydiscordbot.agent;

import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.JDA;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.sparkydiscordbot.Bot;

import java.io.IOException;

public class DiscordBotListAgent implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(DiscordBotListAgent.class);
    private static final String URL = "https://discordbots.org/api/bots/329668530926780426/stats";

    private final ShardManager shardManager;
    private final String token;

    public DiscordBotListAgent(@NotNull(value = "bot cannot be null") final Bot bot,
                               @NotNull(value = "token cannot be null") final String token) {
        this.shardManager = bot.getShardManager();
        this.token = token;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void run() {

        if (this.token != null && !this.token.trim().isEmpty()) {

            LOGGER.info("Submitting bot metrics to Discord Bot List API...");

            for (final JDA jda : this.shardManager.getShards()) {

                final JDA.ShardInfo info = jda.getShardInfo();

                final JSONObject data = new JSONObject();
                data.put("shard_id", info.getShardId());
                data.put("shard_count", info.getShardTotal());
                data.put("server_count", jda.getGuilds().size());

                final RequestBody body = RequestBody.create(Bot.MEDIA_JSON, data.toString());

                final Request request = new Request.Builder()
                        .url(URL)
                        .addHeader("User-Agent", Bot.USER_AGENT)
                        .addHeader("Authorization", this.token)
                        .post(body)
                        .build();

                try {

                    final Response response = Bot.HTTP_CLIENT.newCall(request).execute();

                    final int code = response.code();
                    if (code == 200) {
                        LOGGER.info("Successfully submitted bot metrics to Discord Bot List!");
                        LOGGER.info("Response(" + code + "): " + response.body().string());
                    } else {
                        LOGGER.info("Failed to submit metrics to Discord Bot List");
                        LOGGER.info("Server responded with status code: " + code);
                    }

                } catch (final IOException e) {
                    e.printStackTrace();
                }

            }

        }

    }

}
