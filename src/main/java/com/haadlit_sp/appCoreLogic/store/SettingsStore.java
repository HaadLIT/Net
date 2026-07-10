package com.haadlit_sp.appCoreLogic.store;

import com.haadlit_sp.appCoreLogic.settings.AppSettings;
import com.haadlit_sp.appCoreLogic.util.Json;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Persists {@link AppSettings} to {@code <user home>/NetTracker/settings.json}.
 * Fails soft: any read/write error falls back to defaults so a corrupt file
 * never stops the app from launching.
 */
public final class SettingsStore {

    private static final System.Logger LOG = System.getLogger(SettingsStore.class.getName());

    private final Path file;

    public SettingsStore() {
        this(Path.of(System.getProperty("user.home"), "NetTracker", "settings.json"));
    }

    public SettingsStore(Path file) {
        this.file = file;
    }

    public AppSettings load() {
        try {
            if (Files.exists(file)) {
                Map<String, String> map = Json.parse(Files.readString(file));
                return new AppSettings(Boolean.parseBoolean(map.getOrDefault("alwaysOnTop", "false")));
            }
        } catch (Exception e) {
            LOG.log(System.Logger.Level.WARNING, "Could not read settings; using defaults", e);
        }
        return AppSettings.defaults();
    }

    public void save(AppSettings settings) {
        try {
            Files.createDirectories(file.getParent());
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("alwaysOnTop", String.valueOf(settings.alwaysOnTop()));
            Files.writeString(file, Json.toJson(map));
        } catch (IOException e) {
            LOG.log(System.Logger.Level.WARNING, "Could not save settings", e);
        }
    }
}
