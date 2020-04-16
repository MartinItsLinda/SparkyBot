package uk.sparkydiscordbot.api.entities.event;

public class Event {

    private final String name;
    private final boolean isAsync;

    public Event() {
        this(false);
    }

    /**
     * @param isAsync Whether this {@link Event} should be fired asynchronously
     */
    public Event(final boolean isAsync) {
        this.name = getClass().getSimpleName();
        this.isAsync = isAsync;
    }

    /**
     * @return The names (class names) of this {@link Event}
     */
    public String getEventName() {
        return this.name;
    }

    /**
     * Returns whether this {@link Event} should be fired on its own thread.
     *
     * @return {@code true} if this {@link Event} should be fired asynchronously, {@code false} otherwise
     */
    public boolean isAsync() {
        return this.isAsync;
    }

    /**
     * Check whether this class is an instance of {@link Cancellable}.
     * <p>
     * <p>When calling events that implement the {@link Cancellable} interface,
     * it is advised to listen to the result of {@link Cancellable#isCancelled()}
     * before continuing on with the operation.</p>
     *
     * @return {@code true} if the event is cancellable, {@code false} otherwise
     */
    public boolean isCancellable() {
        return (this instanceof Cancellable);
    }
}
