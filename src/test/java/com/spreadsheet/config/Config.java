package com.spreadsheet.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.time.Duration;
import java.util.Properties;

/**
 * Reads settings from src/test/resources/config.properties.
 *
 * <p>Any setting can be overridden on the command line, for example:
 * {@code mvn test -Dbase.url=http://localhost:4173 -Dbrowser=firefox}
 */
public final class Config {

    private static final Properties properties = loadConfigFile();

    private Config() {}

    public static String getBaseUrl() {
        return get("base.url");
    }

    /** chrome, firefox or edge. */
    public static String getBrowser() {
        return get("browser");
    }

    public static boolean isHeadless() {
        return Boolean.parseBoolean(get("headless"));
    }

    /** How long explicit waits wait before failing. */
    public static Duration getTimeout() {
        return Duration.ofSeconds(Long.parseLong(get("timeout.seconds")));
    }

    private static String get(String key) {
        // A -Dkey=value command-line option wins over the value in config.properties.
        String value = System.getProperty(key, properties.getProperty(key));
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing setting '" + key + "' in config.properties");
        }
        return value.trim();
    }

    private static Properties loadConfigFile() {
        Properties props = new Properties();
        try (InputStream file = Config.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (file == null) {
                throw new IllegalStateException("config.properties not found in src/test/resources");
            }
            props.load(file);
        } catch (IOException e) {
            throw new UncheckedIOException("Could not read config.properties", e);
        }
        return props;
    }
}
