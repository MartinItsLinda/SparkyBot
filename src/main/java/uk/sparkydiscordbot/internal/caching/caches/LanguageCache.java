package uk.sparkydiscordbot.internal.caching.caches;

import com.google.common.cache.CacheLoader;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.sparkydiscordbot.api.entities.configuration.GuildSettings;
import uk.sparkydiscordbot.api.entities.language.I18n;
import uk.sparkydiscordbot.api.entities.language.Language;
import uk.sparkydiscordbot.api.entities.language.exception.LanguageNotSupportedException;
import uk.sparkydiscordbot.internal.caching.EntityLoadingCache;

public class LanguageCache extends EntityLoadingCache<String, I18n> {

    public LanguageCache(){
        super(new LanguageCacheLoader());
    }

    private static final class LanguageCacheLoader extends CacheLoader<String, I18n> {

        private static final Logger LOGGER = LoggerFactory.getLogger(LanguageCacheLoader.class);

        @Override
        public I18n load(@NotNull(value = "key cannot be null") final String key) {
            final GuildSettings settings = GuildSettings.getConfig(key);

            final String code = settings.getString("language", "en_GB");
            try {
                return I18n.getI18n(Language.getByCode(code));
            } catch (final LanguageNotSupportedException ignored) {
                LOGGER.info(String.format("Guild %s requested an unsupported language: %s", key, code));
            }

            return I18n.getI18n(Language.ENGLISH);
        }

    }

}
