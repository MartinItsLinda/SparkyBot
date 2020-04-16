package uk.sparkydiscordbot.api.entities.language;

import org.jetbrains.annotations.NotNull;
import uk.sparkydiscordbot.api.entities.language.exception.LanguageNotSupportedException;

import java.util.Locale;

/**
 * Represents a list of supported languages used for localization of the bot
 * <p>
 * <p>New languages should only be added to this ever growing list once
 * their associated resource bundles have been completed.</p>
 * <p>
 * <p>If a lang doesn't have a translated value for its key,
 * the default value should be english.</p>
 */
public enum Language {

    /**
     * British English
     */
    ENGLISH("en", "GB", "English", "English"),

    /**
     * Welsh
     */
//    WELSH("cy", "GB", "Cymraeg", "Welsh"),

    /**
     * French
     */
    FRENCH("fr", "FR", "Français", "French");

    /**
     * Retrieve a lang by its country code
     *
     * @param code The lang code
     *
     * @return The {@link Language}
     *
     * @throws LanguageNotSupportedException If the requested language is not currently supported
     */
    public static Language getByCode(@NotNull(value = "id cannot be null") final String code) throws LanguageNotSupportedException {
        for (final Language language : Language.values()) {
            if (language.getCode().equalsIgnoreCase(code)) {
                return language;
            }
        }
        throw new LanguageNotSupportedException(code);
    }

    private final String code;
    private final Locale locale;
    private final String nativeName;
    private final String englishName;

    Language(final String language,
             final String country,
             final String nativeName,
             final String englishName) {
        this.code = language.toLowerCase() + "_" + country.toUpperCase();
        this.locale = new Locale(language, country);
        this.nativeName = nativeName;
        this.englishName = englishName;
    }

    /**
     * @return The language code
     */
    public String getCode() {
        return this.code;
    }

    /**
     * @return The {@link Locale}
     */
    public Locale getLocale() {
        return this.locale;
    }

    /**
     * @return The way this {@link Language} is natively spelt
     */
    public String getNativeName() {
        return this.nativeName;
    }

    /**
     * @return The english spelling
     */
    public String getEnglishName() {
        return this.englishName;
    }

}
