package uk.sparkydiscordbot.api.entities.command.argument;

import com.google.common.collect.Lists;
import uk.sparkydiscordbot.api.entities.command.CommandContext;
import uk.sparkydiscordbot.api.entities.language.I18n;
import uk.sparkydiscordbot.api.entities.language.Language;
import uk.sparkydiscordbot.api.exception.parser.ParserException;

import java.util.List;

public abstract class ArgumentParser<T> {

    /**
     * Return the default parameter value to provide if no argument was present
     *
     * @return The default parameter, this cannot be {@code null}
     */
    public Argument<T> getDefaultParameter() {
        return Argument.empty();
    }

    /**
     * Get a {@link List} of example command arguments.
     *
     * @return A {@link List} of example command arguments.
     */
    public List<String> getSuggestions() {
        return Lists.newArrayList();
    }

    /**
     * Return the (member-friendly) identifier for this {@link ArgumentParser}
     *
     * @param i18n The {@link I18n} instance for translations
     *
     * @return The identifier for this {@link ArgumentParser}
     */
    public abstract String getTypeName(final I18n i18n);

    /**
     * Attempts to parse the provided {@link RawArguments} instance to the desired type
     *
     * <p>Should a {@link ParserException} be thrown, the {@link RawArguments} will be rolled back
     * to its state before the attempted parse</p>
     *
     * @param context The {@link CommandContext}
     * @param args    The {@link RawArguments} to parse
     *
     * @return The parsed result, or {@code null} if an error occurred
     */
    public final T tryParse(final CommandContext context,
                            final RawArguments args) throws Exception {
        final String[] raw = args.getRaw();

        final Exception ex;
        try {
            final T obj = this.parse(context, args);

            //not entirely sure on this
            if (obj == null && this.getDefaultParameter().getValue() != null) {
                return this.getDefaultParameter().getValue();
            }

            return obj;
        } catch (final Exception thrown) {
            ex = thrown;
        }

        args.setRaw(raw);

        throw ex;
    }

    /**
     * Attempts to parse the remaining arguments passed by the {@link RawArguments} instance to the desired type
     * <p>
     * <p>Should any issues occur, a {@link ParserException} should be thrown.</p>
     *
     * @param context The {@link CommandContext}
     * @param args    The {@link RawArguments} to parse
     *
     * @return The parsed result
     */
    public abstract T parse(final CommandContext context,
            final RawArguments args);

    @Override
    public String toString() {
        return "ArgumentParser{name=" + this.getTypeName(I18n.getI18n(Language.ENGLISH)) + ", default=" + this.getDefaultParameter().toString() + "}";
    }
}
