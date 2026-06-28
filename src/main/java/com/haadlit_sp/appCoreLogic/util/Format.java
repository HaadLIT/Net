package com.haadlit_sp.appCoreLogic.util;

import java.util.Locale;

/**
 * Human-readable formatting for the metric cards. Stateless and side-effect
 * free so it is safe to call from the EDT on every tick.
 */
public final class Format {

    private static final double UNIT = 1024.0;

    private Format() {
    }

    /** Formats a byte count as B / KB / MB / GB. */
    public static String bytes(long count) {
        if (count < UNIT) {
            return count + " B";
        }
        double kb = count / UNIT;
        if (kb < UNIT) {
            return decimal(kb) + " KB";
        }
        double mb = kb / UNIT;
        if (mb < UNIT) {
            return decimal(mb) + " MB";
        }
        return decimal(mb / UNIT) + " GB";
    }

    /** Formats a millisecond span as HH:MM:SS. */
    public static String duration(long millis) {
        long totalSeconds = Math.max(0, millis) / 1000;
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    private static String decimal(double value) {
        return String.format(Locale.ROOT, "%.2f", value);
    }
}
