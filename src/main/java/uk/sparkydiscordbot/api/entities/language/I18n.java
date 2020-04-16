package uk.sparkydiscordbot.api.entities.language;

import com.google.common.collect.Maps;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.sparkydiscordbot.api.entities.utils.Objects;
import uk.sparkydiscordbot.internal.caching.EntityLoadingCache;
import uk.sparkydiscordbot.internal.caching.caches.LanguageCache;

import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

/**
 * Used for internationalization of the bot
 */
public class I18n {

    /**
     * The fallback {@link I18n} instance
     * <p>
     * <p>Should an {@link I18n} instance not be able
     * to handle a query, this should be queried instead.</p>
     */
    private static I18n FALLBACK_I18N = new I18n(Language.ENGLISH);

    private static final Logger LOGGER = LoggerFactory.getLogger(I18n.class);
    private static final Pattern DOUBLE_QUOTE = Pattern.compile("''");

    private static final Map<String, I18n> REGISTRY_MAP = Maps.newHashMap();
    private static final EntityLoadingCache<String, I18n> LANGUAGE_CACHE = new LanguageCache();

    /**
     * @return The default {@link I18n}
     */
    public static I18n getDefaultI18n() {
        return FALLBACK_I18N;
    }

    /**
     * Retrieve an {@link I18n} instance based on the specified {@link Language} of the {@link Guild}
     * <p>
     * <p>Should the {@link Guild} have no configured language, the default returned is the english {@link I18n}</p>
     *
     * @param guild The {@link Guild}
     *
     * @return The {@link I18n} instance
     */
    public static I18n getI18n(@NotNull(value = "guild cannot be null") final Guild guild) {
        return getI18n(guild.getIdLong());
    }

    /**
     * Retrieve an {@link I18n} instance based on the specified {@link Language} of the {@link Guild}
     * <p>
     * <p>Should the {@link Guild} have no configured language, the default returned is the english version</p>
     *
     * @param id The guild id as a {@link Long}
     *
     * @return The {@link I18n} instance
     */
    public static I18n getI18n(final long id) {
        return getI18n(Long.toString(id));
    }

    /**
     * Retrieve an {@link I18n} instance based on the specified {@link Language} of the {@link Guild}
     * <p>
     * <p>Should the {@link Guild} have no configured language, the default returned is the english {@link I18n}</p>
     *
     * @param id The guild id as a {@link String}
     *
     * @return The {@link I18n} instance
     *
     * @throws NumberFormatException If {@code id} is not convertible to a {@link Long}
     */
    public static I18n getI18n(@NotNull(value = "id cannot be null") final String id) {
        return LANGUAGE_CACHE.getEntity(id, getDefaultI18n());
    }

    /**
     * Retrieve an {@link I18n} instance by {@link Language}
     * <p>
     * <p>If there is no {@link I18n} for the requested {@link Language}, the default returned
     * is {@link Language#ENGLISH}.</p>
     *
     * @param language The {@link Language}
     *
     * @return The {@link I18n} instance
     */
    public static I18n getI18n(@NotNull(value = "language cannot be null") final Language language) {
        return REGISTRY_MAP.getOrDefault(language.getCode(), getDefaultI18n());
    }

    /**
     * Load translation sources of a {@link Class}
     * <p>
     * <p>Should any identical translation keys already exist, they will be overwritten.</p>
     *
     * @param clazz The {@link Class}
     */
    public static void loanI18n(@NotNull(value = "clazz cannot be null") final Class clazz) {

        for (final Language language : Language.values()) {

            final I18n i18n = REGISTRY_MAP.computeIfAbsent(language.getCode(), __ -> new I18n(language));

            final ResourceBundle bundle = getBundle(language, clazz);
            if (bundle == null) {
                LOGGER.warn(String.format("Missing translation file %s.properties in class %s", language.getCode(), clazz.getSimpleName()));
            } else {
                i18n.load(bundle);
            }

        }

    }

    private final Language language;
    private final Map<String, String> translations;

    private I18n(@NotNull(value = "language cannot be null") final Language language) {
        this.language = language;
        this.translations = Maps.newHashMap();
    }

    /**
     * @return The {@link Language}
     */
    public Language getLanguage() {
        return this.language;
    }

    /**
     * Retrieve the value of an associated key
     *
     * @param key The key
     *
     * @return The associated {@link String} value, or {@code null} if no translation exists
     */
    public String getString(final String key) {
        return this.translations.get(key);
    }

    /**
     * Translate the input string
     *
     * @param key  The message key
     * @param args The arguments
     *
     * @return The translated string
     */
    public String tl(@NotNull(value = "key cannot be null") final String key,
                     final Object... args) {
        Objects.checkArgument(!key.isEmpty(), "key cannot be effectively null");

        if (args.length == 0) {
            return DOUBLE_QUOTE.matcher(getMessage(key)).replaceAll("'");
        } else {
            return MessageFormat.format(getMessage(key), args);
        }
    }

    /**
     * @param key The message key
     *
     * @return The string
     */
    private String getMessage(@NotNull(value = "key cannot be null") final String key) {
        Objects.checkArgument(!key.isEmpty(), "key cannot be effectively null");

        String translation = getString(key);
        if (translation == null) {
            translation = getDefaultI18n().getString(key);
        }

        return (translation == null ? key : translation);
    }

    /**
     * Load all key/value pairs from a {@link ResourceBundle} into this {@link I18n}
     *
     * @param bundle The {@link ResourceBundle} to load from
     */
    private void load(@NotNull(value = "bundle cannot be null") final ResourceBundle bundle) {

        final Enumeration<String> iterator = bundle.getKeys();
        while (iterator.hasMoreElements()) {
            final String key = iterator.nextElement();
            this.translations.put(key, bundle.getString(key));
        }

    }

    private static ResourceBundle getBundle(final Language language, final Class clazz) {
        try {
            return ResourceBundle.getBundle("lang." + language.getCode(), language.getLocale(), clazz.getClassLoader());
        } catch (final MissingResourceException ignored){
        }
        return null;
    }

}
