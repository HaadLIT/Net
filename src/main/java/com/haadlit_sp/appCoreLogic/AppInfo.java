package com.haadlit_sp.appCoreLogic;

/**
 * Static product info shown on the Settings page: current version and the
 * human-readable change log. Bump {@link #VERSION} and prepend to
 * {@link #CHANGELOG} on each release.
 */
public final class AppInfo {

    public static final String NAME = "Net Usage Tracker";
    public static final String VERSION = "1.3.0";

    public static final String CHANGELOG = """
            v1.3.0
              - Dev changes | Git privacy guide

            v1.2.0
              - Fixed a major over-count: Data Used was up to ~7x too high.
                Windows readings now come from per-interface counters
                (netsh subinterfaces) instead of the netstat -e aggregate,
                which counted the same bytes once per filter/virtual adapter
                entry. Totals now match carrier/phone usage closely.

            v1.1.0
              - Added Settings page (always-on-top toggle, version, changelog).
              - Session view now shows a live-speed trend graph.
              - Removed the redundant "Check Now" button.

            v1.0.1
              - Fixed 4 GB counter wrap: Data Used no longer resets mid-session.
              - Sessions now save to a per-user folder so they persist reliably.

            v1.0.0
              - Initial release: live session tracking, four metric cards,
                session history with lookup.
            """;

    private AppInfo() {
    }
}
