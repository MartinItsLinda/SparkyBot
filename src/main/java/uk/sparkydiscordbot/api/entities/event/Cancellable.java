package uk.sparkydiscordbot.api.entities.event;

public interface Cancellable {

    /**
     * @return {@code true} if the associated {@link Event} was cancelled, {@code false} otherwise.
     */
    boolean isCancelled();

    /**
     * Set the current cancelled status of the associated {@link Event}
     *
     * @param cancelled Whether to cancel this {@link Event}
     */
    void setCancelled(final boolean cancelled);
}
