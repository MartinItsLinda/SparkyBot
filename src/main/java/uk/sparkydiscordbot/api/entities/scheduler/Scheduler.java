package uk.sparkydiscordbot.api.entities.scheduler;

import com.google.common.collect.Lists;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.sparkydiscordbot.api.entities.utils.Objects;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Scheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(Scheduler.class);
    private static final AtomicInteger ID_GEN = new AtomicInteger(); //this is used to give tasks an id so we can cancel them later
    private static final Scheduler SCHEDULER = new Scheduler(); //scheduler is accessed via singleton
    private static final ThreadGroup THREAD_GROUP = new ThreadGroup("Scheduler Thread Group"); //thread group for the executor service

    /**
     * @return This {@link Scheduler} instance
     */
    public static Scheduler getInstance() {
        return Scheduler.SCHEDULER;
    }

    private final List<ScheduledTask> futures;
    private boolean shutdown = false;

    private ScheduledExecutorService service; //think of executor service like a Timer (the class)
    private ScheduledExecutorService singleThreadSyncService;

    private Scheduler() {
        this.futures = Lists.newCopyOnWriteArrayList(); //should perhaps use a concurrent map instead of COW

        this.service = this.getService();
//        this.singleThreadSyncService = new SingleThreadSyncExecutorService();
        this.runTaskRepeating(this::cleanup, 0L, TimeUnit.MINUTES.toMillis(5));
    }

    private ScheduledExecutorService getService() throws IllegalStateException {
        Objects.checkArgument(!this.shutdown, "cannot retrieve executor service as this scheduler is shutdown");
        if (this.service == null || this.service.isShutdown() || this.service.isTerminated()) { //if the service wasn't created or its shutdown of a new one
            this.service = Executors.newScheduledThreadPool(10, runnable -> new Thread(THREAD_GROUP, runnable, "Scheduled Task"));
        }
        return this.service;
    }

    //Used internally to remove completed tasks from memory
    private void cleanup() {
        //TODO: Find a way to automagically remove tasks after they're done (and not repeating)
        this.futures.removeIf(task -> task.getExecutor().isDone());
    }

    /**
     * Schedule a task to be executed after the given delay
     *
     * @param runnable   The {@link Runnable} to execute
     * @param startAfter How many milliseconds to wait before executing this task
     * @param frequency  How frequently to execute this task in milliseconds
     * @return A {@link ScheduledTask} containing the task id and its executor
     * @throws IllegalStateException If the scheduler is already shutdown
     */
    public ScheduledTask runTaskRepeating(@NotNull(value = "runnable cannot be null") final Runnable runnable,
                                          final long startAfter,
                                          final long frequency) throws IllegalStateException {
        Objects.checkArgument(frequency > 0, "frequency cannot be less than 1");
        final ScheduledFuture<?> future = this.getService().scheduleWithFixedDelay(() -> { //schedule the task using the executor service
            try {
                runnable.run();
            } catch (final Throwable ex) {
                LOGGER.info("A task encountered an uncaught exception", ex);
            }
        }, startAfter, frequency, TimeUnit.MILLISECONDS);

        final ScheduledTask task = new ScheduledTask(Scheduler.ID_GEN.incrementAndGet(), future, true, startAfter, frequency, TimeUnit.MILLISECONDS);

        this.futures.add(task);

        return task;
    }

    /**
     * Schedule a task to be ran once immediately after scheduling
     *
     * @param runnable The {@link Runnable} to execute
     * @throws IllegalStateException If the scheduler is already shutdown
     */
    public void runTask(@NotNull(value = "runnable cannot be null") final Runnable runnable) throws IllegalStateException {
        this.getService().execute(() -> {
            try {
                runnable.run();
            } catch (final Throwable ex) {
                LOGGER.info("A task encountered an uncaught exception", ex);
            }
        });
    }

    /**
     * Schedule a task to be ran after the given delay
     *
     * @param runnable The {@link Runnable} to execute
     * @param delay    How many milliseconds to wait before executing this task
     * @return A {@link ScheduledTask} containing the task id and its executor
     * @throws IllegalStateException If the scheduler is already shutdown
     */
    public ScheduledTask runTaskLater(@NotNull(value = "runnable cannot be null") final Runnable runnable,
                                      final long delay) throws IllegalStateException {
        Objects.checkArgument(delay > 0, "delay cannot be less than 1");
        final ScheduledFuture<?> future = this.getService().schedule(() -> {
            try {
                runnable.run();
            } catch (final Throwable ex) {
                LOGGER.info("A task encountered an uncaught exception", ex);
            }
        }, delay, TimeUnit.MILLISECONDS);

        final ScheduledTask task = new ScheduledTask(Scheduler.ID_GEN.incrementAndGet(), future, false, delay, -1, TimeUnit.MILLISECONDS);

        this.futures.add(task);

        return task;
    }

    /**
     * Shutdown this {@link Scheduler} cancelling all registered tasks and preventing registration of future tasks
     *
     * @throws IllegalStateException If the scheduler is already shutdown
     */
    public synchronized void shutdown() throws IllegalStateException {
        Objects.checkArgument(!this.shutdown, "scheduler already shutdown."); //can't shutdown a scheduler that isn't running *insert meme here*
        this.cancelAllTasks(); //cancel every task then shutdown the executor
        this.service.shutdownNow();
        this.shutdown = true;
    }

    /**
     * Cancel all un-completed tasks in this {@link Scheduler}.
     */
    public void cancelAllTasks() {
        this.futures.forEach(future -> this.destroy(future.getTaskId()));
        this.futures.clear();
    }

    /**
     * Cancel a task by its task id
     *
     * @param taskId The id of the task
     * @return {@code true} if the task was destroyed, {@code false} otherwise
     */
    public boolean destroy(final int taskId) {
        boolean destroyed = false;

        for (final ScheduledTask task : this.futures) {
            if (task.getTaskId() == taskId) { //make sure it's the task we're trying to cancel
                final ScheduledFuture<?> future = task.getExecutor();
                if (!future.isDone()) { //check if it's done yet
                    destroyed = future.cancel(true);
                    this.futures.remove(task);
                }
            }
        }

        return destroyed;
    }

}
