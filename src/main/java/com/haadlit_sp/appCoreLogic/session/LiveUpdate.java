package com.haadlit_sp.appCoreLogic.session;

import com.haadlit_sp.appCoreLogic.io.AppConnection;

import java.util.List;

/**
 * One tick's worth of live data for the dashboard: the metric values plus the
 * current ranked list of apps with active connections.
 */
public record LiveUpdate(Metrics metrics, List<AppConnection> apps) {
}
