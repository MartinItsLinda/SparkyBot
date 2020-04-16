package uk.sparkydiscordbot.api.entities.event;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.sparkydiscordbot.api.entities.module.Module;

import java.util.Collection;

public interface RootEventRegistry {

    Logger LOGGER = LoggerFactory.getLogger(RootEventRegistry.class);

    /**
     * @param event The event to call
     */
    void callEvent(@NotNull(value = "event cannot be null") final Event event);

    /**
     * Registers all {@link Event}s in the given {@link RootEventRegistry} class
     *
     * @param listener The {@link EventListener} to register
     * @param module   The {@link Module} to register
     */
    void registerEvents(@NotNull(value = "listener cannot be null") final EventListener listener,
                        @NotNull(value = "parent module cannot be null") final Module module);

    /**
     * @param event The event
     *
     * @return A {@link Collection} containing every {@link RegisteredListener} registered to this {@link Event}
     */
    Collection<RegisteredListener> getRegisteredListeners(@NotNull(value = "event cannot be null") final Event event);

    /**
     * @param module The {@link Module} to get the listeners of
     *
     * @return A {@link Collection} containing every listener registered to the {@link Module}
     */
    Collection<RegisteredListener> getRegisteredListeners(@NotNull(value = "parent module cannot be null") final Module module);

    /**
     * Unregisters every listener for the {@link Module}
     *
     * @param module The {@link Module} to unregister
     */
    void unregisterAll(@NotNull(value = "parent module cannot be null") final Module module);

    /**
     * Unregisters all listeners
     */
    void unregisterAll();
}
