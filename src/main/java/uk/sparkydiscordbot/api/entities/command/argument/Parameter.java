package uk.sparkydiscordbot.api.entities.command.argument;

public class Parameter<T> {

    private final String token;
    private final T value;

    /**
     * @param token The token that lead to the value
     * @param value The value
     */
    public Parameter(final String token,
                     final T value) {
        this.token = token;
        this.value = value;
    }

    /**
     * @return The token parsed
     */
    public String getToken() {
        return this.token;
    }

    /**
     * @return The value
     */
    public T getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return "Parameter{token=" + this.token + ", value = " + this.value + "}";
    }
}
