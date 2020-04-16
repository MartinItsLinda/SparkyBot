package uk.sparkydiscordbot.util;

import uk.sparkydiscordbot.api.entities.utils.Objects;

import java.util.concurrent.TimeUnit;

public class RateLimiter {

    private int requests;
    private long refresh;

    private long timestamp;
    private int remaining;

    /**
     * @param requests How many requests are permitted
     * @param refresh  How long before the total executed requests reset (in milliseconds)
     */
    public RateLimiter(final int requests,
                       final long refresh) {
        this(requests, refresh, TimeUnit.SECONDS);
    }

    /**
     * @param requests How many requests are permitted
     * @param refresh  How long before the total executed requests reset
     * @param unit     The {@link TimeUnit} at which to measure {@code refresh} at
     */
    public RateLimiter(final int requests,
                       final long refresh,
                       final TimeUnit unit) {
        Objects.checkArgument(requests > 0, "requests cannot be negative");
        Objects.checkArgument(refresh > 0, "refresh cannot be negative");
        this.requests = requests;
        this.refresh = unit == null ? TimeUnit.SECONDS.toMillis(refresh) : unit.toMillis(refresh);
    }

    /**
     * @return The maximum number of requests
     */
    public int getRequestLimit() {
        return this.requests;
    }

    /**
     * Set the maximum amount of requests
     *
     * @param requests The maximum number of requests
     */
    public void setRequestLimit(final int requests) {
        Objects.checkArgument(requests > 0, "requests cannot be negative");
        this.requests = requests;
    }

    /**
     * @return How frequently the total amount of executed requests is reset
     *
     * @see #getRequestLimit()
     * @see #getRemaining()
     */
    public long getRefresh() {
        return this.refresh;
    }

    /**
     * Set how frequently the total amount of executed requests is reset
     *
     * @param refresh How long before the total executed requests reset
     */
    public void setRefresh(final long refresh) {
        this.setRefresh(refresh, TimeUnit.SECONDS);
    }

    /**
     * Set how frequently the total amount of executed requests is reset
     *
     * @param refresh How long before the total executed requests reset
     * @param unit    The {@link TimeUnit} at which to measure {@code refresh} at
     */
    public void setRefresh(final long refresh,
                           final TimeUnit unit) {
        Objects.checkArgument(refresh > 0, "refresh cannot be negative");
        this.refresh = unit == null ? TimeUnit.SECONDS.toMillis(refresh) : unit.toMillis(refresh);
    }

    /**
     * @return The total amount of remaining requests
     */
    public int getRemaining() {
        return this.remaining;
    }

    /**
     * Blocks the current thread until able to execute a request.
     */
    public void acquire() {

        if (!this.canAcquire()) {
            final long sleepy = (System.currentTimeMillis() - this.timestamp) - this.refresh;
            try {
                if (sleepy > 0) {
                    Thread.sleep(sleepy);
                }
            } catch (final InterruptedException ex) {
                Thread.currentThread().interrupt();
                return;
            }
        }

        this.refresh();

        this.remaining--;
        this.timestamp = System.currentTimeMillis();

    }

    /**
     * Tests for being acquirable, if the condition is {@code true} then the total available requests are reduced by
     * one.
     *
     * @return {@code true} if a request was acquired, {@code false} otherwise
     */
    public boolean tryAcquire() {
        if (this.canAcquire()) {
            this.remaining--;
            this.timestamp = System.currentTimeMillis();
            return true;
        }
        return false;
    }

    private boolean canAcquire() {
        final long cooldown = (System.currentTimeMillis() - this.timestamp);
        if (cooldown >= this.refresh) {
            this.remaining = this.requests;
        }
        return this.remaining > 0;
    }

    private void refresh() {
        this.remaining = this.requests;
        this.timestamp = 0;
    }
}
