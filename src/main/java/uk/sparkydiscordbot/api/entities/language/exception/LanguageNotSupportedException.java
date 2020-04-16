package uk.sparkydiscordbot.api.entities.language.exception;

public class LanguageNotSupportedException extends Exception {
    
    private final String language;

    /**
     * @param language The language
     */
    public LanguageNotSupportedException(final String language) {
        this.language = language;
    }

    /**
     * @return The language
     */
    public String getLanguage() {
        return this.language;
    }
}
