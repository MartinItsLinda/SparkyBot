package uk.sparkydiscordbot;

import org.jetbrains.annotations.NotNull;
import uk.sparkydiscordbot.api.entities.utils.Objects;

public enum Environment {

    /*
     * NOTE: Unless it is a release environment, the prefix MUST be specified.
     * Expect null pointers if you don't as I'm not allowing for it because it shouldn't happen
     */

    DEVELOPMENT("development", "!>"),
    TESTFLIGHT("testflight", "!>"),
    RELEASE("release", true);

    /**
     * The environment token queried to determine the value of {@link Environment#getEnvironment()}
     */
    private static final String ENVIRONMENT_VARIABLE = "bot_env";

    private final String name;
    private final String prefix;
    private final boolean release;

    Environment(@NotNull(value = "name cannot be null") final String name) {
        this(name, null, false);
    }

    Environment(@NotNull(value = "name cannot be null")final String name,
                final boolean release) {
        this(name, null, release);
    }

    Environment(@NotNull(value = "name cannot be null")final String name,
                final String prefix) {
        this(name, prefix, false);
    }

    Environment(@NotNull(value = "name cannot be null") final String name,
                final String prefix,
                final boolean release) {
        this.name = name;
        this.prefix = prefix;
        this.release = release;
    }

    /**
     * @return The human readable names for the current environment
     */
    public String getName() {
        return this.name;
    }

    /**
     * @return The default prefix used in this {@link Environment}
     */
    public String getPrefix() {
        return this.prefix;
    }

    /**
     * @return Whether the current environment is a release environment
     */
    public boolean isRelease() {
        return this.release;
    }

    @Override
    public String toString() {
        return "Environment{name=" + this.name + ", prefix=" + this.prefix + ", isRelease=" + this.release + "}";
    }

    private static Environment environment;

    /**
     * Gets the current {@link Environment} setting.
     *
     * <p>The {@link Environment} is determined by the environment
     * variable bot_env</p>
     *
     * <p>Should no environment variable be set, the default is {@link Environment#DEVELOPMENT}</p>
     *
     * @return The current {@link Environment} setting
     */
    public static Environment getEnvironment() {

        Environment result = environment;
        if (result == null) {
            synchronized (Environment.class) {
                result = environment;
                if (result == null) {

                    String env = System.getenv(Environment.ENVIRONMENT_VARIABLE);
                    if (env == null) {
                        result = Environment.DEVELOPMENT;
                    } else {
                        result = Environment.getByName(env);
                    }

                    environment = result;

                }
            }
        }

        return result;
    }

    /**
     * Get an {@link Environment} type by its names
     *
     * @param env The environment type
     *
     * @return The {@link Environment}, or {@code null} if the environment type is unknown.
     */
    public static Environment getByName(@NotNull(value = "env cannot be null") final String env) {
        Objects.checkArgument(!env.isEmpty(), "environment cannot be effectively null");
        for (final Environment environment : values()) {
            if (environment.name.equalsIgnoreCase(env)) {
                return environment;
            }
        }
        return null;
    }

}
