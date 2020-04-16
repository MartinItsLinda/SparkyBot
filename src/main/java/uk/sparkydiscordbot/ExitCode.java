package uk.sparkydiscordbot;

public final class ExitCode {

    /**
     * Shutdown without restarting.
     */
    public static final int EXIT_CODE_NORMAL = 0;

    /**
     * Just restart.
     * <p>
     * <p>This is expected to be used when non-programmatic changes are made.
     * For example config changes or a new module is added / one is removed</p>
     */
    public static final int EXIT_CODE_RESTART = 1;

    /**
     * Apply new updates and restart.
     * <p>This exit code will download the <strong>latest</strong>
     * whether that build be fully tested and bug free or not</p>
     */
    public static final int EXIT_CODE_UPDATE_LATEST = 2;

    /**
     * Apply new updates and restart.
     * <p>This exit code will only ever download the latest recommended version.
     * This is the preferred update code when restarting in a non-development environment</p>
     */
    public static final int EXIT_CODE_UPDATE_RECOMMENDED = 3;

    private ExitCode() {
    }

}
