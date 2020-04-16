package uk.sparkydiscordbot.api.entities.metrics;

import com.google.common.collect.Lists;
import com.mongodb.client.MongoCollection;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDA.Status;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.sparkydiscordbot.Bot;
import uk.sparkydiscordbot.api.entities.module.loader.ModuleLoader;
import uk.sparkydiscordbot.util.MongoUtils;

import java.util.List;

public class HeartBeatTask implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(HeartBeatTask.class);

    private final ShardManager shardManager;
    private final MongoCollection<Document> heartbeats;
    private final Object lock;

    public HeartBeatTask(final Bot bot) {
        this.shardManager = bot.getShardManager();
        this.heartbeats = MongoUtils.getCollectionByName("heartbeats", bot.getMongoDatabase());
        this.lock = new Object();
    }

    @Override
    public void run() {
        synchronized (this.lock) {
            try {
                this.submit();
            } catch (final Exception ex) {
                LOGGER.info("An error occurred whilst performing heartbeat", ex);
            }
        }
    }

    private void submit() {

        //this is just to keep record of alive / down shards on the site

        final Document document = new Document();

        document.put("time_ms", System.currentTimeMillis());

        document.put("shards_total", this.shardManager.getShardsTotal());
        document.put("shards_connected", this.shardManager.getShards().stream().filter(jda -> jda.getStatus() == Status.CONNECTED).count());

        final List<Document> shards = Lists.newArrayList();
        for (final JDA shard : this.shardManager.getShards()) {

            final Document sdoc = new Document();
            sdoc.put("shard_id", shard.getShardInfo().getShardId());
            sdoc.put("ping", shard.getGatewayPing());
            sdoc.put("status", shard.getStatus().name());

            shards.add(sdoc);

        }

        document.put("shards", shards);

        this.heartbeats.insertOne(document);

    }

}
