package uk.sparkydiscordbot.api.entities.configuration;

import com.google.common.collect.Maps;
import com.google.gson.internal.LazilyParsedNumber;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;
import uk.sparkydiscordbot.api.entities.cache.Saveable;
import uk.sparkydiscordbot.api.entities.utils.Objects;
import uk.sparkydiscordbot.internal.caching.EntityLoadingCache;
import uk.sparkydiscordbot.internal.caching.caches.GuildConfigCache;

import java.util.Map;

public class GuildSettings implements Saveable {

    private static final EntityLoadingCache<String, GuildSettings> CONFIG_CACHE = new GuildConfigCache();

    //TODO: Revise this sytem, it's hideous

    public static GuildSettings getConfig(final Guild guild) {
        return getConfig(guild.getId());
    }

    public static GuildSettings getConfig(final long guildId) {
        return getConfig(Long.toString(guildId));
    }

    public static GuildSettings getConfig(final String guildId) {
        return CONFIG_CACHE.getEntity(guildId);
    }

    public static GuildSettings fromMap(final Map<String, Object> map) {
        final GuildSettings config = new GuildSettings((String)map.get("server_id"));
        config.config.putAll(map);
        return config;
    }

    private final String serverId;
    private final Map<String, Object> config;

    private GuildSettings(final String serverId) {
        this.serverId = serverId;
        this.config = Maps.newHashMap();
    }

    /**
     * @return The guild id this {@link GuildSettings} is for
     */
    public String getServerId() {
        return this.serverId;
    }

    /**
     * @param key The key
     *
     * @return The {@link Integer} value associated with the key
     *
     * @throws IllegalArgumentException If the key is null or effectively null (empty)
     * @throws NullPointerException     If there is no value for the given key
     * @throws IllegalArgumentException If the value for the given key is not a {@link Number}
     */
    public int getInt(@NotNull(value = "key cannot be null") final String key) throws IllegalArgumentException, NullPointerException {
        Objects.checkArgument(!key.isEmpty(), "key cannot be null or effectively null");

        final Object result = this.get(key);
        if (result == null) {
            this.throwError(key);
        }

        return this.toNumber(result).intValue();
    }

    /**
     * Retrieve the value associated with a key
     *
     * @param key The key
     *
     * @return The value associated with the key or {@code null} if there is no value
     *
     * @throws IllegalArgumentException If the key is null or effectively null (empty)
     */
    public Object get(@NotNull(value = "key cannot be null") final String key) throws IllegalArgumentException {
        Objects.checkArgument(!key.isEmpty(), "key cannot be null or effectively null");
        return this.config.get(key);
    }

    private void throwError(final String key) {
        throw new NullPointerException("No value exists for key '" + key + "'");
    }

    private Number toNumber(final Object node) {
        return new LazilyParsedNumber(node.toString());
    }

    /**
     * @param key The key
     * @param def The default value to return
     *
     * @return The {@link Integer} value or {@code def} if the given key has no value
     *
     * @throws IllegalArgumentException If the value for the given key is not a {@link Number}
     */
    public int getInt(@NotNull(value = "key cannot be null") final String key,
                      final int def) throws IllegalArgumentException {
        Objects.checkArgument(!key.isEmpty(), "key cannot be null or effectively null");

        final Object result = this.get(key);
        if (result == null) {
            return def;
        }

        return this.toNumber(result).intValue();
    }

    /**
     * @param key The key
     *
     * @return The {@link Long} value
     *
     * @throws IllegalArgumentException If the key is null or effectively null (empty)
     * @throws NullPointerException     If there is no value for the given key
     * @throws IllegalArgumentException If the value for the given key is not a {@link Number}
     */
    public long getLong(@NotNull(value = "key cannot be null") final String key) throws IllegalArgumentException, NullPointerException {
        Objects.checkArgument(!key.isEmpty(), "key cannot be null or effectively null");

        final Object result = this.get(key);
        if (result == null) {
            this.throwError(key);
        }

        return this.toNumber(result).longValue();
    }

    /**
     * @param key The key
     * @param def The default value to return
     *
     * @return The {@link Long} value or {@code def} if the given key has no value
     *
     * @throws IllegalArgumentException If the value for the given key is not a {@link Number}
     */
    public long getLong(@NotNull(value = "key cannot be null") final String key,
                        final long def) throws IllegalArgumentException {
        Objects.checkArgument(!key.isEmpty(), "key cannot be null or effectively null");

        final Object result = this.get(key);
        if (result == null) {
            return def;
        }

        return this.toNumber(result).longValue();
    }

    /**
     * @param key The key
     *
     * @return The {@link Float} value
     *
     * @throws IllegalArgumentException If the key is null or effectively null (empty)
     * @throws NullPointerException     If there is no value for the given key
     * @throws IllegalArgumentException If the value for the given key is not a {@link Number}
     */
    public float getFloat(@NotNull(value = "key cannot be null") final String key) throws IllegalArgumentException, NullPointerException {
        Objects.checkArgument(!key.isEmpty(), "key cannot be null or effectively null");

        final Object result = this.get(key);
        if (result == null) {
            this.throwError(key);
        }

        return this.toNumber(result).floatValue();
    }

    /**
     * @param key The key
     * @param def The default value to return
     *
     * @return The {@link Float} value or {@code def} if the given key has no value
     *
     * @throws IllegalArgumentException If the value for the given key is not a {@link Number}
     */
    public float getFloat(@NotNull(value = "key cannot be null") final String key,
                          final long def) throws IllegalArgumentException {
        Objects.checkArgument(!key.isEmpty(), "key cannot be null or effectively null");

        final Object result = this.get(key);
        if (result == null) {
            return def;
        }

        return this.toNumber(result).floatValue();
    }

    /**
     * @param key The key
     *
     * @return The {@link Double} value
     *
     * @throws IllegalArgumentException If the key is null or effectively null (empty)
     * @throws NullPointerException     If there is no value for the given key
     * @throws IllegalArgumentException If the value for the given key is not a {@link Number}
     */
    public double getDouble(@NotNull(value = "key cannot be null") final String key) throws IllegalArgumentException, NullPointerException {
        Objects.checkArgument(!key.isEmpty(), "key cannot be null or effectively null");

        final Object result = this.get(key);
        if (result == null) {
            this.throwError(key);
        }

        return this.toNumber(result).doubleValue();
    }

    /**
     * @param key The key
     * @param def The default value to return
     *
     * @return The {@link Double} value or {@code def} if the given key has no value
     *
     * @throws IllegalArgumentException If the value for the given key is not a {@link Number}
     */
    public double getDouble(@NotNull(value = "key cannot be null") final String key,
                            final double def) throws IllegalArgumentException {
        Objects.checkArgument(!key.isEmpty(), "key cannot be null or effectively null");

        final Object result = this.get(key);
        if (result == null) {
            return def;
        }

        return this.toNumber(result).doubleValue();
    }

    /**
     * @param key The key
     *
     * @return The {@link Boolean} value
     *
     * @throws IllegalArgumentException If the value for the given key is not a {@link Boolean}
     */
    public boolean getBoolean(@NotNull(value = "key cannot be null") final String key) throws IllegalArgumentException {
        return this.getBoolean(key, false);
    }

    /**
     * @param key The key
     * @param def The default value to return
     *
     * @return The {@link Boolean} value or {@code def} if the given key has no value
     *
     * @throws IllegalArgumentException If the value for the given key is not a {@link Boolean}
     */
    public boolean getBoolean(@NotNull(value = "key cannot be null") final String key,
                              final boolean def) throws IllegalArgumentException {
        Objects.checkArgument(!key.isEmpty(), "key cannot be null or effectively null");

        final Object result = this.get(key);
        if (result == null) {
            return def;
        }

        return (Boolean) result;
    }

    /**
     * @param key The key
     *
     * @return The {@link String} value
     *
     * @throws IllegalArgumentException If the key is null or effectively null (empty)
     * @throws NullPointerException     If there is no value for the given key
     */
    public String getString(@NotNull(value = "key cannot be null") final String key) throws IllegalArgumentException, NullPointerException {
        Objects.checkArgument(!key.isEmpty(), "key cannot be null or effectively null");

        final Object result = this.get(key);
        if (result == null) {
            this.throwError(key);
        }

        return result.toString();
    }

    /**
     * @param key The key
     * @param def The default value to return
     *
     * @return The {@link String} value or {@code def} if the given key has no value
     *
     * @throws IllegalArgumentException If the key is null or effectively null (empty)
     */
    public String getString(@NotNull(value = "key cannot be null") final String key,
                            @NotNull(value = "def cannot be null") final String def) throws IllegalArgumentException {
        Objects.checkArgument(!key.isEmpty(), "key cannot be null or effectively null");

        final Object result = this.get(key);
        if (result == null || result.toString().isEmpty()) {
            return def;
        }

        return result.toString();
    }

    @Override
    public boolean isModified() {
        return false;
    }

    @Override
    public void save() {

    }
}
