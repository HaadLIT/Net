package com.haadlit_sp.appCoreLogic.io;

import java.util.Locale;

/**
 * Picks the {@link NetworkReader} matching the host OS. Unknown platforms
 * fall back to the Linux {@code /proc/net/dev} reader.
 */
public final class ReaderFactory {

    private ReaderFactory() {
    }

    public static NetworkReader create() {
        String os = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
        if (os.contains("win")) {
            return new WindowsReader();
        }
        if (os.contains("mac") || os.contains("darwin")) {
            return new MacReader();
        }
        return new LinuxReader();
    }
}
