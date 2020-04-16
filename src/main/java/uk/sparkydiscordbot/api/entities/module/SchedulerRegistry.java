package uk.sparkydiscordbot.api.entities.module;

import com.google.common.collect.Lists;
import org.jetbrains.annotations.NotNull;
import uk.sparkydiscordbot.api.entities.scheduler.ScheduledTask;
import uk.sparkydiscordbot.api.entities.scheduler.Scheduler;

import java.util.List;

public class SchedulerRegistry {

    private final Scheduler scheduler;
    private final List<Integer> taskIds; //I'm going to worry about cleaning up this list at some other point

    SchedulerRegistry() {
        this.scheduler = Scheduler.getInstance();
        this.taskIds = Lists.newArrayList();
    }

    /**
     * Schedule a task to be executed after the given delay
     *
     * @param runnable   The {@link Runnable} to execute
     * @param startAfter How many milliseconds to wait before executing this task
     * @param delay      How frequently to execute this task
     *
     * @return A {@link ScheduledTask} containing the task id and its executor
     *
     * @throws IllegalStateException If the scheduler is already shutdown
     */
    public ScheduledTask runTaskRepeating(@NotNull(value = "runnable cannot be null") final Runnable runnable,
                                          final long startAfter,
                                          final long delay) throws IllegalStateException {
        final ScheduledTask task = this.scheduler.runTaskRepeating(runnable, startAfter, delay);

        this.taskIds.add(task.getTaskId());

        return task;
    }

    /**
     * Schedule a task to be ran once immediately after scheduling
     *
     * @param runnable The {@link Runnable} to execute
     *
     * @return A {@link ScheduledTask} containing the task id and its executor
     *
     * @throws IllegalStateException If the scheduler is already shutdown
     */
    public ScheduledTask runTask(@NotNull(value = "runnable cannot be null") final Runnable runnable) throws IllegalStateException {
        return this.runTaskLater(runnable, 0L);
    }

    /**
     * Schedule a task to be ran after the given delay
     *
     * @param runnable The {@link Runnable} to execute
     * @param delay    How many milliseconds to wait before executing this task
     *
     * @return A {@link ScheduledTask} containing the task id and its executor
     *
     * @throws IllegalStateException If the scheduler is already shutdown
     */
    public ScheduledTask runTaskLater(@NotNull(value = "runnable cannot be null") final Runnable runnable,
                                      final long delay) throws IllegalStateException {
        final ScheduledTask task = this.scheduler.runTaskLater(runnable, delay);

        this.taskIds.add(task.getTaskId());

        return task;
    }

    /**
     * Cancel all un-completed tasks in this {@link Scheduler}.
     */
    public void cancelAllTasks() {
        for (final int taskId : this.taskIds) {
            this.scheduler.destroy(taskId);
        }
    }

    /**
     * Cancel a task by its task id
     *
     * @param taskId The id of the task
     *
     * @return {@code true} if the task was destroyed, {@code false otherwise}
     */
    public boolean destroy(final int taskId) {
        final boolean destroyed = this.scheduler.destroy(taskId);
        if (destroyed) {
            this.taskIds.remove(taskId);
        }
        return destroyed;
    }

}
