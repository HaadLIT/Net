package com.haadlit_sp.appCoreLogic.session;

import com.haadlit_sp.appCoreLogic.io.NetworkReader;

/**
 * Owns the live state of a single session: snapshots a baseline on start, then
 * derives {@link Metrics} on each tick relative to that baseline.
 *
 * <p>Fail-safe: OS byte counters can reset or wrap, so every delta is clamped
 * to {@code >= 0} — a wrap shows as a stalled tick rather than a negative or
 * absurdly large reading.
 */
public final class SessionTracker {

    private static final double BYTES_PER_GB = 1024.0 * 1024.0 * 1024.0;

    private final NetworkReader reader;

    private boolean active;
    private long baselineTotal;
    private long startMillis;
    private long lastTotal;
    private long lastTickMillis;
    private long lastDataUsed;

    public SessionTracker(NetworkReader reader) {
        this.reader = reader;
    }

    /** Snapshots the current counter and begins a session. */
    public void start() throws Exception {
        baselineTotal = reader.read().total();
        startMillis = System.currentTimeMillis();
        lastTotal = baselineTotal;
        lastTickMillis = startMillis;
        lastDataUsed = 0;
        active = true;
    }

    /** Reads the counter and computes the metrics for this instant. */
    public Metrics tick() throws Exception {
        if (!active) {
            throw new IllegalStateException("No active session");
        }
        long now = System.currentTimeMillis();
        long total = reader.read().total();

        long dataUsed = Math.max(0, total - baselineTotal);
        long deltaBytes = Math.max(0, total - lastTotal);
        long deltaMillis = Math.max(1, now - lastTickMillis);
        long durationMillis = now - startMillis;

        double speedKBs = (deltaBytes / 1024.0) / (deltaMillis / 1000.0);
        double hours = durationMillis / 3_600_000.0;
        double gbPerHour = hours > 0 ? (dataUsed / BYTES_PER_GB) / hours : 0.0;

        lastTotal = total;
        lastTickMillis = now;
        lastDataUsed = dataUsed;

        return new Metrics(dataUsed, durationMillis, gbPerHour, speedKBs);
    }

    public void stop() {
        active = false;
    }

    public boolean isActive() {
        return active;
    }

    public long startMillis() {
        return startMillis;
    }

    public long lastDataUsed() {
        return lastDataUsed;
    }
}
