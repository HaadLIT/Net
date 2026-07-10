package com.haadlit_sp.appCoreLogic;

import com.haadlit_sp.appCoreLogic.io.AppConnection;
import com.haadlit_sp.appCoreLogic.io.ReaderFactory;
import com.haadlit_sp.appCoreLogic.session.AppUsageTracker;
import com.haadlit_sp.appCoreLogic.session.LiveUpdate;
import com.haadlit_sp.appCoreLogic.session.Metrics;
import com.haadlit_sp.appCoreLogic.session.Session;
import com.haadlit_sp.appCoreLogic.session.SessionTracker;
import com.haadlit_sp.appCoreLogic.store.SessionStore;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Core facade the UI talks to. Hides the reader/tracker/store wiring behind a
 * small session-oriented API so the render layer never touches OS details.
 */
public final class Net {

    private static final System.Logger LOG = System.getLogger(Net.class.getName());

    private final SessionTracker tracker = new SessionTracker(ReaderFactory.create());
    private final AppUsageTracker appTracker = new AppUsageTracker(ReaderFactory.createAppConnectionReader());
    private final SessionStore store = new SessionStore();

    private LocalDateTime sessionStart;

    /** Snapshots the OS counters and starts a session. */
    public void start() throws Exception {
        tracker.start();
        appTracker.reset();
        sessionStart = LocalDateTime.now().withNano(0);
    }

    /** Recomputes live metrics and the current per-app connection ranking. */
    public LiveUpdate refresh() throws Exception {
        Metrics metrics = tracker.tick();
        return new LiveUpdate(metrics, readApps());
    }

    public boolean isActive() {
        return tracker.isActive();
    }

    /**
     * Freezes the session, persists it to the Data folder and returns it.
     * A final reading is attempted so the saved totals are up to date.
     */
    public Session stop() throws IOException {
        try {
            tracker.tick();
            readApps();
        } catch (Exception ignored) {
            // Keep the last known readings if the final probe fails.
        }
        Session session = new Session(
                store.nextNumber(),
                sessionStart != null ? sessionStart : LocalDateTime.now().withNano(0),
                LocalDateTime.now().withNano(0),
                tracker.lastDataUsed(),
                tracker.samples(),
                appTracker.summary());
        tracker.stop();
        store.save(session);
        return session;
    }

    public List<Session> history() {
        return store.list();
    }

    public Optional<Session> find(int number) {
        return store.find(number);
    }

    /** Per-app read failures must not break the metrics, so they degrade to empty. */
    private List<AppConnection> readApps() {
        try {
            return appTracker.tick();
        } catch (Exception e) {
            LOG.log(System.Logger.Level.WARNING, "Per-app connection read failed", e);
            return List.of();
        }
    }
}
