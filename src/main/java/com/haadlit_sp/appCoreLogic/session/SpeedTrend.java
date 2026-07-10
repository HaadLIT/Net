package com.haadlit_sp.appCoreLogic.session;

import java.util.ArrayList;
import java.util.List;

/**
 * Accumulates per-second speed samples into a bounded set of points so the
 * trend graph shows the whole session at a useful resolution no matter how
 * long it runs.
 *
 * <p>Each stored point is the average KB/s over a "bucket" of ticks. Buckets
 * start at one tick each (full detail for short sessions). Once the buffer
 * reaches {@code capacity}, adjacent points are merged pairwise — halving the
 * count and doubling the ticks-per-bucket — so the stored size stays between
 * {@code capacity/2} and {@code capacity} for any duration. {@code capacity}
 * must be even.
 */
public final class SpeedTrend {

    private final int capacity;
    private final List<Double> buckets = new ArrayList<>();

    private int bucketTicks = 1; // ticks averaged into each stored point
    private double pendingSum;
    private int pendingCount;

    public SpeedTrend(int capacity) {
        this.capacity = capacity;
    }

    public void reset() {
        buckets.clear();
        bucketTicks = 1;
        pendingSum = 0;
        pendingCount = 0;
    }

    public void add(double speedKBs) {
        pendingSum += speedKBs;
        pendingCount++;
        if (pendingCount == bucketTicks) {
            buckets.add(pendingSum / bucketTicks);
            pendingSum = 0;
            pendingCount = 0;
            if (buckets.size() >= capacity) {
                halveResolution();
            }
        }
    }

    /** Merges adjacent points pairwise and doubles the bucket size. */
    private void halveResolution() {
        List<Double> merged = new ArrayList<>(buckets.size() / 2);
        for (int i = 0; i + 1 < buckets.size(); i += 2) {
            merged.add((buckets.get(i) + buckets.get(i + 1)) / 2.0);
        }
        buckets.clear();
        buckets.addAll(merged);
        bucketTicks *= 2;
    }

    /**
     * The stored points plus any partial trailing bucket, so the tail of a
     * session that ends mid-bucket is not lost. Does not mutate state.
     */
    public List<Double> snapshot() {
        List<Double> result = new ArrayList<>(buckets);
        if (pendingCount > 0) {
            result.add(pendingSum / pendingCount);
        }
        return result;
    }
}
