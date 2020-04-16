package uk.sparkydiscordbot.internal.module;

import com.google.common.collect.Lists;
import com.mongodb.client.MongoDatabase;
import org.jetbrains.annotations.NotNull;
import uk.sparkydiscordbot.Bot;
import uk.sparkydiscordbot.api.entities.command.RootCommandRegistry;
import uk.sparkydiscordbot.api.entities.economy.AccountRegistry;
import uk.sparkydiscordbot.api.entities.event.RootEventRegistry;
import uk.sparkydiscordbot.api.entities.module.EventWaiter;
import uk.sparkydiscordbot.api.entities.module.Module;
import uk.sparkydiscordbot.api.entities.module.ModuleData;
import uk.sparkydiscordbot.api.entities.module.loader.ModuleLoader;
import uk.sparkydiscordbot.api.entities.utils.Objects;
import uk.sparkydiscordbot.api.event.module.ModuleDisabledEvent;
import uk.sparkydiscordbot.api.event.module.ModuleEnabledEvent;
import uk.sparkydiscordbot.api.event.module.ModuleLoadEvent;
import uk.sparkydiscordbot.api.exception.module.InvalidModuleException;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

public class ModuleLoaderImpl implements ModuleLoader {

    /**
     * The modules directory
     */
    private static final File MODULES = new File("modules");

    private final List<Module> modules = Lists.newArrayList();

    private final RootCommandRegistry commandManager;
    private final RootEventRegistry eventManager;
    private final AccountRegistry accounts;
    private final MongoDatabase database;

    public ModuleLoaderImpl(final Bot bot) {
        this.accounts = bot.getAccounts();
        this.commandManager = bot.getCommandRegistry();
        this.eventManager = bot.getEventRegistry();
        this.database = bot.getMongoDatabase();
    }

    @Override
    public Module getModuleByName(@NotNull(value = "module names cannot be null") final String name) {
        return this.modules.stream().filter(module -> module.getData().name().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    @Override
    public Collection<Module> getModules() {
        return Collections.unmodifiableCollection(this.modules);
    }

    @Override
    public Collection<Module> getEnabledModules() {
        return this.modules.stream().filter(Module::isEnabled).collect(Collectors.toSet());
    }

    @Override
    public Module loadModule(@NotNull(value = "module file cannot be null") final File file) throws IllegalArgumentException, IllegalStateException, InvalidModuleException {
        Objects.checkArgument(file.exists(), "file doesn't exist");
        Objects.checkArgument(file.isFile(), "file is not a file");

        try (final JarFile jar = this.getJarFile(file)) {
            if (jar == null) {
                return null;
            }

            final Enumeration<JarEntry> enumeration = jar.entries();

            final URL[] urls = new URL[] {file.toURI().toURL()};
            final ClassLoader loader = URLClassLoader.newInstance(urls);

            while (enumeration.hasMoreElements()) {
                final JarEntry entry = enumeration.nextElement();

                if (entry.isDirectory() || !entry.getName().endsWith(".class")) {
                    continue;
                }

                String className = entry.getName();
                className = className.substring(0, className.length() - 6).replace("/", ".");

                final Class<?> clazz = loader.loadClass(className);
                if (!Module.class.isAssignableFrom(clazz)) {
                    continue;
                }

                final Class<? extends Module> moduleClass = clazz.asSubclass(Module.class);
                try {
                    final Module module = moduleClass.getDeclaredConstructor().newInstance();
                    final ModuleData data = module.getData();

                    if (this.getModuleByName(data.name()) != null) {
                        throw new IllegalStateException("Module '" + data.name() + "' is already loaded");
                    } else {
                        final File dataFolder = new File(MODULES, data.name());
                        if (!dataFolder.exists()) {
                            //noinspection ResultOfMethodCallIgnored
                            dataFolder.mkdirs();
                        }

                        module.init(this.commandManager, this.eventManager, this.accounts, this.database, dataFolder, file);

                        this.modules.add(module);

                        this.eventManager.callEvent(new ModuleLoadEvent(module));
                        return module;
                    }
                } catch (final IllegalAccessException | InstantiationException ignored) { //impossible?
                } catch (NoSuchMethodException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }

        } catch (final IOException | ClassNotFoundException ex) {
            throw new InvalidModuleException("An error occurred whilst attempting to load module", ex);
        }
        throw new InvalidModuleException(String.format("File %s does not contain a class that extends Module", file.getName()));
    }

    @SuppressWarnings("ConstantConditions") //intellij is being weird, .listFiles() won't null?
    @Override
    public Collection<Module> loadModules() throws NullPointerException, IllegalArgumentException {

        final List<File> files = Lists.newArrayList(Arrays.stream(MODULES.listFiles()).filter(file -> file.getName().endsWith(".jar")).collect(Collectors.toList()));
        final List<Module> modules = Lists.newArrayListWithCapacity(files.size());

        for (final File child : files) {
            if (child.isFile()) {
                try {
                    modules.add(this.loadModule(child));
                } catch (final Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        return modules;
    }

    @Override
    public void disableModules() {
        this.getEnabledModules().forEach(this::disableModule);
    }

    @Override
    public void enableModule(@NotNull(value = "module cannot be null") final Module module) {
        Objects.checkState(!module.isEnabled(), "Module already enabled");

        LOGGER.info("Enabling " + module.getFullName() + "...");
        try {
            module.setEnabled(true);
        } catch (final Throwable ex) {
            LOGGER.info("An error occurred whilst enabling " + module.getFullName(), ex);
        }
        this.eventManager.callEvent(new ModuleEnabledEvent(module));
    }

    @Override
    public void disableModule(@NotNull(value = "module cannot be null") final Module module) {
        Objects.checkState(module.isEnabled(), "Module is not enabled");

        LOGGER.info("Disabling " + module.getFullName() + "...");

        //false = don't init if not already
        final EventWaiter waiter = module.getEventWaiter(false);
        if (waiter != null && !waiter.isShutdown()) { //getEventWaiter(init) returns null if not already initialized
            waiter.shutdown();
        }

        try {
            module.setEnabled(false);
        } catch (final Throwable ex) {
            LOGGER.info("An error occurred whilst disabling " + module.getFullName(), ex);
        }

        this.eventManager.callEvent(new ModuleDisabledEvent(module));

        module.getCommandRegistry().unregisterAll();
        module.getEventRegistry().unregisterAll();
        module.getScheduler().cancelAllTasks();

        this.modules.remove(module);
    }

    @Override
    public void reloadModules() {
        this.disableModules();
        this.loadModules().forEach(this::enableModule);
    }

    private JarFile getJarFile(final File file) {
        try {
            return new JarFile(file);
        } catch (final IOException ex) {
            return null;
        }
    }

}
