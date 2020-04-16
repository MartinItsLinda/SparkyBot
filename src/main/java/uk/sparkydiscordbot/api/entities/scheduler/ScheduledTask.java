package uk.sparkydiscordbot.api.entities.scheduler;

import org.jetbrains.annotations.NotNull;
import uk.sparkydiscordbot.api.entities.utils.Objects;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ScheduledTask {

    private final int taskId;
    private final ScheduledFuture<?> future;
    private final boolean repeating;
    private final long initialDelay;
    private final long repeatAfter;
    private final TimeUnit unit;

    /**
     * Construct a new {@link ScheduledTask}
     *
     * @param taskId The id of this {@link ScheduledTask}
     * @param future The executor of this {@link ScheduledTask}
     */
    ScheduledTask(final int taskId,
                  @NotNull(value = "future cannot be null") final ScheduledFuture<?> future) {
        this(taskId, future, false, -1, -1, TimeUnit.MILLISECONDS);
    }

    /**
     * Construct a new {@link ScheduledTask}
     *
     * @param taskId       The id of this {@link ScheduledTask}
     * @param future       The executor of this {@link ScheduledTask}
     * @param repeating    Whether this {@link ScheduledTask} repeats
     * @param initialDelay The initial delay before executing this {@link ScheduledTask}
     * @param repeatAfter  How long to wait before repeating this {@link ScheduledTask}
     * @param unit         The {@link TimeUnit} used to calculate frequency of execution
     */
    ScheduledTask(final int taskId,
                  @NotNull(value = "future cannot be null") final ScheduledFuture<?> future,
                  final boolean repeating,
                  final long initialDelay,
                  final long repeatAfter,
                  @NotNull(value = "unit cannot be null") final TimeUnit unit) {
        Objects.checkArgument(taskId > 0, "task id cannot be negative");
        this.taskId = taskId;
        this.future = future;
        this.repeating = repeating;
        this.initialDelay = (initialDelay == 0 ? -1 : initialDelay);
        this.repeatAfter = (repeatAfter == 0 ? -1 : repeatAfter);
        this.unit = unit;
    }

    /**
     * @return This {@link ScheduledTask}s task id
     */
    public int getTaskId() {
        return this.taskId;
    }

    /**
     * @return The executor of this {@link ScheduledTask}
     */
    public ScheduledFuture<?> getExecutor() {
        return this.future;
    }

    /**
     * @return {@code true} if this {@link ScheduledTask} repeats, {@code false} otherwise
     */
    public boolean isRepeating() {
        return this.repeating;
    }

    /**
     * @return The initial delay before executing this {@link ScheduledTask}, or -1 if this task executes immediately
     */
    public long getInitialDelay() {
        return this.initialDelay;
    }

    /**
     * @return The remaining delay before execution
     */
    public long getRemainingDelay(@NotNull(value = "unit cannot be null") final TimeUnit unit) {
        return this.future.getDelay(unit);
    }

    /**
     * @return How frequently to repeat this {@link ScheduledTask}, or -1 if this task does not repeat.
     */
    public long getRepeatAfter() {
        return this.repeatAfter;
    }

    /**
     * @return The {@link TimeUnit} used for measuring frequency of execution for this {@link ScheduledTask}
     */
    public TimeUnit getTimeUnit() {
        return this.unit;
    }

    /**
     * Cancel this {@link ScheduledTask}
     *
     * @return {@code true} if this task was cancelled, {@code false} otherwise
     */
    public boolean cancel() {
        return Scheduler.getInstance().destroy(this.taskId);
    }

}
