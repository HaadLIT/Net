package com.haadlit_sp.appCoreLogic.session;

import com.haadlit_sp.appCoreLogic.io.AppConnection;
import com.haadlit_sp.appCoreLogic.io.AppConnectionReader;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Tracks which apps are on the network during a session. Each tick returns the
 * current ranking (for the live panel) and updates a per-app peak connection
 * count, which forms the session-history summary.
 */
public final class AppUsageTracker {

    private final AppConnectionReader reader;
    private final Map<String, Integer> peakByApp = new LinkedHashMap<>();

    public AppUsageTracker(AppConnectionReader reader) {
        this.reader = reader;
    }

    public void reset() {
        peakByApp.clear();
    }

    /** Reads the current per-app connections and folds them into the peaks. */
    public List<AppConnection> tick() throws Exception {
        List<AppConnection> current = reader.read();
        for (AppConnection app : current) {
            peakByApp.merge(app.name(), app.connections(), Math::max);
        }
        return current;
    }

    /** Ranked peak connections per app over the whole session. */
    public List<AppConnection> summary() {
        List<AppConnection> apps = new ArrayList<>();
        peakByApp.forEach((name, peak) -> apps.add(new AppConnection(name, peak)));
        apps.sort(Comparator.comparingInt(AppConnection::connections).reversed());
        return apps;
    }
}
