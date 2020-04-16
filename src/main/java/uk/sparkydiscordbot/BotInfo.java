package uk.sparkydiscordbot;

public class BotInfo {

    /**
     * The name of the Bot
     */
    public static final String BOT_NAME = "Sparky";

    /**
     * Version Major
     */
    public static final String VERSION_MAJOR = "@version_major@";

    /**
     * Version Minor
     */
    public static final String VERSION_MINOR = "@version_minor@";

    /**
     * Version Patch
     */
    public static final String VERSION_PATCH = "@version_patch@";

    /**
     * Current build version
     */
    public static final String VERSION_BUILD = "@version_build@";

    /**
      * The full version string
     */
    @SuppressWarnings("ConstantConditions") //Gradle replaces all the fields with their appropriate values, so stop intellij from giving a warning on it
    public static final String VERSION = VERSION_MAJOR.startsWith("@") ? "Unknown" : String.format("%s.%s.%s_%s", VERSION_MAJOR, VERSION_MINOR, VERSION_PATCH, VERSION_BUILD);

    /**
     * The current git branch
     */
    public static final String GIT_BRANCH = "@git_branch@";

    /**
     * The current git commit id
     */
    public static final String GIT_COMMIT = "@git_commit@";

    /**
     * The short git commit id
     */
    public static final String GIT_COMMIT_SHORT = "@git_commit_short@";

    /**
     * The build author
     */
    public static final String BUILD_AUTHOR = "@build_author@";

    /**
     * The build date
     */
    public static final String BUILD_DATE = "@build_date@";

    private BotInfo() {
    }

}
