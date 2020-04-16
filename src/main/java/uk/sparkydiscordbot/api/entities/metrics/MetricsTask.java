package uk.sparkydiscordbot.api.entities.metrics;

import com.google.common.collect.Lists;
import com.mongodb.client.MongoCollection;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.sparkydiscordbot.Bot;
import uk.sparkydiscordbot.api.entities.module.Metrics;
import uk.sparkydiscordbot.api.entities.module.Metrics.Graph;
import uk.sparkydiscordbot.api.entities.module.Module;
import uk.sparkydiscordbot.api.entities.module.ModuleData;
import uk.sparkydiscordbot.api.entities.module.loader.ModuleLoader;
import uk.sparkydiscordbot.util.DateUtils;
import uk.sparkydiscordbot.util.MongoUtils;

import java.util.List;
import java.util.Map;

public class MetricsTask implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(MetricsTask.class);

    private final ModuleLoader moduleLoader;
    private final ShardManager shardManager;
    private final MongoCollection<Document> metrics;
    private final Object lock;

    public MetricsTask(final Bot bot) {
        this.moduleLoader = bot.getModuleLoader();
        this.shardManager = bot.getShardManager();
        this.metrics = MongoUtils.getCollectionByName("metrics");
        this.lock = new Object();
    }

    @Override
    public void run() {
        synchronized (this.lock) {
            try {
                this.submit();
            } catch (final Exception ex) {
                LOGGER.info("An error occurred whilst submitting statistics", ex);
            }
        }
    }

    private void submit() throws Exception {

        final Document document = new Document();

        document.put("time_ms", System.currentTimeMillis());
        document.put("memory_used_mb", ServerInfo.getUsedMemory());
        document.put("memory_total_mb", ServerInfo.getTotalMemory());
        document.put("memory_max_mb", ServerInfo.getMaxMemory());
        document.put("threads", ServerInfo.getTotalThreads());
        document.put("cores", ServerInfo.getAvailableProcessors());
        document.put("process_cpu_load", ServerInfo.getProcessCpuLoad());
        document.put("system_cpu_load", ServerInfo.getSystemCpuLoad());
        document.put("disk_used", ServerInfo.getDiskUsage());
        document.put("disk_size", ServerInfo.getDiskSize());

        document.put("guilds", this.shardManager.getGuilds().size());
        document.put("members", this.shardManager.getUsers().size());
        document.put("roles", this.shardManager.getRoles().size());
        document.put("text_channels", this.shardManager.getTextChannels().size());
        document.put("voice_channels", this.shardManager.getVoiceChannels().size());

        document.put("modules_loaded", this.moduleLoader.getModules().size());
        document.put("modules_enabled", this.moduleLoader.getEnabledModules().size());

        final List<Document> modules = Lists.newArrayList();

        for (final Module module : this.moduleLoader.getEnabledModules()) {

            final Document mdoc = new Document();

            final ModuleData data = module.getData();
            mdoc.put("names", data.name());
            mdoc.put("version", data.version());

            final Document graphs = new Document();

            final Metrics metrics = module.getMetrics();
            for (final Graph graph : metrics.getGraphs()) {

                final Map<String, Integer> result = graph.getCallable().call();

                final Document gdoc = new Document();
                for (final Map.Entry<String, Integer> gr : result.entrySet()) {
                    gdoc.put(gr.getKey().replaceAll(" ", "_").toLowerCase(), gr.getValue());
                }

                graphs.put(graph.getName(), gdoc);

            }

            mdoc.put("graphs", graphs);

            modules.add(mdoc);

        }

        document.put("modules", modules);

        this.metrics.insertOne(document);

        LOGGER.info(String.format("Metrics submitted at %s", DateUtils.generateTimestamp("hh:mm:ss.sss")));

    }

}
