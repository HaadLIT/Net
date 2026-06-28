package com.haadlit_sp.appCoreLogic.session;

import java.time.LocalDateTime;

/**
 * A completed, persistable session record.
 *
 * @param number        sequential session id, also used in the file name
 * @param start         when the session was started
 * @param stop          when the session was stopped
 * @param dataUsedBytes total bytes used over the session
 */
public record Session(int number, LocalDateTime start, LocalDateTime stop, long dataUsedBytes) {
}
