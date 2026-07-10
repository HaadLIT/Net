package com.haadlit_sp.appCoreLogic.session;

import java.time.LocalDateTime;
import java.util.List;

/**
 * A completed, persistable session record.
 *
 * @param number        sequential session id, also used in the file name
 * @param start         when the session was started
 * @param stop          when the session was stopped
 * @param dataUsedBytes total bytes used over the session
 * @param speedSamples  per-second live-speed (KB/s) history, for the trend graph
 */
public record Session(int number, LocalDateTime start, LocalDateTime stop, long dataUsedBytes,
                      List<Double> speedSamples) {
}
