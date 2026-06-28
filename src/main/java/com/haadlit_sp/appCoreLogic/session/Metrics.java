package com.haadlit_sp.appCoreLogic.session;

/**
 * The four live values shown on the dashboard, computed once per tick.
 *
 * @param dataUsedBytes  bytes used since the session snapshot
 * @param durationMillis elapsed time since the session started
 * @param gbPerHour      projected GB/hour from the average so far
 * @param speedKBs       KB/s observed over the last tick
 */
public record Metrics(long dataUsedBytes, long durationMillis, double gbPerHour, double speedKBs) {
}
