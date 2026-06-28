package com.haadlit_sp.appCoreLogic.util;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Minimal JSON helper for flat string/number objects — enough to persist a
 * session without pulling in a third-party library. It is deliberately not a
 * general parser: it only round-trips the flat objects this app writes.
 */
public final class Json {

    private static final Pattern PAIR = Pattern.compile(
            "\"([^\"]+)\"\\s*:\\s*(\"(?:[^\"\\\\]|\\\\.)*\"|-?\\d+(?:\\.\\d+)?|true|false|null)");

    private Json() {
    }

    /** Serializes a flat map; Number values are written bare, others quoted. */
    public static String toJson(Map<String, Object> map) {
        StringBuilder sb = new StringBuilder("{\n");
        int index = 0;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            sb.append("  \"").append(entry.getKey()).append("\": ");
            Object value = entry.getValue();
            if (value instanceof Number) {
                sb.append(value);
            } else {
                sb.append('"').append(escape(String.valueOf(value))).append('"');
            }
            if (++index < map.size()) {
                sb.append(',');
            }
            sb.append('\n');
        }
        return sb.append("}\n").toString();
    }

    /** Parses a flat JSON object into key -> raw string value. */
    public static Map<String, String> parse(String json) {
        Map<String, String> result = new LinkedHashMap<>();
        Matcher matcher = PAIR.matcher(json);
        while (matcher.find()) {
            String key = matcher.group(1);
            String raw = matcher.group(2);
            result.put(key, unwrap(raw));
        }
        return result;
    }

    private static String unwrap(String raw) {
        if (raw.length() >= 2 && raw.charAt(0) == '"') {
            String inner = raw.substring(1, raw.length() - 1);
            return inner.replace("\\\"", "\"").replace("\\\\", "\\");
        }
        return raw;
    }

    private static String escape(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
