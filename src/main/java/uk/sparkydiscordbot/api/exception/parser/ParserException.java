package uk.sparkydiscordbot.api.exception.parser;

public class ParserException extends RuntimeException {

    public ParserException(final Throwable cause) {
        super(cause);
    }

    public ParserException(final String message) {
        super(message);
    }

    public ParserException(final String message,
                           final Throwable cause) {
        super(message, cause);
    }
}
