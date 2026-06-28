package com.haadlit_sp.appCoreLogic.io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Linux reader. Sums rx + tx bytes per non-loopback interface from
 * {@code /proc/net/dev}. Each data row looks like:
 *
 * <pre>
 * eth0: 12345 67 0 0 0 0 0 0 8910 11 0 0 0 0 0 0
 *       rx_bytes                  tx_bytes
 * </pre>
 */
public final class LinuxReader implements NetworkReader {

    private static final Path PROC_NET_DEV = Path.of("/proc/net/dev");

    @Override
    public ByteCount read() throws IOException {
        long received = 0;
        long sent = 0;

        List<String> lines = Files.readAllLines(PROC_NET_DEV);
        for (String line : lines) {
            int colon = line.indexOf(':');
            if (colon < 0) {
                continue; // header rows have no ':'
            }
            String iface = line.substring(0, colon).trim();
            if (iface.equals("lo")) {
                continue; // exclude loopback
            }
            String[] fields = line.substring(colon + 1).trim().split("\\s+");
            if (fields.length >= 9) {
                received += parseLong(fields[0]); // column 1 = rx bytes
                sent += parseLong(fields[8]);     // column 9 = tx bytes
            }
        }
        return new ByteCount(received, sent);
    }

    private static long parseLong(String value) {
        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException e) {
            return 0L;
        }
    }
}
