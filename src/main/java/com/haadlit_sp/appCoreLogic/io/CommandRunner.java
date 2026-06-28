package com.haadlit_sp.appCoreLogic.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Spawns an OS command and returns its output as lines.
 *
 * <p>Fail-safes: streams are always closed (try-with-resources), the child
 * process is force-killed on timeout or interruption so we never leak a
 * lingering process, and a hard 5s cap keeps the 1-second UI tick responsive.
 */
public final class CommandRunner {

    private static final int TIMEOUT_SECONDS = 5;

    private CommandRunner() {
    }

    public static List<String> run(String... command) throws IOException {
        ProcessBuilder builder = new ProcessBuilder(command);
        builder.redirectErrorStream(true);

        Process process = builder.start();
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader =
                     new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } finally {
            drain(process, command);
        }
        return lines;
    }

    private static void drain(Process process, String[] command) throws IOException {
        try {
            if (!process.waitFor(TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                process.destroyForcibly();
                throw new IOException("Command timed out: " + String.join(" ", command));
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            process.destroyForcibly();
            throw new IOException("Command interrupted: " + String.join(" ", command), e);
        }
    }
}
