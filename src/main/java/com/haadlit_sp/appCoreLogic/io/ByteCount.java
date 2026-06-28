package com.haadlit_sp.appCoreLogic.io;

/**
 * Immutable snapshot of cumulative bytes seen on the OS network counters.
 * {@code received} + {@code sent} gives the total used for the session math.
 */
public record ByteCount(long received, long sent) {

    public long total() {
        return received + sent;
    }
}
