package uk.sparkydiscordbot.internal.caching;

import com.google.common.cache.*;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutionException;

public class EntityLoadingCache<K, V> {

    private final LoadingCache<K, V> cache;

    /**
     * Construct a new {@link EntityLoadingCache}
     *
     * @param loader The {@link CacheLoader}
     */
    public EntityLoadingCache(@NotNull(value = "loader cannot be null") final CacheLoader<K, V> loader) {
        this.cache = CacheBuilder.newBuilder().build(loader);
    }

    /**
     * Construct a new {@link EntityLoadingCache}
     *
     * @param spec   The {@link CacheBuilderSpec}
     * @param loader The {@link CacheLoader}
     */
    public EntityLoadingCache(@NotNull(value = "cache config cannot be null") final CacheBuilderSpec spec,
                              @NotNull(value = "loader cannot be null") final CacheLoader<K, V> loader) {
        this.cache = CacheBuilder.from(spec).build(loader);
    }

    /**
     * @return The {@link Cache}
     */
    public Cache getCache() {
        return this.cache;
    }

    /**
     * @return The {@link CacheStats}
     */
    public CacheStats getCacheStats() {
        return this.cache.stats();
    }

    /**
     * Get an entity from the internal cache or load it if no entity exists.
     *
     * @param key The key used in searching for the requested entity
     *
     * @return The entity, or {@code null} if an error occurred whilst querying the cache or no entity was found.
     */
    public V getEntity(@NotNull(value = "key cannot be null") final K key) {
        try {
            return this.cache.get(key);
        } catch (final ExecutionException ignored) {
        }
        return null;
    }

    /**
     * Get an entity from the internal cache or return {@code def} if no entity exists.
     *
     * @param key The key used in searching for the requested entity
     * @param d   The default value to use should no such cache value exist.
     *
     * @return The cached entities value, or {@code def} if no value is present or the value is {@code null}
     */
    public V getEntity(@NotNull(value = "key cannot be null") final K key,
                       @NotNull(value = "default cannot be null") final V d) {
        final V v = this.getEntity(key);
        if (v == null) {
            return  d;
        }
        return v;
    }

    /**
     * Get an entity from the internal cache.
     *
     * @param key The key used in searching for the requested entity
     *
     * @return The entity, or {@code null} if no entity exists within the cache.
     */
    public V getEntityIfPresent(final K key) {
        return this.cache.getIfPresent(key);
    }

}
