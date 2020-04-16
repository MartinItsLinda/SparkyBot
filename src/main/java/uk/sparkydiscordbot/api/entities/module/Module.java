package uk.sparkydiscordbot.api.entities.module;

import com.moandjiezana.toml.Toml;
import com.mongodb.client.MongoDatabase;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.sparkydiscordbot.api.entities.economy.AccountRegistry;
import uk.sparkydiscordbot.api.entities.event.RootEventRegistry;
import uk.sparkydiscordbot.api.entities.language.I18n;
import uk.sparkydiscordbot.api.entities.scheduler.Scheduler;
import uk.sparkydiscordbot.api.entities.utils.Objects;
import uk.sparkydiscordbot.api.entities.command.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class Module implements CommandExecutor {

    private final ModuleData data;

    private Logger logger;
    private CommandRegistry commandRegistry;
    private EventRegistry eventRegistry;
    private SchedulerRegistry schedulerRegistry;
    private Metrics metrics;
    private AccountRegistry accounts;
    private MongoDatabase database;
    private File dataFolder;
    private File jar;

    private Toml config;
    private EventWaiter eventWaiter;

    private boolean enabled;

    public Module() {
        //no ModuleData annotation...this is a prerequisite for a Module so we cannot loanI18n it
        if (!getClass().isAnnotationPresent(ModuleData.class)) {
            throw new IllegalArgumentException("Class is not annotated with ModuleData");
        }
        this.data = this.getClass().getAnnotation(ModuleData.class);
    }

    //this is called in the module loader, making it final prevents it being overridden
    public final void init(@NotNull(value = "command manager cannot be null") final RootCommandRegistry rootCommandRegistry,
                           @NotNull(value = "event manager cannot be null") final RootEventRegistry rootEventRegistry,
                           @NotNull(value = "accounts cannot be null") final AccountRegistry accounts,
                           @NotNull(value = "database cannot be null") final MongoDatabase database,
                           @NotNull(value = "data folder cannot be null") final File dataFolder,
                           @NotNull(value = "jar cannot be null") final File jar) {
        this.logger = LoggerFactory.getLogger(this.data.name());
        this.commandRegistry = new CommandRegistry(rootCommandRegistry, this);
        this.eventRegistry = new EventRegistry(rootEventRegistry, this);
        this.schedulerRegistry = new SchedulerRegistry();
        this.metrics = new Metrics(this);
        this.accounts = accounts;
        this.database = database;
        this.dataFolder = dataFolder;
        this.jar = jar;

        //Register locale sources
        I18n.loanI18n(this.getClass());
    }

    /**
     * Called on {@link Module} enable
     */
    public void onEnable() {

    }

    /**
     * Called on {@link Module} disable
     */
    public void onDisable() {

    }

    /**
     * @return The {@link ModuleData}
     */
    public ModuleData getData() {
        return this.data;
    }

    /**
     * @return This {@link Module}'s names and version
     */
    public String getFullName() {
        return this.data.name() + " v" + this.data.version();
    }

    /**
     * @return The generated {@link Logger} for this {@link Module}
     */
    public Logger getLogger() {
        return this.logger;
    }

    /**
     * @return This {@link Module}s {@link CommandRegistry}
     */
    public CommandRegistry getCommandRegistry() {
        return this.commandRegistry;
    }

    /**
     * @return The {@link EventRegistry}
     */
    public EventRegistry getEventRegistry() {
        return this.eventRegistry;
    }

    /**
     * @return The {@link EventWaiter}
     */
    public EventWaiter getEventWaiter() {
        return this.getEventWaiter(true);
    }

    /**
     * @param init Whether to initialize the {@link EventWaiter} if not already.
     *
     * @return The {@link EventWaiter}, or {@code null} if the {@link EventWaiter} is
     * not initialized and {@code init} is {@code false}
     */
    public EventWaiter getEventWaiter(final boolean init) {
        Objects.checkArgument(this.isEnabled(), "cannot retrieve event waiter whilst not enabled");

        //this can only be requested whilst the module is enabled (because you cannot register events whilst not enabled)
        //and as such cannot be called in Module.init

        EventWaiter result = this.eventWaiter;
        if (result == null && init) { //https://en.wikipedia.org/wiki/Double-checked_locking
            synchronized (this) {
                result = this.eventWaiter;
                if (result == null) {
                    this.eventWaiter = result = new EventWaiter(this.eventRegistry);
                }
            }
        }

        return result;
    }

    /**
     * @return The {@link Scheduler}
     */
    public SchedulerRegistry getScheduler() {
        return this.schedulerRegistry;
    }

    /**
     * @return The {@link Metrics} for this {@link Module}
     */
    public Metrics getMetrics() {
        return this.metrics;
    }

    /**
     * @return The {@link AccountRegistry}
     */
    public AccountRegistry getAccounts() {
        return this.accounts;
    }

    /**
     * @return The {@link MongoDatabase}
     */
    public MongoDatabase getMongoDatabase() {
        return this.database;
    }

    /**
     * @return This {@link Module}s data folder
     */
    public File getDataFolder() {
        return this.dataFolder;
    }

    /**
     * @return The jar file
     */
    public File getJar() {
        return this.jar;
    }

    /**
     * @return This {@link Module}s configuration file.
     */
    public Toml getConfig() {
        if (this.config == null) {
            final File destination = new File(this.dataFolder, "config.toml");
            final URL internal = this.getClass().getResource("/config.toml");

            if (internal == null) {
                throw new IllegalArgumentException("Module does not contain 'config.toml'");
            }

            if (!destination.exists()) {
                try {
                    FileUtils.copyURLToFile(internal, destination);
                } catch (final IOException ex) {
                    throw new IllegalStateException(String.format("Couldn't copy internal file config.toml to %s: %s",
                            destination.getAbsolutePath(), ex.getMessage()));
                }
            }

            this.config = new Toml().read(destination);
        }
        return this.config;
    }

    /**
     * @return {@code true} if this {@link Module} is enabled, {@code false} otherwise
     */
    public boolean isEnabled() {
        return this.enabled;
    }

    /**
     * Sets this {@link Module}'s current enabled status.
     *
     * @param enabled The new enabled status of this {@link Module}
     *
     * @see Module#onEnable()
     * @see Module#onDisable()
     */
    public void setEnabled(final boolean enabled) {
        if (this.enabled != enabled) {
            this.enabled = enabled;
            if (this.enabled) {
                this.onEnable();
            } else {
                this.onDisable();
            }
        }
    }

    /**
     * @param context   The {@link CommandContext} to handle
     * @param arguments The {@link Arguments} passed with the {@link CommandContext}
     *
     * @return {@code true} if this {@link Command} was executed in the correct format, {@code false} if an error
     * occurred during the {@link Command}'s execution or if it was improperly executed
     */
    @Override
    public boolean execute(@NotNull(value = "request cannot be null") final CommandContext context,
                           @NotNull(value = "arguments cannot be null") final Arguments arguments) {
        return false;
    }

    @Override
    public String toString() {
        return "Module{data = " + this.data.toString() + "}";
    }

}
