package com.haadlit_sp.appCoreLogic.io;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Windows per-app connection reader.
 *
 * <p>Windows exposes no non-admin, per-process network <em>byte</em> counter,
 * so this ranks apps by their number of active TCP connections instead:
 * <ul>
 *   <li>{@code netstat -ano} → established TCP connections with owning PIDs,</li>
 *   <li>{@code tasklist} → maps each PID to its image name.</li>
 * </ul>
 *
 * <p>The PID→name map is cached and only refreshed when an unknown PID appears,
 * so the common case spawns just one {@code netstat} per second.
 */
public final class WindowsAppConnectionReader implements AppConnectionReader {

    private final Map<String, String> pidToName = new java.util.HashMap<>();

    @Override
    public List<AppConnection> read() throws IOException {
        Map<String, Integer> byPid = countEstablishedByPid();
        ensureNames(byPid.keySet());

        Map<String, Integer> byApp = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : byPid.entrySet()) {
            String name = pidToName.getOrDefault(entry.getKey(), "PID " + entry.getKey());
            byApp.merge(name, entry.getValue(), Integer::sum);
        }

        List<AppConnection> apps = new ArrayList<>();
        byApp.forEach((name, count) -> apps.add(new AppConnection(name, count)));
        apps.sort((a, b) -> Integer.compare(b.connections(), a.connections()));
        return apps;
    }

    private Map<String, Integer> countEstablishedByPid() throws IOException {
        Map<String, Integer> byPid = new LinkedHashMap<>();
        for (String line : CommandRunner.run("netstat", "-ano")) {
            String trimmed = line.trim();
            if (!trimmed.startsWith("TCP")) {
                continue;
            }
            String[] parts = trimmed.split("\\s+");
            // TCP  <local>  <foreign>  <state>  <pid>
            if (parts.length < 5 || !parts[3].equalsIgnoreCase("ESTABLISHED")) {
                continue;
            }
            byPid.merge(parts[4], 1, Integer::sum);
        }
        return byPid;
    }

    private void ensureNames(Set<String> pids) throws IOException {
        boolean hasUnknown = pids.stream().anyMatch(pid -> !pidToName.containsKey(pid));
        if (!hasUnknown) {
            return;
        }
        pidToName.clear();
        for (String line : CommandRunner.run("tasklist", "/fo", "csv", "/nh")) {
            List<String> columns = parseCsvRow(line);
            if (columns.size() >= 2) {
                pidToName.put(columns.get(1).trim(), columns.get(0).trim());
            }
        }
    }

    /** Parses a tasklist CSV row: {@code "image.exe","1234",...}. */
    private static List<String> parseCsvRow(String line) {
        String trimmed = line.trim();
        if (trimmed.length() >= 2 && trimmed.startsWith("\"") && trimmed.endsWith("\"")) {
            trimmed = trimmed.substring(1, trimmed.length() - 1);
        }
        return List.of(trimmed.split("\",\""));
    }
}
