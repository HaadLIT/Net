package com.haadlit_sp.appCoreLogic;

import com.haadlit_sp.appCoreLogic.io.ReaderFactory;
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

    private final SessionTracker tracker = new SessionTracker(ReaderFactory.create());
    private final SessionStore store = new SessionStore();

    private LocalDateTime sessionStart;

    /** Snapshots the OS counters and starts a session. */
    public void start() throws Exception {
        tracker.start();
        sessionStart = LocalDateTime.now().withNano(0);
    }

    /** Recomputes live metrics for the current instant. */
    public Metrics refresh() throws Exception {
        return tracker.tick();
    }

    public boolean isActive() {
        return tracker.isActive();
    }

    /**
     * Freezes the session, persists it to the Data folder and returns it.
     * A final reading is attempted so the saved total is up to date.
     */
    public Session stop() throws IOException {
        try {
            tracker.tick();
        } catch (Exception ignored) {
            // Keep the last known reading if the final probe fails.
        }
        Session session = new Session(
                store.nextNumber(),
                sessionStart != null ? sessionStart : LocalDateTime.now().withNano(0),
                LocalDateTime.now().withNano(0),
                tracker.lastDataUsed(),
                tracker.samples());
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
}
