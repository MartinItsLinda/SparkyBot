package uk.sparkydiscordbot.api.entities.command;

import com.google.common.collect.Lists;
import org.jetbrains.annotations.NotNull;
import uk.sparkydiscordbot.api.entities.utils.Objects;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

public final class Command {

    private final String[] names;
    private final Collection<String> examples;
    private final String description;
    private final String usage;
    private final long cooldown;
    private final CommandExecutor executor;

    private Command parent; //used to build up things such as command usage

    /**
     * @param names       The names and aliases of this {@link Command}
     * @param examples    The examples of this {@link Command}
     * @param description The description of this {@link Command}
     * @param usage       The usage of this {@link Command}
     * @param cooldown    The cooldown of this {@link Command}
     * @param executor    The {@link CommandExecutor} for this {@link Command}
     */
    private Command(@NotNull(value = "names cannot be null") final String[] names,
                    @NotNull(value = "examples cannot be null") final Collection<String> examples,
                    final String description,
                    final String usage,
                    final long cooldown,
                    @NotNull(value = "executor cannot be null") final CommandExecutor executor) {
        Objects.checkArgument(names.length > 0, "names cannot be empty");
        this.names = names;
        this.examples = examples;
        this.description = description;
        this.usage = usage;
        this.cooldown = cooldown;
        this.executor = executor;
    }

    /**
     * @return The names of this {@link Command}
     */
    public final String getName() {
        return this.names[0];
    }

    /**
     * @return A {@link Collection} containing every alias for this {@link Command}
     */
    public Collection<String> getAliases() {
        return Lists.newArrayList(this.names);
    }

    /**
     * @return A {@link Collection} containing every example for this {@link Command}
     */
    public Collection<String> getExamples() {
        return Lists.newArrayList(this.examples);
    }

    /**
     * @return The description of this {@link Command}
     */
    public final String getDescription() {
        return this.description;
    }

    /**
     * @return The usage of this {@link Command}
     */
    public final String getUsage() {
        return this.usage;
    }

    /**
     * @return How frequently this {@link Command} may be executed by a member in milliseconds
     */
    public long getCooldown() {
        return this.cooldown;
    }

    /**
     * @return The {@link CommandExecutor} for this {@link Command}
     */
    public CommandExecutor getExecutor() {
        return this.executor;
    }

    /**
     * Creates a new {@link Command.Builder}
     *
     * @return The {@link Command.Builder}
     */
    public static Builder builder() {
        return new Command.Builder();
    }

    /**
     *
     */
    public static final class Builder {

        private String[] names;
        private Collection<String> examples;
        private String description;
        private String usage;
        private long cooldown;
        private CommandExecutor executor;

        /**
         * Creates a new {@link Command.Builder}
         */
        private Builder() {
            this.examples = Lists.newArrayList();
        }

        /**
         * Set the unique names of this {@link Command}
         *
         * @param name The names of this {@link Command}
         *
         * @return This {@link Builder}
         */
        public Builder names(@NotNull(value = "command must have a names") final String name,
                             final String... aliases) {
            Objects.checkArgument(name.length() > 0, "command cannot have an effectively null names");

            final String[] names = new String[1 + aliases.length];
            names[0] = name;

            System.arraycopy(aliases, 0, names, 1, aliases.length);

            this.names = names;

            return this;
        }

        /**
         * @param examples The examples
         *
         * @return This {@link Builder}
         */
        public Builder examples(@NotNull(value = "examples cannot be null") final String... examples) {
            Objects.checkNotNull(examples, "examples cannot be null");
            this.examples = Arrays.asList(examples);
            return this;
        }

        /**
         * @param description The description of this {@link Command}
         *
         * @return This {@link Builder}
         */
        public Builder description(@NotNull(value = "description cannot be null") final String description) {
            this.description = description;
            return this;
        }

        /**
         * @param usage The usage of this {@link Command}
         *
         * @return This {@link Builder}
         */
        public Builder usage(@NotNull(value = "usage cannot be null") final String usage) {
            this.usage = usage;
            return this;
        }

        /**
         * @param cooldown How frequently this command may be executed by a member in seconds
         *
         * @return This {@link Builder}
         */
        public Builder cooldown(final long cooldown) {
            return this.cooldown(cooldown, TimeUnit.SECONDS);
        }

        /**
         * @param cooldown How frequently this command may be executed by a member
         * @param unit     The {@link TimeUnit} to measure the cooldown in
         *
         * @return This {@link Builder}
         */
        public Builder cooldown(final long cooldown,
                                final TimeUnit unit) {
            Objects.checkArgument(cooldown >= 1, "cooldown cannot be less than 1");
            this.cooldown = unit.toMillis(cooldown);
            return this;
        }

        /**
         * @param executor The executor for this {@link Command}
         *
         * @return This {@link Command.Builder}
         */
        public Builder executor(@NotNull(value = "executor cannot be null") final CommandExecutor executor) {
            this.executor = executor;
            return this;
        }

        /**
         * @return The {@link Command}
         *
         * @throws IllegalArgumentException If there was no names specified for this {@link Command}
         * @throws NullPointerException     If this {@link Command} has no {@link CommandExecutor}
         */
        public Command build() {
            Objects.checkArgument(this.names.length > 0, "command must have a names");
            Objects.checkNotNull(this.executor, "command executor cannot be null");
            return new Command(this.names, this.examples, this.description, this.usage, this.cooldown, this.executor);
        }

    }

    /**
     * A class used to represent flags passed with a command
     */
    public static final class Flag {

        private final String name;
        private final String value;

        /**
         * Create a new {@link Flag}
         *
         * @param name  The flag name
         * @param value The flags value, or {@code null} if there is no value
         */
        public Flag(@NotNull(value = "name cannot be null") final String name,
                    final String value) {
            this.name = name;
            this.value = value;
        }

        /**
         * @return The name of this {@link Flag}
         */
        public String getName() {
            return this.name;
        }

        /**
         * @return This {@link Flag}s value, or {@code null} if there is no value
         */
        public String getValue() {
            return this.value;
        }

        /**
         * @return This {@link Flag}s value as an {@link Integer}
         *
         * @throws IllegalArgumentException If the {@code value} is {@code null}
         * @throws NumberFormatException    If the input value is not convertible to an {@link Integer}
         */
        public int getAsInt() throws IllegalArgumentException, NumberFormatException {
            if (this.value == null) {
                throw new IllegalArgumentException("Cannot convert a null value");
            }
            return Integer.parseInt(this.value);
        }

        /**
         * Attempts to convert the input flag value into an {@link Integer}
         * <p>
         * <p>Should the input value be {@code null}, {@code def} is returned</p>
         *
         * @param def The value to return should the input value be {@code null}
         *
         * @return The {@link Integer} value, or {@code def} if no value was passed with the flag
         *
         * @throws NumberFormatException If the input value is not convertible to a {@link Integer}
         */
        public int getAsInt(final int def) throws NumberFormatException {
            if (this.value == null) {
                return def;
            }
            return Integer.parseInt(this.value);
        }

        /**
         * @return This {@link Flag}s value as a {@link Double}
         *
         * @throws IllegalArgumentException If the {@code value} is {@code null}
         * @throws NumberFormatException    If the input value is not convertible to a {@link Double}
         */
        public double getAsDouble() throws IllegalArgumentException {
            if (this.value == null) {
                throw new IllegalArgumentException("Cannot convert a null value");
            }
            return Double.parseDouble(this.value);
        }

        /**
         * Attempts to convert the input flag value into a {@link Double}
         * <p>
         * <p>Should the input value be {@code null}, {@code def} is returned</p>
         *
         * @param def The value to return should the input value be {@code null}
         *
         * @return The {@link Double} value, or {@code def} if no value was passed with the flag
         *
         * @throws NumberFormatException If the input value is not convertible to a {@link Double}
         */
        public double getAsDouble(final int def) throws NumberFormatException {
            if (this.value == null) {
                return def;
            }
            return Double.parseDouble(this.value);
        }

        /**
         * @return This {@link Flag}s value as a {@link Boolean}
         */
        public boolean isTrue() throws IllegalArgumentException {
            if (this.value == null) {
                return false;
            }
            return Boolean.parseBoolean(this.value);
        }

        /**
         * Attempts to convert the input flag value into a {@link Double}
         * <p>
         * <p>Should the input value be {@code null}, {@code def} is returned</p>
         *
         * @param def The value to return should the input value be {@code null}
         *
         * @return The {@link Boolean} value, or {@code def} if no value was passed with the flag
         *
         * @throws NumberFormatException If the input value is not convertible to a {@link Double}
         */
        public boolean is(final boolean def) throws NumberFormatException {
            if (this.value == null) {
                return def;
            }
            return Boolean.parseBoolean(this.value);
        }

        @Override
        public String toString() {
            return "Flag{name=" + this.name + ", value = " + this.value + "}";
        }
    }

}
