package com.haadlit_sp.appCoreLogic.io;

/**
 * Reads the OS-level cumulative byte counters across active interfaces
 * (loopback excluded). One implementation per platform.
 */
public interface NetworkReader {

    ByteCount read() throws Exception;
}
