package uk.sparkydiscordbot.internal.event;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.jetbrains.annotations.NotNull;
import uk.sparkydiscordbot.api.entities.event.*;
import uk.sparkydiscordbot.api.entities.event.EventListener;
import uk.sparkydiscordbot.api.entities.module.Module;
import uk.sparkydiscordbot.api.exception.event.EventException;
import uk.sparkydiscordbot.Bot;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class EventRegistryImpl implements RootEventRegistry {

    private final Map<Class<? extends Event>, List<RegisteredListener>> listeners;

    public EventRegistryImpl() {
        this.listeners = Maps.newConcurrentMap();
    }

    @Override
    public void callEvent(@NotNull(value = "event cannot be null") final Event event) {
        if (event.isAsync()) {
            Bot.CACHED_EXECUTOR_SERVICE.submit(() -> this.fireEvent(event));
        } else {
            this.fireEvent(event);
        }
    }

    @Override
    public void registerEvents(@NotNull(value = "listener cannot be null") final EventListener listener,
                               @NotNull(value = "module cannot be null") final Module module) {

        final List<Method> methods = Arrays.stream(listener.getClass().getDeclaredMethods()).filter(method -> method.isAnnotationPresent(EventHandler.class)).collect(Collectors.toList());
        for (final Method method : methods) {

            if (method.isBridge() || method.isSynthetic()) {
                continue;
            }

            final EventHandler handler = method.getAnnotation(EventHandler.class);

            final Class<?>[] parameters = method.getParameterTypes();
            if (parameters.length == 0 || parameters.length > 1 || !Event.class.isAssignableFrom(parameters[0])) {
                continue;
            }

            final Class<? extends Event> eventClass = parameters[0].asSubclass(Event.class);

            if (!method.isAccessible()) {
                method.setAccessible(true);
            }

            final List<RegisteredListener> registrations = this.listeners.computeIfAbsent(eventClass, k -> new ArrayList<>());

            final EventExecutor executor = (eventListener, event) -> {
                try {
                    method.invoke(eventListener, event);
                } catch (final IllegalAccessException | InvocationTargetException ex) {
                    throw new EventException(ex);
                }
            };

            registrations.add(new RegisteredListener(listener, executor, handler, module));

            this.listeners.put(eventClass, registrations);

        }
    }

    @Override
    public Collection<RegisteredListener> getRegisteredListeners(@NotNull(value = "event cannot be null") final Event event) {
        final List<RegisteredListener> listeners = Lists.newArrayList();
        for (final Class<? extends Event> clazz : this.listeners.keySet()) {
            if (clazz.isAssignableFrom(event.getClass())) {
                listeners.addAll(this.listeners.get(clazz));
            }
        }
        return Collections.unmodifiableCollection(listeners);
    }

    @Override
    public Collection<RegisteredListener> getRegisteredListeners(@NotNull(value = "parent module cannot be null") final Module module) {
        final List<RegisteredListener> listeners = Lists.newArrayList();
        for (final Class<? extends Event> clazz : this.listeners.keySet()) {
            for (final RegisteredListener listener : this.listeners.get(clazz)) {
                if (listener.getModule() == module) {
                    listeners.add(listener);
                }
            }
        }
        return Collections.unmodifiableCollection(listeners);
    }

    @Override
    public void unregisterAll(@NotNull(value = "parent module cannot be null") final Module module) {
        this.listeners.keySet().forEach(clazz -> this.listeners.get(clazz).removeIf(listener -> listener.getModule() == module));
    }

    @Override
    public void unregisterAll() {
        this.listeners.clear();
    }

    private void fireEvent(@NotNull(value = "event cannot be null") final Event event) {
        this.getRegisteredListeners(event).forEach(listener -> {
            try {
                listener.callEvent(event);
            } catch (final Throwable ex) {
                LOGGER.info("Could not pass event " + event.getEventName() + " to " + listener.getModule().getFullName(), ex);
            }
        });
    }
}
