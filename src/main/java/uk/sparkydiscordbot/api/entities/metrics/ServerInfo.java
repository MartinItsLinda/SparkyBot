package uk.sparkydiscordbot.api.entities.metrics;

import com.sun.management.OperatingSystemMXBean;
import uk.sparkydiscordbot.Bot;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ServerInfo {

    /**
     * @return The total amount of memory in MB
     */
    public static long getTotalMemory() {
        return Runtime.getRuntime().totalMemory() >> 20;
    }

    /**
     * @return The total amount of used memory in MB
     */
    public static long getUsedMemory() {
        final Runtime runtime = Runtime.getRuntime();
        return (runtime.totalMemory() - runtime.freeMemory()) >> 20;
    }

    /**
     * @return The maximum amount of memory in MB
     */
    public static long getMaxMemory() {
        return Runtime.getRuntime().maxMemory() >> 20;
    }

    /**
     * @return How many {@link Thread}'s are running on the current JVM instance
     */
    public static int getTotalThreads() {
        return ManagementFactory.getThreadMXBean().getThreadCount();
    }

    /**
     * @return The recent cpu usage for the JVM process as a percentage
     */
    public static double getProcessCpuLoad() {
        return (ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class).getProcessCpuLoad() * 100);
    }

    /**
     * @return The recent cpu usage for the whole system as a percentage
     */
    public static double getSystemCpuLoad() {
        return (ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class).getSystemCpuLoad() * 100);
    }

    /**
     * @return The total amount of used disk space in MB, or -1 if an I/O error occurred
     */
    public static long getDiskUsage() {
        try {
            final FileStore store = Files.getFileStore(Paths.get("/"));
            return ((store.getTotalSpace() - store.getUsableSpace()) / 1024) / 1024; //size returned is in bytes, we want MB
        } catch (IOException ignored) {
        }
        return -1;
    }

    /**
     * @return The total size of the current drive in MB, or -1 if an I/O error occurred
     */
    public static long getDiskSize() {
        try {
            return (Files.getFileStore(Paths.get("/")).getTotalSpace() / 1024) / 1024; //size returned is in bytes, we want MB
        } catch (IOException ignored) {
        }
        return -1;
    }

    /**
     * @return The amount of available processors
     */
    public static int getAvailableProcessors() {
        return Runtime.getRuntime().availableProcessors();
    }

    /**
     * @return The unix-epoch timestamp of the bot startup
     */
    public static long getUptime() {
        return Bot.getInstance().getStartTime();
    }

}
