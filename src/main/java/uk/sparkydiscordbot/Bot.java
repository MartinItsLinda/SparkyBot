package uk.sparkydiscordbot;

import com.google.gson.JsonParser;
import com.moandjiezana.toml.Toml;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.sparkydiscordbot.agent.CarbonitexAgent;
import uk.sparkydiscordbot.agent.DiscordBotListAgent;
import uk.sparkydiscordbot.api.entities.command.Command;
import uk.sparkydiscordbot.api.entities.command.RootCommandRegistry;
import uk.sparkydiscordbot.api.entities.economy.AccountRegistry;
import uk.sparkydiscordbot.api.entities.event.RootEventRegistry;
import uk.sparkydiscordbot.api.entities.language.I18n;
import uk.sparkydiscordbot.api.entities.metrics.HeartBeatTask;
import uk.sparkydiscordbot.api.entities.metrics.MetricsTask;
import uk.sparkydiscordbot.api.entities.module.Module;
import uk.sparkydiscordbot.api.entities.module.loader.ModuleLoader;
import uk.sparkydiscordbot.api.entities.scheduler.Scheduler;
import uk.sparkydiscordbot.api.entities.utils.Objects;
import uk.sparkydiscordbot.internal.command.CommandRegistryImpl;
import uk.sparkydiscordbot.internal.command.defaults.admin.*;
import uk.sparkydiscordbot.internal.economy.AccountRegistryImpl;
import uk.sparkydiscordbot.internal.event.EventRegistryImpl;
import uk.sparkydiscordbot.internal.module.ModuleLoaderImpl;
import uk.sparkydiscordbot.listeners.*;
import uk.sparkydiscordbot.net.server.NettyServer;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;

/**
 * The main class for the bot
 */
public class Bot {

    /**
     * Logger instance
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(Bot.class);

    /**
     * User-Agent header used for web queries
     */
    @SuppressWarnings("ConstantConditions")
    //Intellij recognises BotInfo.VERSION_MAJOR to never change so constant conditions suppresses the error
    public static final String USER_AGENT = BotInfo.BOT_NAME + (BotInfo.VERSION_MAJOR.startsWith("@") ? "" : " v" + BotInfo.VERSION);

    /**
     * A {@link JsonParser} instance
     */
    public static final JsonParser JSON_PARSER = new JsonParser();

    /**
     * A {@link MediaType}
     */
    public static final MediaType MEDIA_JSON = MediaType.parse("application/json;charset=utf8");

    /**
     * The {@link OkHttpClient} used for executing web requests
     */
    public static final OkHttpClient HTTP_CLIENT = new OkHttpClient.Builder().addInterceptor(chain -> {
        Request request = chain.request();

        if (request.header("User-Agent") == null) {
            request = request.newBuilder().header("User-Agent", Bot.USER_AGENT).build();
        }

        return chain.proceed(request);
    }).build();

    /**
     * The thread group used for threads created by {@link #CACHED_EXECUTOR_SERVICE}
     */
    private static final ThreadGroup THREAD_GROUP = new ThreadGroup("Executor-Thread-Group");

    /**
     * A convenience {@link BiFunction} to create a new {@link Thread} with the {@code threadName} and {@code threadExecutor}
     */
    private static final BiFunction<String, Runnable, Thread> THREAD_FUNCTION = (threadName, threadExecutor) -> new Thread(THREAD_GROUP, threadExecutor, threadName);

    /**
     * A single threaded {@link ScheduledExecutorService}.
     */
    public static final ScheduledExecutorService SINGLE_EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor(threadExecutor -> THREAD_FUNCTION.apply("Single Thread Service", threadExecutor));

    /**
     * A cached {@link ExecutorService}
     */
    public static final ExecutorService CACHED_EXECUTOR_SERVICE = Executors.newCachedThreadPool(threadExecutor -> THREAD_FUNCTION.apply("Cached Thread Service", threadExecutor));

    /**
     * The default port netty will use should no netty port be specified in the config file
     */
    private static final long DEFAULT_NETTY_PORT = 6016;

    private static Bot instance;

    static {
        THREAD_GROUP.setMaxPriority(Thread.NORM_PRIORITY);
    }

    private long startTime;
    private boolean running;
    private Toml config;
    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;
    private AccountRegistry accounts;
    private RootCommandRegistry commandRegistry;
    private RootEventRegistry eventRegistry;
    private ModuleLoader moduleLoader;
    private ShardManager shardManager;
    private NettyServer server;

    public static void main(final String[] args) {
        Bot bot = null;
        try {
            (bot = new Bot()).start(); //start in current directory
        } catch (final Throwable ex) {
            LOGGER.info("An error occurred during startup and the bot has to shutdown...");
            ex.printStackTrace();
            if (bot != null) {
                bot.shutdown();
            }
        }
    }

    private void start() throws IOException, LoginException, InterruptedException {
        Objects.checkArgument(!this.running, "bot is already running");

        Bot.instance = this;

        this.running = true;
        this.startTime = System.currentTimeMillis();

        final File file = new File("bot.toml");
        if (!file.exists()) {
            FileUtils.copyURLToFile(this.getClass().getResource("/bot.toml"), file); //config wasn't found so copy internal one (resources/bot.toml)
            LOGGER.info(String.format("bot.toml was created at %s.", file.getCanonicalPath()));
            System.exit(ExitCode.EXIT_CODE_NORMAL); //need to use System.exit() because if the starter is present it will error on restart
        } else {

            LOGGER.info("Loading configuration...");

            this.config = new Toml().read(file);

            LOGGER.info("Connecting to database...");

            final String user = this.config.getString("db.user");
            final String pass = this.config.getString("db.psswd");
            final String host = this.config.getString("db.host");
            final int port = Math.toIntExact(this.config.getLong("mongo.port"));

            if ((user == null || user.isEmpty()) || (pass == null || pass.isEmpty())) {
                LOGGER.info("MongoDB information is not configured, attempting to connect with no authentication...");
                this.mongoClient = new MongoClient(host, port);
            } else {
                this.mongoClient = new MongoClient(new MongoClientURI(String.format("mongodb://%s:%s@%s:%s", user, pass, host, port)));
            }

            LOGGER.info("Loading default database...");

            this.mongoDatabase = this.mongoClient.getDatabase(this.config.getString("mongo.database"));

            LOGGER.info("Loading data managers...");

            //just class initialization
            this.accounts = new AccountRegistryImpl();
            this.commandRegistry = new CommandRegistryImpl();
            this.eventRegistry = new EventRegistryImpl();
            this.moduleLoader = new ModuleLoaderImpl(this);

            LOGGER.info("Connecting to Discord API...");

            final DefaultShardManagerBuilder builder = DefaultShardManagerBuilder.create(
                        GatewayIntent.GUILD_MEMBERS,
                        GatewayIntent.GUILD_BANS,
                        GatewayIntent.GUILD_VOICE_STATES,
                        GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.GUILD_MESSAGE_REACTIONS
                    )
                    .setToken(this.config.getString("client.token"))
                    .setAutoReconnect(true)
                    .setEnableShutdownHook(false) //we dont use the shutdown hook on JDA as we handle that ourselves
                    .setActivity(Activity.of(Activity.ActivityType.DEFAULT, this.config.getString("client.game", "")))
                    .setBulkDeleteSplittingEnabled(false)
                    .setHttpClient(HTTP_CLIENT)
                    .setShardsTotal(Math.toIntExact(this.config.getLong("docker.shards-total")))
                    .setShards(Math.toIntExact(this.config.getLong("docker.shard-from")), Math.toIntExact(this.config.getLong("docker.shard-to")))
                    .disableCache(CacheFlag.ACTIVITY, CacheFlag.CLIENT_STATUS, CacheFlag.EMOTE, CacheFlag.MEMBER_OVERRIDES)
                    .addEventListeners(
                            new GuildMessageListener(this),
                            new GuildJoinQuitListener(this),
                            new GuildMemberListener(this),
                            new GuildMemberReactionListener(this),
                            new GuildMemberVoiceListener(this),
                            new GuildModerationListener(this),
                            new GuildRoleListener(this),
                            new GuildUpdateListener(this)
                    );

            LOGGER.info("Starting shards...");

            this.shardManager = builder.build();

            //Sleep until all shards are connected (shard manager builder doesn't have a buildBlocking method)
            while (this.shardManager.getShards().stream().anyMatch(shard -> shard.getStatus() != JDA.Status.CONNECTED)) {
                Thread.sleep(50L);
            }

            LOGGER.info("Registering default commands...");

            this.commandRegistry.registerCommand(Command.builder()
                    .names("buildinfo")
                    .executor(new BuildInfoCommand())
                    .build());

            this.commandRegistry.registerCommand(Command.builder()
                    .names("update")
                    .executor(new UpdateCommand(this))
                    .build());

            this.commandRegistry.registerCommand(Command.builder()
                    .names("restart")
                    .executor(new RestartCommand())
                    .build());

            this.commandRegistry.registerCommand(Command.builder()
                    .names("stop")
                    .executor(new StopCommand())
                    .build());

            LOGGER.info("Loading language files...");

            I18n.loanI18n(this.getClass());

            LOGGER.info("Loading modules...");

            //If there were no modules to load it just returns an empty list, so no harm done
            final Collection<Module> modules = this.moduleLoader.loadModules();
            modules.forEach(this.moduleLoader::enableModule);

            LOGGER.info(String.format("Loaded %d modules", this.moduleLoader.getEnabledModules().size()));

            if (this.config.getBoolean("server.use-netty", false)) {
                this.server = new NettyServer(Math.toIntExact(this.config.getLong("server.port", DEFAULT_NETTY_PORT)));
                CACHED_EXECUTOR_SERVICE.submit(this.server::start);
            }

            //Loading tasks

            if (Environment.getEnvironment().isRelease()) {
                LOGGER.info("Loading bot list tasks...");

                LOGGER.info("Starting Carbonitex task...");
                Scheduler.getInstance().runTaskRepeating(new CarbonitexAgent(this, this.config.getString("client.agent.carbon.key")), 0L, TimeUnit.MINUTES.toMillis(30));

                LOGGER.info("Starting Discord Bot List task...");
                Scheduler.getInstance().runTaskRepeating(new DiscordBotListAgent(this, this.config.getString("client.agent.dbl.key")), 0L, TimeUnit.MINUTES.toMillis(30));
            }

            LOGGER.info("Starting metrics...");
            Scheduler.getInstance().runTaskRepeating(new MetricsTask(this), 0L, TimeUnit.HOURS.toMillis(1));

            LOGGER.info("Starting heart beat...");
            Scheduler.getInstance().runTaskRepeating(new HeartBeatTask(this), 0L, TimeUnit.SECONDS.toMillis(15));

            LOGGER.info("Registering shutdown hook...");

            //register our shutdown hook so if something happens we get properly shutdown
            Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown, "Auto-Shutdown-Thread"));

            LOGGER.info(String.format("Startup completed! Took %dms, running api version: %s.", (System.currentTimeMillis() - this.startTime), BotInfo.VERSION));
        }
    }

    /*
     * Don't put a System.exit() in here otherwise a deadlock will happen
     * or an exception...depending if the shutdown method
     * was called via shutdown hook or manual call
     *
     * System.exit() -> fire shutdown hooks inside synchronized method ->
     * -> shutdown hook calls System.exit() -> re calls that synchronized
     * method -> waits infinitely as it never loses the synchronized lock
     */
    private void shutdown() {
        Objects.checkArgument(this.running, "bot not running");

        this.running = false;

        if (this.shardManager != null) {
            this.shardManager.shutdown();
        }

        if (this.moduleLoader != null) {
            this.moduleLoader.disableModules();
        }

        if (this.eventRegistry != null) {
            this.eventRegistry.unregisterAll();
        }

        if (this.commandRegistry != null) {
            this.commandRegistry.unregisterAll();
        }

        if (this.server != null) {
            this.server.stop();
        }

        if (this.mongoClient != null) {
            this.mongoClient.close();
        }

        Scheduler.getInstance().shutdown();

        Bot.CACHED_EXECUTOR_SERVICE.shutdownNow();
        Bot.SINGLE_EXECUTOR_SERVICE.shutdownNow();

        Bot.instance = null;
    }

    /**
     * @return The current instance
     */
    public static Bot getInstance() {
        return Bot.instance;
    }

    /**
     * @return The bot config
     */
    public Toml getConfig() {
        return this.config;
    }

    /**
     * @return The time of startup
     */
    public long getStartTime() {
        return this.startTime;
    }

    /**
     * @return The {@link MongoDatabase}
     */
    public MongoDatabase getMongoDatabase() {
        return this.mongoDatabase;
    }

    /**
     * @return The {@link AccountRegistry} impl
     */
    public AccountRegistry getAccounts() {
        return this.accounts;
    }

    /**
     * @return The {@link RootCommandRegistry} impl
     */
    public RootCommandRegistry getCommandRegistry() {
        return this.commandRegistry;
    }

    /**
     * @return The {@link RootEventRegistry} impl
     */
    public RootEventRegistry getEventRegistry() {
        return this.eventRegistry;
    }

    /**
     * @return The {@link ModuleLoader} impl
     */
    public ModuleLoader getModuleLoader() {
        return this.moduleLoader;
    }

    /**
     * @return The {@link ShardManager}
     */
    public ShardManager getShardManager() {
        return this.shardManager;
    }

}
