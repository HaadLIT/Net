package com.haadlit_sp.appCoreLogic.io;

import java.io.IOException;
import java.util.List;

/**
 * Windows reader. Parses {@code netstat -e}, whose "Bytes" row holds the
 * machine-wide received and sent totals (loopback is not counted there).
 *
 * <pre>
 *                            Received            Sent
 * Bytes                    1234567             7654321
 * </pre>
 */
public final class WindowsReader implements NetworkReader {

    @Override
    public ByteCount read() throws IOException {
        List<String> lines = CommandRunner.run("netstat", "-e");
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.toLowerCase().startsWith("bytes")) {
                String[] parts = trimmed.split("\\s+");
                if (parts.length >= 3) {
                    return new ByteCount(parseLong(parts[1]), parseLong(parts[2]));
                }
            }
        }
        throw new IOException("netstat -e: 'Bytes' row not found in output");
    }

    private static long parseLong(String value) {
        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException e) {
            return 0L;
        }
    }
}
