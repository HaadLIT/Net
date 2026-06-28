package com.haadlit_sp.appCoreLogic.io;

import java.io.IOException;
import java.util.List;

/**
 * macOS reader. Parses {@code netstat -ib} and sums the per-interface
 * aggregate rows (the {@code <Link#...>} network row carries cumulative
 * Ibytes/Obytes) for every non-loopback interface.
 *
 * <p>{@code -ib} is used instead of a single {@code -I <ifname>} call so the
 * app does not need to know the interface name up front and still covers
 * every active interface.
 */
public final class MacReader implements NetworkReader {

    @Override
    public ByteCount read() throws IOException {
        long received = 0;
        long sent = 0;

        List<String> lines = CommandRunner.run("netstat", "-ib");
        for (String line : lines) {
            String[] cols = line.trim().split("\\s+");
            // Name Mtu Network Address Ipkts Ierrs Ibytes Opkts Oerrs Obytes
            if (cols.length < 10) {
                continue;
            }
            String name = cols[0];
            String network = cols[2];
            if (name.startsWith("lo") || !network.startsWith("<Link")) {
                continue; // skip loopback and the duplicate address-family rows
            }
            received += parseLong(cols[6]);
            sent += parseLong(cols[9]);
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
