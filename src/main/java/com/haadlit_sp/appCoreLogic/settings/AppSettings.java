package com.haadlit_sp.appCoreLogic.settings;

/**
 * User-adjustable settings, persisted between runs.
 *
 * @param alwaysOnTop keep the app window above other windows
 */
public record AppSettings(boolean alwaysOnTop) {

    public static AppSettings defaults() {
        return new AppSettings(false);
    }
}
