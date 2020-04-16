package uk.sparkydiscordbot.api.entities.command.parsers;

import com.google.common.collect.Lists;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;
import uk.sparkydiscordbot.api.entities.command.CommandContext;
import uk.sparkydiscordbot.api.entities.command.argument.Argument;
import uk.sparkydiscordbot.api.entities.command.argument.ArgumentParser;
import uk.sparkydiscordbot.api.entities.command.argument.RawArguments;
import uk.sparkydiscordbot.api.entities.language.I18n;
import uk.sparkydiscordbot.api.entities.utils.FinderUtil;
import uk.sparkydiscordbot.api.exception.parser.ParserException;

import java.util.*;

public class ArgumentParsers {

    /**
     * Parse text input. This parsers allows strings with white space if it receives unescaped single or double quotes
     */
    public static final ArgumentParser<String> STRING = new ArgumentParser<String>() {
        @Override
        public String getTypeName(final I18n i18n) {
            return i18n.tl("parserStringTypeName");
        }

        @Override
        public String parse(final CommandContext context,
                            final RawArguments args) {
            if (args.peek() == null) {
                return null;
            }

            final String first = args.next();
            final char qc = first.charAt(0);

            if (qc == '"' || qc == '\'') {

                final StringBuilder builder = new StringBuilder();
                builder.append(first, 1, first.length());

                while (args.peek() != null) {

                    final String next = args.next();
                    final char ch = next.charAt(next.length() - 1);

                    if (ch == qc) {
                        return builder.append(next, 0, next.length() - 1).toString();
                    } else {
                        builder.append(next).append(" ");
                    }

                }

                throw new ParserException(context.i18n("parserStringUnfinishedQuotedString"));

            }

            return first;
        }

    };

    /**
     * Parses the remaining text input from passed {@link RawArguments} instance
     */
    public static final ArgumentParser<String> REMAINING_STRING = new ArgumentParser<String>() {
        @Override
        public String getTypeName(final I18n i18n) {
            return i18n.tl("parserStringTypeName");
        }

        @Override
        public String parse(final CommandContext context,
                            final RawArguments args) {
            final StringBuilder builder = new StringBuilder();
            while (args.peek() != null) {
                builder.append(STRING.parse(context, args)).append(" ");
            }
            return builder.toString().trim();
        }

    };

    /**
     * Parses the remaining text input from passed {@link RawArguments} instance
     */
    public static final ArgumentParser<String> REMAINING_STRING_NO_QUOTE = new ArgumentParser<String>() {
        @Override
        public String getTypeName(final I18n i18n) {
            return i18n.tl("parserStringTypeName");
        }

        @Override
        public String parse(final CommandContext context,
                            final RawArguments args) {
            final StringBuilder builder = new StringBuilder();
            while (args.peek() != null) {
                builder.append(args.next()).append(" ");
            }
            return builder.toString().trim();
        }

    };

    /**
     * Parse numerical input as an {@link Integer}
     */
    public static final ArgumentParser<Integer> INTEGER = new ArgumentParser<Integer>() {
        @Override
        public String getTypeName(final I18n i18n) {
            return i18n.tl("parserNumberTypeName");
        }

        @Override
        public Integer parse(final CommandContext context,
                             final RawArguments args) {
            final String parsed = args.next();
            try {
                return Integer.parseInt(parsed);
            } catch (final NumberFormatException ex) {
                throw new ParserException(context.i18n("parserNumberInvalidNumber"));
            }
        }

    };

    /**
     *
     */
    public static final ArgumentParser<Integer[]> INTEGER_RANGE = new ArgumentParser<Integer[]>() {

        @Override
        public List<String> getSuggestions() {
            return Lists.newArrayList("1-15", "12-19", "5-8");
        }

        @Override
        public String getTypeName(final I18n i18n) {
            return i18n.tl("parserIntegerRangeTypeName");
        }

        @Override
        public Integer[] parse(final CommandContext context,
                               final RawArguments args) {

            final String peek = args.peek();
            if (peek.indexOf('-') == -1) {
                throw new ParserException("No range specified");
            }

            final String[] range = args.next().split("-");
            if (range.length != 2) {
                throw new ParserException("Invalid range length, must be equal to 2 arguments");
            }

            final int from = Integer.parseInt(range[0]);
            final int to = Integer.parseInt(range[1]);

            if (from > to) {
                throw new ParserException("From cannot be greater than to");
            }

            final List<Integer> integers = Lists.newArrayList();

            for (int i = from; i <= to; i++) {
                integers.add(i);
            }

            return integers.toArray(new Integer[integers.size() - 1]);
        }

    };

    /**
     *
     */
    public static final ArgumentParser<Integer[]> INTEGER_ARRAY = new ArgumentParser<Integer[]>() {

        @Override
        public List<String> getSuggestions() {
            return Lists.newArrayList("1;15;12;3", "9;2;11;6;5", "17;13");
        }

        @Override
        public String getTypeName(final I18n i18n) {
            return i18n.tl("parserIntegerArrayTypeName");
        }

        @Override
        public Integer[] parse(final CommandContext context,
                               final RawArguments args) {
            final String[] split = args.next().split(";");

            final List<Integer> integers = Lists.newArrayList();

            for (final String parse : split) {
                try {
                    integers.add(Integer.parseInt(parse));
                } catch (final NumberFormatException ignored) {
                    throw new ParserException(context.i18n("parserNumberInvalidNumber"));
                }
            }

            Collections.sort(integers);

            return integers.toArray(new Integer[integers.size() - 1]);
        }
    };

    /**
     * Parse numerical input as a {@link Long}
     */
    public static final ArgumentParser<Long> LONG = new ArgumentParser<Long>() {
        @Override
        public String getTypeName(final I18n i18n) {
            return i18n.tl("parserNumberTypeName");
        }

        @Override
        public Long parse(final CommandContext context,
                          final RawArguments args) {
            final String parsed = args.next();
            try {
                return Long.parseLong(parsed);
            } catch (final NumberFormatException ex) {
                throw new ParserException(context.i18n("parserNumberInvalidNumber"));
            }
        }

    };

    /**
     * Parse numerical input as a {@link Double}
     */
    public static final ArgumentParser<Double> DOUBLE = new ArgumentParser<Double>() {
        @Override
        public String getTypeName(final I18n i18n) {
            return i18n.tl("parserNumberTypeName");
        }

        @Override
        public Double parse(final CommandContext context,
                            final RawArguments args) {
            final String parsed = args.next();
            try {
                return Double.parseDouble(parsed);
            } catch (final NumberFormatException ex) {
                throw new ParserException(context.i18n("parserNumberInvalidNumber"));
            }
        }

    };

    /**
     * Parse numerical input as an {@link Long}, throwing a {@link ParserException} should the value parsed be less than 1
     */
    public static final ArgumentParser<Long> NON_NEGATIVE_NUMBER = new ArgumentParser<Long>() {
        @Override
        public String getTypeName(final I18n i18n) {
            return i18n.tl("parserNumberTypeName");
        }

        @Override
        public Long parse(final CommandContext context,
                             final RawArguments args) {
            final String parsed = args.next();
            try {
                final long result = Long.parseLong(parsed);
                if (result < 1) {
                    throw new ParserException(context.i18n("parserNumberNotNegative"));
                }
                return result;
            } catch (final NumberFormatException ex) {
                throw new ParserException(context.i18n("parserInvalidNumber"));
            }
        }
    };

    /**
     * Parse a string into a {@link Member}
     */
    public static final ArgumentParser<Member> MEMBER = new ArgumentParser<Member>() {
        @Override
        public List<String> getSuggestions() {
            return Lists.newArrayList("212530298259374080", "Samuel", "Samuel#0001", "@Samuel#0001");
        }

        @Override
        public String getTypeName(final I18n i18n) {
            return i18n.tl("parserMemberTypeName");
        }

        @Override
        public Member parse(final CommandContext context,
                            final RawArguments args) {
            final StringBuilder builder = new StringBuilder();

            Member member = null;
            while (args.peek() != null) {

                builder.append(args.next()).append(" ");

                final List<Member> members = FinderUtil.findMembers(builder.toString().trim(), context.getServer());
                if (members.size() == 0) {
                    args.rollback();
                    break;
                }

                member = members.get(0);

            }

            if (member == null) {
                throw new ParserException(context.i18n("parserUnknownMember", builder.toString()));
            }

            return member;
        }

    };

    /**
     * Parse a string into a {@link User}
     */
    public static final ArgumentParser<User> USER = new ArgumentParser<User>() {
        @Override
        public String getTypeName(final I18n i18n) {
            return i18n.tl("parserMemberTypeName");
        }

        @Override
        public User parse(final CommandContext context,
                          final RawArguments args) {
            return MEMBER.parse(context, args).getUser(); //search to local guild not global
        }
    };

    /**
     * Return a functionally identical copy of the {@code parser} changing its names
     *
     * @param parser   The {@link ArgumentParser} to rename
     * @param typename The new type names
     *
     * @return A functionally identical {@link ArgumentParser}
     */
    public static <T> ArgumentParser<T> rename(final ArgumentParser<T> parser,
                                               final String typename) {
        return new ArgumentParser<T>() {
            @Override
            public Argument<T> getDefaultParameter() {
                return parser.getDefaultParameter();
            }

            @Override
            public String getTypeName(final I18n i18n) {
                return i18n.tl(typename);
            }

            @Override
            public T parse(final CommandContext context,
                           final RawArguments args) {
                return parser.parse(context, args);
            }

        };
    }

    /**
     * Apply a fallback parameter in the event of none being specified
     *
     * @param parser   The parser
     * @param fallback The fallback parameter
     *
     * @return The {@link ArgumentParser}
     */
    public static <T> ArgumentParser<T> alt(final ArgumentParser<T> parser,
                                            final T fallback) {
        return new ArgumentParser<T>() {
            @Override
            public Argument<T> getDefaultParameter() {
                return Argument.create(fallback);
            }

            @Override
            public String getTypeName(final I18n i18n) {
                return parser.getTypeName(i18n);
            }

            @Override
            public T parse(final CommandContext context,
                           final RawArguments args) {
                return parser.parse(context, args);
            }

        };
    }

    /**
     * Creates an {@link ArgumentParser} that can resolve any parameter type with a corresponding parser
     *
     * @param parser1     a parser
     * @param parser2     another parser
     * @param moreParsers more parsers...
     *
     * @return The {@link ArgumentParser}
     */
    public static ArgumentParser<Object> anyOf(@NotNull(value = "parser1 cannot be null") final ArgumentParser<?> parser1,
                                               @NotNull(value = "parser2 cannot be null") final ArgumentParser<?> parser2,
                                               final ArgumentParser<?>... moreParsers) {
        return new ArgumentParser<Object>() {

            private final ArgumentParser<?>[] parsers;

            {
                this.parsers = new ArgumentParser<?>[2 + moreParsers.length];

                this.parsers[0] = parser1;
                this.parsers[1] = parser2;

                System.arraycopy(moreParsers, 0, this.parsers, 2, moreParsers.length);
            }

            @Override
            public String getTypeName(final I18n i18n) {
                final StringBuilder sb = new StringBuilder();
                for (final ArgumentParser parser : moreParsers) {
                    sb.append('|').append(parser.getTypeName(i18n));
                }
                return sb.toString().trim();
            }

            @Override
            public Object parse(final CommandContext context,
                                final RawArguments args) {
                final StringBuilder errors = new StringBuilder();
                for (final ArgumentParser<?> parser : this.parsers) {
                    try {
                        return parser.tryParse(context, args);
                    } catch (final Exception ex) {
                        errors.append(ex.getMessage()).append("\n");
                    }
                }
                throw new ParserException(errors.toString());
            }
        };
    }

    /**
     * Creates an {@link ArgumentParser} that will only accept any of the provided choices
     *
     * @param parser  The {@link ArgumentParser} used
     * @param choice  a choice
     * @param choices more choices
     *
     * @return The {@link ArgumentParser}
     */
    public static <T> ArgumentParser<T> options(final ArgumentParser<T> parser,
                                                final String choice,
                                                final String... choices) {
        return new ArgumentParser<T>() {

            private final Set<String> options;
            private final String typename;

            {
                this.options = new HashSet<>();
                this.options.add(choice);
                this.options.addAll(Arrays.asList(choices));

                if (choices.length == 0) {
                    this.typename = choice;
                } else {
                    final StringBuilder builder = new StringBuilder(choice);
                    for (final String option : choices) {
                        builder.append('|').append(option);
                    }
                    this.typename = builder.toString().trim();
                }
            }

            @Override
            public String getTypeName(final I18n i18n) {
                return this.typename;
            }

            @Override
            public T parse(final CommandContext context,
                           final RawArguments args) {
                final String parse = STRING.parse(context, args);

                for (final String option : this.options) {
                    if (option.equalsIgnoreCase(parse)) {
                        return parser.parse(context, new RawArguments(new String[]{option}));
                    }
                }

                throw new ParserException(context.i18n("", parse));
            }

        };
    }

    private ArgumentParsers() {
    }

}
