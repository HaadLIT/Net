package com.haadlit_sp.appCoreLogic.io;

import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;

/**
 * Windows reader. Sums the per-interface IP byte counters reported by
 * {@code netsh interface ipv4|ipv6 show subinterfaces}, excluding loopback.
 *
 * <pre>
 *       MTU  MediaSenseState      Bytes In     Bytes Out  Interface
 *      1500                1     298338540      27487078  Wi-Fi
 * </pre>
 *
 * <p>{@code netstat -e} is deliberately NOT used. It reports a machine-wide
 * aggregate over every entry in the interface table, so adapters that register
 * filter/virtual entries (e.g. Killer NDIS drivers) have the same bytes counted
 * repeatedly — measured at ~7x the real adapter throughput on such hardware.
 * These per-subinterface counters track the adapter itself, and report IP-layer
 * octets, which is close to what a mobile carrier meters.
 */
public final class WindowsReader implements NetworkReader {

    private static final System.Logger LOG = System.getLogger(WindowsReader.class.getName());

    /** The loopback pseudo-interface reports this MTU in any locale. */
    private static final long LOOPBACK_MTU = 4_294_967_295L;

    @Override
    public ByteCount read() throws IOException {
        ByteCount v4 = sumFamily("ipv4");
        ByteCount v6 = sumFamily("ipv6");
        return new ByteCount(v4.received() + v6.received(), v4.sent() + v6.sent());
    }

    /**
     * Sums one address family. A family that is unavailable (e.g. IPv6 disabled)
     * contributes zero rather than failing the whole reading.
     */
    private ByteCount sumFamily(String family) {
        long received = 0;
        long sent = 0;
        try {
            for (String line : CommandRunner.run("netsh", "interface", family, "show", "subinterfaces")) {
                String[] parts = line.trim().split("\\s+");
                if (parts.length < 5) {
                    continue;
                }
                long mtu;
                long bytesIn;
                long bytesOut;
                try {
                    mtu = Long.parseLong(parts[0]);
                    bytesIn = Long.parseLong(parts[2]);
                    bytesOut = Long.parseLong(parts[3]);
                } catch (NumberFormatException e) {
                    continue; // header or separator row
                }
                if (mtu == LOOPBACK_MTU || interfaceName(parts).contains("loopback")) {
                    continue; // exclude loopback
                }
                received += bytesIn;
                sent += bytesOut;
            }
        } catch (IOException e) {
            LOG.log(System.Logger.Level.WARNING, "Could not read " + family + " subinterfaces", e);
        }
        return new ByteCount(received, sent);
    }

    /** The interface name is the trailing column and may contain spaces. */
    private static String interfaceName(String[] parts) {
        return String.join(" ", Arrays.copyOfRange(parts, 4, parts.length)).toLowerCase(Locale.ROOT);
    }
}
