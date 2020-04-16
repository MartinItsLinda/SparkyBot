package uk.sparkydiscordbot.api.entities.module;


import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.sparkydiscordbot.api.entities.event.Event;
import uk.sparkydiscordbot.api.entities.event.EventHandler;
import uk.sparkydiscordbot.api.entities.event.EventListener;
import uk.sparkydiscordbot.api.entities.utils.Objects;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class EventWaiter implements EventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventWaiter.class);
    private static final ThreadGroup THREAD_GROUP = new ThreadGroup("Event Waiter Thread Group");

    private final ScheduledExecutorService service;
    private final Map<Class<?>, List<Waiter<?>>> waiters;

    /**
     * Construct a new {@link EventWaiter} with the default {@link ScheduledExecutorService}
     */
    EventWaiter(final EventRegistry registry) {
        this(registry, Executors.newSingleThreadScheduledExecutor(runnable -> new Thread(THREAD_GROUP, runnable, "Event Waiter Executor Thread")));
    }

    /**
     * Construct a new {@link EventWaiter} with the provided {@link ScheduledExecutorService}
     *
     * @param registry The {@link EventRegistry} to build to
     * @param service  The {@link ScheduledExecutorService} to use
     */
    EventWaiter(@NotNull(value = "registry cannot be null") final EventRegistry registry,
                @NotNull(value = "executor service cannot be null") final ScheduledExecutorService service) {
        this.service = service;
        this.waiters = Maps.newHashMap();
        registry.register(this);
    }

    /**
     * Waits a predetermined amount of time for the {@link Event}
     *
     * @param event         The {@link Class} of the {@link Event}
     * @param predicate     The {@link Predicate} to test when a compatible {@link Event} is called
     * @param function      The {@link Function} to perform the action
     * @param timeoutAfter  The maximum length of time to wait for, or 0 to wait indefinitely
     * @param unit          The {@link TimeUnit} to measure the {@code timeout} in
     * @param timeoutAction The {@link Runnable} to execute should a compatible {@link Event} not be thrown before
     *
     * @return The {@link WaitingEvent}
     */
    public <T extends Event> WaitingEvent<T> newWaiter(@NotNull(value = "event cannot be null") final Class<T> event,
                                                    final Predicate<T> predicate,
                                                    @NotNull(value = "function cannot be null") final Function<T, Boolean> function,
                                                    final long timeoutAfter,
                                                    @NotNull(value = "unit cannot be null") final TimeUnit unit,
                                                    final Runnable timeoutAction) {

        Objects.checkArgument(!this.isShutdown(), "Cannot register new WaitingEvent's on a shutdown EventWaiter");
        Objects.checkArgument(timeoutAfter > 0, "timeout cannot be negative or equal to 0");

        final WaitingEvent<T> waiter = new WaitingEvent<>(predicate, function);
        final List<Waiter<?>> waiters = this.waiters.computeIfAbsent(event, __ -> Lists.newArrayListWithCapacity(1));

        waiters.add(waiter);

        //noinspection ConstantConditions
        if (timeoutAfter > 0 && unit != null) {
            this.service.schedule(() -> {
                if (waiters.remove(waiter) && timeoutAction != null) {
                    try {
                        timeoutAction.run();
                    } catch (final Throwable ignored) {
                    }
                }
            }, timeoutAfter, unit);
        }

        return waiter;
    }

    /**
     * Register a {@link Waiter} with multiple classes
     *
     * @param classes The {@link Class}'s to register the waiter to
     * @param waiter  The {@link Waiter} to register
     * @param timeout The maximum length of time to wait for
     * @param unit    The {@link TimeUnit} to measure the {@code timeout} in
     */
    private void addWaiter(final Set<Class<?>> classes,
                                             final Waiter waiter,
                                             final long timeout,
                                             final TimeUnit unit,
                                             final Runnable runnable) {

        for (final Class<?> clazz : classes) {
            this.waiters.computeIfAbsent(clazz, __ -> Lists.newArrayListWithCapacity(1)).add(waiter);
        }

        if (timeout > 0 && unit != null) {
            this.service.schedule(() -> {
                for (final Class<?> clazz : classes) {
                    this.waiters.get(clazz).remove(waiter);
                }
            }, timeout, unit);
        }
    }

    /**
     * Checks whether the internal {@link ExecutorService} is either {@code null}, shutdown or terminated
     *
     * @return {@code true} if the {@link ExecutorService} is shutdown, {@code false otherwise}
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isShutdown() {
        return this.service == null || this.service.isShutdown() || this.service.isTerminated();
    }

    /**
     * Shutdown this {@link EventWaiter} and clear any registered {@link WaitingEvent}
     */
    public void shutdown() {
        if (!this.isShutdown()) {
            this.waiters.clear();
            this.service.shutdownNow();
        }
    }

    /**
     * of a new {@link WaitingEventBuilder}
     *
     * @param clazz The {@link Class} of the {@link Event}
     *
     * @return The {@link WaitingEventBuilder}
     */
    public <T extends Event> WaitingEventBuilder<T> newBuilder(final Class<T> clazz) {
        return new WaitingEventBuilder<>(this, clazz);
    }

    /**
     * Creates a new {@link MultiWaitingEventBuilder}
     *
     * @return The {@link MultiWaitingEventBuilder}
     */
    public MultiWaitingEventBuilder newMultiBuilder() {
        return new MultiWaitingEventBuilder(this);
    }

    @EventHandler
    public final void onEvent(final Event event) {
        Objects.checkArgument(!this.isShutdown(), "Cannot handle event on shutdown EventWaiter");

        Class clazz = event.getClass();
        while (clazz != null) {
            if (this.waiters.containsKey(clazz)) {
                final List<Waiter<?>> waiters = this.waiters.get(clazz);
                final Waiter[] events = waiters.toArray(new Waiter[0]);

                //noinspection unchecked
                waiters.removeAll(Stream.of(events).filter(waiter -> waiter.call(event)).collect(Collectors.toSet()));
            }
            clazz = clazz.getSuperclass();
        }

    }

    private interface Waiter<T extends Event> {

        /**
         * @param event The {@link Event} called
         *
         * @return {@code true} if the {@code event} was handled successfully and can be
         * removed from the waiting event queue, {@code false} otherwise.
         */
        boolean call(T event);

    }

    /**
     *
     */
    public class WaitingEvent<T extends Event> implements Waiter<T>{

        private final Predicate<T> predicate;
        private final Function<T, Boolean> function;

        WaitingEvent(final Predicate<T> predicate,
                     final Function<T, Boolean> function) {
            this.predicate = predicate;
            this.function = function;
        }

        /**
         * Call this {@link WaitingEvent}s internal handler {@link Function}
         *
         * @param event The {@link Event} called
         *
         * @return {@code true} if the {@code event} was handled successfully and can be
         * removed from the waiting event queue, {@code false} otherwise.
         */
        @Override
        public boolean call(final T event) {
            try {
                return this.predicate == null || this.predicate.test(event) ? this.function.apply(event) : false;
            } catch (final Throwable ex) {
                LOGGER.info("WaitingEvent threw an exception", ex);
            }
            return true;
        }

    }

    public class MultiWaitingEvent implements Waiter {

        private final Map<Class<?>, List<WaitingEvent<?>>> events;

        MultiWaitingEvent(final Map<Class<?>, List<WaitingEvent<?>>> events) {
            this.events = events;
        }

        @Override
        public boolean call(final Event event) {

            final List<WaitingEvent<?>> events = this.events.get(event.getClass());
            for (final WaitingEvent evt : events) {
                try {
                    //noinspection unchecked
                    return evt.call(event);
                } catch (final Throwable ignored){
                }
            }

            return false;
        }
    }

    /**
     *
     */
    public class WaitingEventBuilder<T extends Event> {

        protected final EventWaiter waiter;
        protected final Class<T> clazz;

        private Predicate<T> predicate;
        private Function<T, Boolean> function;
        private long timeout;
        private TimeUnit unit;
        private Runnable timeoutAction;

        /**
         * Construct a new {@link WaitingEventBuilder}
         *
         * @param waiter The {@link EventWaiter} to build to
         * @param clazz  The {@link Class} of the {@link Event}
         */
        WaitingEventBuilder(final EventWaiter waiter,
                            final Class<T> clazz) {
            this.waiter = waiter;
            this.clazz = clazz;
        }

        /**
         * Set the {@link Predicate} to check prior to execution of the {@link WaitingEvent}
         *
         * @param predicate The {@link Predicate}
         *
         * @return This {@link WaitingEventBuilder}
         */
        public WaitingEventBuilder<T> check(@NotNull(value = "predicate cannot be null") final Predicate<T> predicate) {
            this.predicate = predicate;
            return this;
        }

        /**
         * Set the executor for the {@link WaitingEvent}
         *
         * @param consumer The {@link Consumer}
         *
         * @return This {@link WaitingEventBuilder}
         */
        public WaitingEventBuilder<T> executor(@NotNull(value = "consumer cannot be null") final Consumer<T> consumer) {
            this.function = (event) -> {
                try {
                    consumer.accept(event);
                } catch (final Throwable ignored) {
                }
                return true;
            };
            return this;
        }

        /**
         * Set the executor for the {@link WaitingEvent}
         *
         * @param function The {@link Function}
         *
         * @return This {@link WaitingEventBuilder}
         */
        public WaitingEventBuilder<T> executor(@NotNull(value = "function cannot be null") final Function<T, Boolean> function) {
            this.function = function;
            return this;
        }

        /**
         * Set the timeout duration
         *
         * @param timeout The timeout (in seconds)
         *
         * @return This {@link WaitingEventBuilder}
         */
        public WaitingEventBuilder<T> timeoutAfter(final long timeout) {
            return this.timeoutAfter(timeout, TimeUnit.SECONDS);
        }

        /**
         * Set the timeout duration and the unit to measure it in
         *
         * @param timeout The timeout
         * @param unit    The {@link TimeUnit} to measure the {@code timeout} in
         *
         * @return This {@link WaitingEventBuilder}
         */
        public WaitingEventBuilder<T> timeoutAfter(final long timeout,
                                                   @NotNull(value = "unit cannot be null") final TimeUnit unit) {
            Objects.checkArgument(timeout > 0, "timeout cannot be negative or equal to 0");
            this.timeout = timeout;
            this.unit = unit;
            return this;
        }

        /**
         * Set the {@link Runnable} to be executed should a compatible {@link Event} not be called before the time is
         * up
         *
         * @param timeoutAction The {@link Runnable} to execute
         *
         * @return This {@link WaitingEventBuilder}
         */
        public WaitingEventBuilder<T> timeoutAction(@NotNull(value = "timeoutAction cannot be null") final Runnable timeoutAction) {
            this.timeoutAction = timeoutAction;
            return this;
        }

        /**
         * of and register the {@link WaitingEvent}
         *
         * @return The {@link WaitingEvent}
         */
        public WaitingEvent<T> build() {
            return this.waiter.newWaiter(this.clazz, this.predicate, this.function, this.timeout, this.unit, this.timeoutAction);
        }

    }

    public class MultiWaitingEventBuilder {

        private final EventWaiter waiter;
        private final Map<Class<?>, List<WaitingEvent<?>>> events;

        private long timeout;
        private TimeUnit unit;
        private Runnable timeoutAction;

        MultiWaitingEventBuilder(final EventWaiter waiter) {
            this.waiter = waiter;
            this.events = Maps.newHashMap();
        }

        /**
         * Set the timeout duration
         *
         * @param timeout The timeout (in seconds)
         *
         * @return This {@link MultiWaitingEventBuilder}
         */
        public MultiWaitingEventBuilder timeoutAfter(final long timeout) {
            return this.timeoutAfter(timeout, TimeUnit.SECONDS);
        }

        /**
         * Set the timeout duration and the unit to measure it in
         *
         * @param timeout The timeout
         * @param unit    The {@link TimeUnit} to measure the {@code timeout} in
         *
         * @return This {@link MultiWaitingEventBuilder}
         */
        public MultiWaitingEventBuilder timeoutAfter(final long timeout,
                                                   @NotNull(value = "unit cannot be null") final TimeUnit unit) {
            Objects.checkArgument(timeout > 0, "timeout cannot be negative or equal to 0");
            this.timeout = timeout;
            this.unit = unit;
            return this;
        }

        /**
         * Set the {@link Runnable} to be executed should a compatible {@link Event} not be called before the time is
         * up
         *
         * @param timeoutAction The {@link Runnable} to execute
         *
         * @return This {@link MultiWaitingEventBuilder}
         */
        public MultiWaitingEventBuilder timeoutAction(@NotNull(value = "timeoutAction cannot be null") final Runnable timeoutAction) {
            this.timeoutAction = timeoutAction;
            return this;
        }

        /**
         * Register a {@link WaitingEvent} to be part of this {@link EventWaiter}
         *
         * @param clazz    The {@link Class} of the {@link Event}
         * @param check    The {@link Predicate} to test when a compatible {@link Event} is called
         * @param function The {@link Function} to perform the action
         *
         * @return This {@link MultiWaitingEventBuilder}
         */
        public <T extends Event> MultiWaitingEventBuilder addWaiter(final Class<T> clazz,
                                                                    final Predicate<T> check,
                                                                    final Function<T, Boolean> function) {

            final WaitingEvent<T> waiter = new WaitingEvent<>(check, function);
            this.events.computeIfAbsent(clazz, __ -> Lists.newArrayList()).add(waiter);

            return this;
        }

        /**
         * of and register the {@link MultiWaitingEvent}
         *
         * @return The {@link MultiWaitingEvent}
         */
        public MultiWaitingEvent build() {
            final MultiWaitingEvent waiter = new MultiWaitingEvent(this.events);
            this.waiter.addWaiter(this.events.keySet(), waiter, this.timeout, this.unit, this.timeoutAction);
            return waiter;
        }

    }

}
