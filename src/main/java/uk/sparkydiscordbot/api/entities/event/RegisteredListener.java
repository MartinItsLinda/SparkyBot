package uk.sparkydiscordbot.api.entities.event;

import org.jetbrains.annotations.NotNull;
import uk.sparkydiscordbot.api.entities.module.Module;
import uk.sparkydiscordbot.api.exception.event.EventException;

public class RegisteredListener {

    private final EventListener listener;
    private final EventExecutor executor;
    private final EventHandler handler;
    private final Module module;

    /**
     * @param listener The {@link EventListener} to the {@link Event}
     * @param executor The {@link EventExecutor} for this {@link Event}
     * @param handler  The {@link EventHandler} for this {@link Event}
     * @param module   The {@link Module} to register this {@link RegisteredListener} to
     */
    public RegisteredListener(@NotNull(value = "listener cannot be null") final EventListener listener,
                              @NotNull(value = "executor cannot be null") final EventExecutor executor,
                              @NotNull(value = "handler cannot be null") final EventHandler handler,
                              @NotNull(value = "parent module cannot be null") final Module module) {
        this.listener = listener;
        this.executor = executor;
        this.handler = handler;
        this.module = module;
    }

    /**
     * @return The listening class of this {@link Event}
     */
    public EventListener getListener() {
        return this.listener;
    }

    /**
     * @return The {@link Module} this {@link RegisteredListener} is registered to
     */
    public Module getModule() {
        return this.module;
    }

    /**
     * @return The {@link EventExecutor} for this {@link Event}
     */
    public EventExecutor getExecutor() {
        return this.executor;
    }

    /**
     * @return The {@link EventHandler}
     */
    public EventHandler getHandler() {
        return this.handler;
    }

    /**
     * @param event The {@link Event} to call
     *
     * @throws EventException If an error occurred during event execution
     */
    public void callEvent(@NotNull(value = "event cannot be null") final Event event) throws EventException {
        if (!(event.isCancellable() && ((Cancellable) event).isCancelled() && this.handler.ignoreCancelled())) {
            this.executor.execute(this.listener, event);
        }
    }

}
