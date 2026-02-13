# Time Tracker

A lightweight desktop application that monitors computer usage by tracking active windows, applications, and user input activity on Windows.

## What It Does

Time Tracker runs in the background and logs:
- **Active window titles** - Which window/document you're working in
- **Process names** - Which application is active
- **Keystroke counts** - Number of keys pressed per session
- **Mouse click counts** - Number of clicks per session
- **Time duration** - How long each window was active
- **Hostname** - Which computer the data came from

Data is automatically saved to daily CSV files (format: `yyyyMMdd.csv`) in the current working directory.

## How It Works

The application:
1. Polls the active window every second using Windows APIs (via JNA)
2. Listens for global keyboard and mouse events (via jnativehook)
3. Creates a new record whenever the active window changes
4. Detects inactivity after 3 seconds of no window changes
5. Writes timestamped records with input counts to daily CSV files

## Output Format

Each CSV file contains:
```
# Hostname: YOUR-COMPUTER-NAME
start,end,process,title,keys,clicks
1707840000000,1707840123000,notepad.exe,Document1.txt - Notepad,245,12
1707840123000,1707840456000,chrome.exe,Chrome - Google,182,45
```

**Note:** Fields containing commas, quotes, or newlines are automatically quoted following RFC 4180 standard.

Fields:
- **start/end**: Unix timestamps in milliseconds
- **process**: Executable name
- **title**: Window title text
- **keys**: Number of keystrokes during this session
- **clicks**: Number of mouse clicks during this session

## Building

Requirements:
- Java 17 or later
- Maven

Build the executable JAR:
```bash
mvn clean package
```

This creates `target/timetracker-1.0-SNAPSHOT-jar-with-dependencies.jar`

Run tests:
```bash
mvn test
```

## Running

```bash
java -jar target/timetracker-1.0-SNAPSHOT-jar-with-dependencies.jar
```

The application runs continuously until terminated (Ctrl+C).

On first run, a configuration file is created at `~/.timetracker/config.properties` with default settings. CSV files are created in the configured output directory (current directory by default).

## Configuration

The application can be customized by editing `~/.timetracker/config.properties`:

```properties
# Polling interval in milliseconds
polling.interval.ms=1000

# Inactivity timeout in milliseconds
inactivity.timeout.ms=3000

# Output directory for CSV files
output.directory=.
```

This file is automatically created with defaults on first run.

## Platform Support

Currently Windows only. Uses Windows-specific APIs for active window detection.

## Privacy Note

This application logs all active window titles and process names. Be mindful of sensitive information that may appear in window titles (passwords, personal data, etc.). Review CSV output before sharing.
