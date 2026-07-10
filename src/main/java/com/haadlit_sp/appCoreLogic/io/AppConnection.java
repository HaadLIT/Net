package com.haadlit_sp.appCoreLogic.io;

/**
 * A single application's active network-connection count at a point in time
 * (or its peak over a session). Used for the live per-app panel and the
 * session history summary.
 *
 * @param name        process image name, e.g. {@code chrome.exe}
 * @param connections number of active TCP connections attributed to it
 */
public record AppConnection(String name, int connections) {
}
