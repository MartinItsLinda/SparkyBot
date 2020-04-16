package uk.sparkydiscordbot.api.entities.command;

import com.google.common.collect.Lists;
import org.jetbrains.annotations.NotNull;
import uk.sparkydiscordbot.api.entities.command.argument.Argument;
import uk.sparkydiscordbot.api.entities.command.argument.ArgumentParser;
import uk.sparkydiscordbot.api.entities.command.argument.Parameter;
import uk.sparkydiscordbot.api.entities.command.argument.RawArguments;
import uk.sparkydiscordbot.api.exception.command.CommandSyntaxException;
import uk.sparkydiscordbot.api.exception.parser.ParserException;

import java.util.Arrays;
import java.util.List;

public class Arguments {

    private final RawArguments args;
    private final CommandContext context;
    private final List<Command.Flag> flags;
    private final List<ArgumentParser<?>> syntax;

    private int current;

    /**
     * @param context The {@link CommandContext}
     * @param args    The {@link RawArguments} to parse
     * @param flags   The flags executed with the {@link Command}
     *
     * @throws CommandSyntaxException If an error occurred during parsing and no fallback parameter was provided by the
     *                                {@link ArgumentParser}
     * @throws ParserException        If a parser error occurred
     */
    public Arguments(final CommandContext context,
                     final RawArguments args,
                     final List<Command.Flag> flags) throws CommandSyntaxException, ParserException {
        this.args = args;
        this.context = context;
        this.flags = flags;
        this.syntax = Lists.newArrayList();
    }

    /**
     * @return The {@link RawArguments}
     */
    public RawArguments getRawArguments() {
        return this.args;
    }

    /**
     * @return The syntax generated as a result of calls to {@link #nextParameter(ArgumentParser)}
     */
    public String getSyntax() {

        final StringBuilder builder = new StringBuilder();

        for (final ArgumentParser<?> parser : this.syntax) {

            builder.append(" ");
            if (parser.getDefaultParameter().getValue() == null && !parser.getDefaultParameter().isNullable()) {
                builder.append("<").append(parser.getTypeName(this.context.getI18n())).append(">");
            } else {
                builder.append("[").append(parser.getTypeName(this.context.getI18n())).append("]");
            }

        }

        return builder.toString();
    }

    /**
     * Retrieve the value of the next {@link Parameter}
     *
     * @param parser The {@link ArgumentParser} to use
     *
     * @return The value
     *
     * @throws ClassCastException    If the {@link Parameter} isn't castable to the requested type
     * @throws IllegalStateException If no more parameters are left to retrieve
     */
    public <T> T next(final ArgumentParser<T> parser) throws IllegalStateException {
        return this.nextParameter(parser).getValue();
    }

    /**
     * Request the next parsed {@link Parameter}
     *
     * @param parser The {@link ArgumentParser} to use
     *
     * @return The {@link Parameter}
     *
     * @throws IllegalStateException  If there are no arguments
     * @throws CommandSyntaxException If there are no more elements to retrieve
     * @throws ClassCastException     If the {@link Parameter#getValue()} isn't castable to the requested type
     */
    public <T> Parameter<T> nextParameter(final ArgumentParser<T> parser) throws CommandSyntaxException {
        final Argument<T> argument = parser.getDefaultParameter();
        final String parse = this.args.peek();

        //Sometimes a command may want null instead of just erroring like with >bal in economy
        //You have two options, >bal or >bal @member, nobody wants to >bal @self
        if ((parse == null || parse.trim().isEmpty()) && argument.getValue() == null && !argument.isNullable()) {
            throw new CommandSyntaxException(this.context.getLabel(), this.context.getCommand().getUsage());
        } else {
            final Parameter<T> parameter;
            if (parse == null || parse.isEmpty()) {
                parameter = new Parameter<>("", argument.getValue());
            } else {
                parameter = new Parameter<>(parse, parser.parse(this.context, args));
            }
            return parameter;
        }
    }

    /**
     * @return {@code true} if the command was executed as a dry-start, {@code false} otherwise
     */
    public boolean isDry() {
        return !this.args.hasNext();
    }

    /**
     * @return The total amount of parsed {@link Parameter}s
     */
    public int length() {
        return this.args.length();
    }

    /**
     * Retrieves a {@link Command.Flag}
     *
     * @param flag The flag to get
     *
     * @return The flags value if it was present, or {@code null} if no flag by that name was passed.
     */
    public Command.Flag getFlag(@NotNull(value = "flag cannot be null") final String flag) {
        return this.flags.stream().filter(pair -> pair.getName().equalsIgnoreCase(flag)).findFirst().orElse(null);
    }

    /**
     * Check for the presence of a flag
     *
     * @param flag The flag to check
     *
     * @return {@code true} if the {@code flag} was present, {@code false} otherwise
     */
    public boolean hasFlag(@NotNull(value = "flag cannot be null") final String flag) {
        return this.getFlag(flag) != null;
    }

    /**
     * Join all arguments provided when executing the command into one {@link String}
     *
     * @param joiner The {@link CharSequence} of which to join the parameters by
     *
     * @return The joined {@link String}
     */
    public String join(final CharSequence joiner) {
        final StringBuilder builder = new StringBuilder();

        for (int i = 0; i < this.args.length(); i++) {
            if (i != 0) {
                builder.append(joiner);
            }
            builder.append(this.args.getRaw(i));
        }

        return builder.toString();
    }

    /**
     * Copy this {@link Arguments}
     *
     * @return A copy of this {@link Arguments}
     */
    public Arguments copy() {

        final Arguments args = new Arguments(this.context, this.args, this.flags);

        args.syntax.addAll(this.syntax);

        return args;
    }

    @Override
    public String toString() {
        return "Arguments{raw=" + this.getRawArguments().toString() + ", context=" + this.context.toString() +
                ", flags=" + Arrays.toString(this.flags.toArray()) + "}";
    }

}
