package nl.ghyze.timetracker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigurationService {

    private static final String CONFIG_DIR = ".timetracker";
    private static final String CONFIG_FILE = "config.properties";

    // Default values (current hardcoded preferences)
    private static final int DEFAULT_POLLING_INTERVAL_MS = 1000;
    private static final int DEFAULT_INACTIVITY_TIMEOUT_MS = 3000;
    private static final String DEFAULT_OUTPUT_DIRECTORY = ".";

    private final Properties properties;
    private final File configFile;

    public ConfigurationService() {
        this.properties = new Properties();
        this.configFile = getConfigFile();
        loadConfiguration();
    }

    private File getConfigFile() {
        String userHome = System.getProperty("user.home");
        File configDir = new File(userHome, CONFIG_DIR);
        return new File(configDir, CONFIG_FILE);
    }

    private void loadConfiguration() {
        if (configFile.exists()) {
            try (FileInputStream fis = new FileInputStream(configFile)) {
                properties.load(fis);
                System.out.println("Configuration loaded from: " + configFile.getAbsolutePath());
            } catch (IOException ex) {
                System.err.println("Failed to load configuration, using defaults");
                ex.printStackTrace();
            }
        } else {
            System.out.println("Configuration file not found, using defaults");
            createDefaultConfiguration();
        }
    }

    private void createDefaultConfiguration() {
        // Set default values
        properties.setProperty("polling.interval.ms", String.valueOf(DEFAULT_POLLING_INTERVAL_MS));
        properties.setProperty("inactivity.timeout.ms", String.valueOf(DEFAULT_INACTIVITY_TIMEOUT_MS));
        properties.setProperty("output.directory", DEFAULT_OUTPUT_DIRECTORY);

        // Save to file
        File configDir = configFile.getParentFile();
        if (!configDir.exists()) {
            configDir.mkdirs();
        }

        try (FileOutputStream fos = new FileOutputStream(configFile)) {
            properties.store(fos, "Time Tracker Configuration");
            System.out.println("Default configuration created at: " + configFile.getAbsolutePath());
        } catch (IOException ex) {
            System.err.println("Failed to create default configuration file");
            ex.printStackTrace();
        }
    }

    public int getPollingIntervalMs() {
        String value = properties.getProperty("polling.interval.ms", String.valueOf(DEFAULT_POLLING_INTERVAL_MS));
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            System.err.println("Invalid polling.interval.ms value, using default");
            return DEFAULT_POLLING_INTERVAL_MS;
        }
    }

    public int getInactivityTimeoutMs() {
        String value = properties.getProperty("inactivity.timeout.ms", String.valueOf(DEFAULT_INACTIVITY_TIMEOUT_MS));
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            System.err.println("Invalid inactivity.timeout.ms value, using default");
            return DEFAULT_INACTIVITY_TIMEOUT_MS;
        }
    }

    public String getOutputDirectory() {
        return properties.getProperty("output.directory", DEFAULT_OUTPUT_DIRECTORY);
    }
}
