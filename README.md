# Net Usage Tracker

Net Usage Tracker is a lightweight Java desktop application for keeping an eye on network activity. It presents live usage metrics in a focused Swing interface, records completed monitoring sessions, and makes historical usage easy to review.

## Features

- Live network data usage and transfer speed
- Current session tracking with start and stop times
- Speed trend graph for the active session
- Session history with saved usage totals and speed samples
- Windows per-application connection information
- Settings page with an always-on-top preference
- Local JSON persistence with no external services or third-party runtime dependencies

## Requirements

- Java Development Kit (JDK) 17 or newer
- Windows, macOS, or Linux with a graphical desktop environment

The application selects an operating-system-specific network reader automatically. Per-application connection details are currently available on Windows; total network usage remains available on macOS and Linux.

## Run Locally

Clone the repository, open a terminal at its root, and start the application with the Gradle wrapper:

```bash
# Windows
./gradlew.bat run

# macOS / Linux
./gradlew run
```

On Windows PowerShell, use:

```powershell
.\gradlew.bat run
```

## Build a Runnable JAR

```bash
./gradlew clean jar
```

The generated JAR is placed in `build/libs/` and includes a `Main-Class` manifest entry, so it can be launched directly:

```bash
java -jar build/libs/NetTracker-1.0-SNAPSHOT.jar
```

## Test

Run the automated test suite with:

```bash
./gradlew test
```

## Data Storage

Completed sessions are stored as JSON files in:

```text
<user home>/NetTracker/Data/
```

This keeps session history available even when the application is launched from a different working directory. Runtime session data is intentionally excluded from version control.

## Project Structure

```text
src/main/java/com/haadlit_sp/
├── Main.java                    Application entry point
├── appCoreLogic/                Tracking, readers, sessions, settings, storage
└── appRenderLogic/              Swing window, pages, components, and theme
```

The core tracking logic is kept separate from the Swing presentation layer, making the platform readers and session storage easier to maintain independently from the user interface.

## Version

Current release: **1.2.0**

See the Settings page in the application for the built-in changelog.