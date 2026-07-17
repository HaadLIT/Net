package com.haadlit_sp.appCoreLogic.session;

import com.haadlit_sp.appCoreLogic.io.NetworkReader;

import java.util.List;

/**
 * Owns the live state of a single session: snapshots a baseline on start, then
 * derives {@link Metrics} on each tick relative to that baseline.
 *
 * <p>Fail-safe: OS byte counters can wrap, or reset when an interface
 * reconnects (e.g. a hotspot dropping). Rather than diffing against a fixed
 * baseline — which either event would push negative — usage is accumulated
 * from per-tick deltas, and any negative delta is treated as a skipped tick.
 * The running total therefore never collapses to zero mid-session.
 */
public final class SessionTracker {

    private static final double BYTES_PER_GB = 1024.0 * 1024.0 * 1024.0;

    /** Target point budget for the stored trend (must be even). */
    private static final int TREND_CAPACITY = 600;

    private final NetworkReader reader;
    private final SpeedTrend trend = new SpeedTrend(TREND_CAPACITY);

    private boolean active;
    private long accumulatedBytes;
    private long startMillis;
    private long lastTotal;
    private long lastTickMillis;
    private long lastDataUsed;

    public SessionTracker(NetworkReader reader) {
        this.reader = reader;
    }

    /** Snapshots the current counter and begins a session. */
    public void start() throws Exception {
        startMillis = System.currentTimeMillis();
        lastTotal = reader.read().total();
        lastTickMillis = startMillis;
        accumulatedBytes = 0;
        lastDataUsed = 0;
        trend.reset();
        active = true;
    }

    /** Reads the counter and computes the metrics for this instant. */
    public Metrics tick() throws Exception {
        if (!active) {
            throw new IllegalStateException("No active session");
        }
        long now = System.currentTimeMillis();
        long total = reader.read().total();

        // A negative delta means the counter wrapped or was reset; skip that
        // tick's jump rather than corrupting the running total.
        long deltaBytes = Math.max(0, total - lastTotal);
        accumulatedBytes += deltaBytes;

        long deltaMillis = Math.max(1, now - lastTickMillis);
        long durationMillis = now - startMillis;

        double speedKBs = (deltaBytes / 1024.0) / (deltaMillis / 1000.0);
        double hours = durationMillis / 3_600_000.0;
        double gbPerHour = hours > 0 ? (accumulatedBytes / BYTES_PER_GB) / hours : 0.0;

        trend.add(speedKBs);

        lastTotal = total;
        lastTickMillis = now;
        lastDataUsed = accumulatedBytes;

        return new Metrics(accumulatedBytes, durationMillis, gbPerHour, speedKBs);
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

    /** Downsampled live-speed (KB/s) history for this session, for the trend graph. */
    public List<Double> samples() {
        return trend.snapshot();
    }
}
