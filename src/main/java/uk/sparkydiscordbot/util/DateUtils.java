package uk.sparkydiscordbot.util;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

    /**
     * Generates a timestamp based on the specified format.
     *
     * @param format The format to generate this timestamp in
     *
     * @return The formatted timestamp
     */
    public static String generateTimestamp(@NotNull(value = "format cannot be null") final String format) {
        return new SimpleDateFormat(format).format(new Date());
    }
}
