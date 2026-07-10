package com.haadlit_sp.appCoreLogic.io;

import java.util.List;

/**
 * Reads the set of applications with active network connections, ranked most
 * to least. Platforms with no supported source return an empty list.
 */
public interface AppConnectionReader {

    List<AppConnection> read() throws Exception;
}
