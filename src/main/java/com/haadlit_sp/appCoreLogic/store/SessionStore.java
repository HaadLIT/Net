package com.haadlit_sp.appCoreLogic.store;

import com.haadlit_sp.appCoreLogic.session.Session;
import com.haadlit_sp.appCoreLogic.util.Format;
import com.haadlit_sp.appCoreLogic.util.Json;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Persists completed sessions as {@code session-XXXX.json} files under the
 * {@code Data/} folder so past sessions can be listed and reopened.
 *
 * <p>Each method that touches disk fails soft: a single unreadable file is
 * skipped (and logged) rather than breaking the whole list.
 */
public final class SessionStore {

    private static final System.Logger LOG = System.getLogger(SessionStore.class.getName());
    private static final String PREFIX = "session-";
    private static final String SUFFIX = ".json";

    private final Path directory;

    public SessionStore() {
        this(Path.of("Data"));
    }

    public SessionStore(Path directory) {
        this.directory = directory;
    }

    /** Next sequential session number (max existing + 1). */
    public int nextNumber() {
        return list().stream().mapToInt(Session::number).max().orElse(0) + 1;
    }

    public void save(Session session) throws IOException {
        Files.createDirectories(directory);
        Path file = directory.resolve(fileName(session.number()));
        Files.writeString(file, Json.toJson(toMap(session)));
    }

    /** All stored sessions, sorted by number ascending. Never throws. */
    public List<Session> list() {
        List<Session> sessions = new ArrayList<>();
        if (!Files.isDirectory(directory)) {
            return sessions;
        }
        try (Stream<Path> files = Files.list(directory)) {
            files.filter(SessionStore::isSessionFile).forEach(file -> read(file).ifPresent(sessions::add));
        } catch (IOException e) {
            LOG.log(System.Logger.Level.WARNING, "Could not list session directory: " + directory, e);
        }
        sessions.sort(Comparator.comparingInt(Session::number));
        return sessions;
    }

    public Optional<Session> find(int number) {
        Path file = directory.resolve(fileName(number));
        return Files.exists(file) ? read(file) : Optional.empty();
    }

    private Optional<Session> read(Path file) {
        try {
            Map<String, String> map = Json.parse(Files.readString(file));
            return Optional.of(new Session(
                    Integer.parseInt(map.get("number")),
                    LocalDateTime.parse(map.get("start")),
                    parseNullable(map.get("stop")),
                    Long.parseLong(map.get("dataUsedBytes"))));
        } catch (Exception e) {
            LOG.log(System.Logger.Level.WARNING, "Skipping unreadable session file: " + file, e);
            return Optional.empty();
        }
    }

    private static LocalDateTime parseNullable(String value) {
        return (value == null || value.isEmpty()) ? null : LocalDateTime.parse(value);
    }

    private static Map<String, Object> toMap(Session session) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("number", session.number());
        map.put("date", session.start().toLocalDate().toString());
        map.put("start", session.start().toString());
        map.put("stop", session.stop() == null ? "" : session.stop().toString());
        map.put("dataUsedBytes", session.dataUsedBytes());
        map.put("dataUsed", Format.bytes(session.dataUsedBytes()));
        return map;
    }

    private static boolean isSessionFile(Path file) {
        String name = file.getFileName().toString();
        return name.startsWith(PREFIX) && name.endsWith(SUFFIX);
    }

    private static String fileName(int number) {
        return String.format("%s%04d%s", PREFIX, number, SUFFIX);
    }
}
