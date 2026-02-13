package nl.ghyze;

import java.io.File;
import java.io.FileWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import nl.ghyze.inputcounter.InputCounter;
import nl.ghyze.timetracker.ActiveWindow;
import nl.ghyze.timetracker.ConfigurationService;
import nl.ghyze.timetracker.ProgramTimeRecord;
import nl.ghyze.timetracker.windows.ActiveWindowWin32;

import org.joda.time.DateTime;

public class TimeTracker {

    private final ActiveWindow activeWindow;
    private String lastTitle = "none";
    private String lastProcess = "none";
    private long lastChange = System.currentTimeMillis();
    private final List<ProgramTimeRecord> records;
    private File file;
    private FileWriter writer;

    private long lastCheck = 0l;

    private final InputCounter counter;
    private final ConfigurationService config;
    private final ScheduledExecutorService scheduler;

    private String hostname = null;

    public TimeTracker() {
        config = new ConfigurationService();
        counter = new InputCounter();
        activeWindow = new ActiveWindowWin32();
        records = new ArrayList<ProgramTimeRecord>();
        scheduler = Executors.newSingleThreadScheduledExecutor();

        // Register shutdown hook to close resources properly
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
    }

    public void start() {
        long intervalMs = config.getPollingIntervalMs();
        scheduler.scheduleAtFixedRate(
                this::check,
                0,
                intervalMs,
                TimeUnit.MILLISECONDS
        );
        System.out.println("Time tracker started with polling interval: " + intervalMs + "ms");
    }

    private void check() {
        long current = System.currentTimeMillis();
        if (!lastTitle.equals(activeWindow.getActiveWindowTitle())) {
            writeRecord();
        } else if (lastCheck > 0 && lastCheck < current - config.getInactivityTimeoutMs()) {
            lastProcess = "";
            lastTitle = "";
            writeRecord();
        }
        lastCheck = current;
    }

    private void writeRecord() {
        long change = System.currentTimeMillis();

        ProgramTimeRecord record = new ProgramTimeRecord(new DateTime(
                lastChange), new DateTime(change), lastTitle, lastProcess, counter.getKeys(), counter.getClicks());
        records.add(record);
        writeToFile(record);

        lastChange = change;
        System.out.println(record);
        lastTitle = activeWindow.getActiveWindowTitle();
        lastProcess = activeWindow.getActiveWindowProcessName();
    }

    private void writeToFile(ProgramTimeRecord record) {
        String fileName = record.getStart().toString("yyyyMMdd") + ".csv";
        if (file == null || !file.getName().equals(fileName)) {
            File outputDir = new File(config.getOutputDirectory());
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }
            file = new File(outputDir, fileName);
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                writer = null;
            }
        }

        if (writer == null) {
            try {
                writer = new FileWriter(file, true);
                if (file.length() == 0) {
                    writer.append("# Hostname: ").append(getHostname()).append("\r\n");
                    writer.append("start,end,process,title,keys,clicks\r\n");
                    writer.flush();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        if (writer != null) {
            try {
                writer.append(record.toFileString() + "\r\n");
                writer.flush();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private String getHostname() {
        if (hostname == null) {
            try {
                InetAddress addr;
                addr = InetAddress.getLocalHost();
                hostname = addr.getHostName();
            } catch (UnknownHostException ex) {
                System.out.println("Hostname can not be resolved");
            }
        }
        return hostname;
    }

    private void shutdown() {
        System.out.println("Shutting down time tracker...");

        // Shutdown scheduler
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException ex) {
            scheduler.shutdownNow();
        }

        // Close writer
        if (writer != null) {
            try {
                writer.flush();
                writer.close();
                System.out.println("Writer closed successfully");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        TimeTracker tracker = new TimeTracker();
        tracker.start();

        // Keep application running
        try {
            Thread.currentThread().join();
        } catch (InterruptedException ex) {
            System.out.println("Application interrupted");
        }
    }
}
