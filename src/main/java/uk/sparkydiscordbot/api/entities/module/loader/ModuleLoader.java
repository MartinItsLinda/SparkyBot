package uk.sparkydiscordbot.api.entities.module.loader;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.sparkydiscordbot.api.entities.module.Module;
import uk.sparkydiscordbot.api.exception.module.InvalidModuleException;

import java.io.File;
import java.util.Collection;

public interface ModuleLoader {

    Logger LOGGER = LoggerFactory.getLogger(ModuleLoader.class);

    /**
     * Retrieve a {@link Module} by-names
     *
     * @param name The names of the {@link Module}
     *
     * @return The {@link Module}, or {@code null} if no module was found
     */
    Module getModuleByName(@NotNull(value = "module names cannot be null") final String name);

    /**
     * Get every loaded {@link Module}. Even if an error occurred during {@link Module#onEnable()} it will still appear
     * in this list
     *
     * @return A {@link Collection} of loaded {@link Module}'s
     */
    Collection<Module> getModules();

    /**
     * @return A {@link Collection} of enabled {@link Module}'s
     */
    Collection<Module> getEnabledModules();

    /**
     * @param directory The module file
     *
     * @return The loaded {@link Module}
     *
     * @throws IllegalArgumentException If the directory is not a valid jar file
     * @throws IllegalArgumentException If this module is already loaded
     * @throws InvalidModuleException   If this module does not contain a main class
     */
    Module loadModule(@NotNull(value = "module file cannot be null") final File directory) throws IllegalArgumentException, IllegalStateException, InvalidModuleException;

    /**
     * Loads all {@link Module}'s in the current directory, if the directory has no child files it will just return an
     * empty list.
     *
     * @return A {@link Collection} containing every loaded module
     *
     * @throws NullPointerException     If the specified directory is null
     * @throws IllegalArgumentException If the specified directory is not a valid directory
     */
    Collection<Module> loadModules() throws NullPointerException, IllegalArgumentException;

    /**
     * Disables every enabled {@link Module}
     */
    void disableModules();

    /**
     * @param module The {@link Module} to enable
     */
    void enableModule(@NotNull(value = "module cannot be null") final Module module);

    /**
     * @param module The {@link Module} to disable
     */
    void disableModule(@NotNull(value = "module cannot be null") final Module module);

    /**
     * Reload all {@link Module}s
     *
     * @see ModuleLoader#disableModule(Module)
     * @see ModuleLoader#enableModule(Module)
     */
    void reloadModules();

}
