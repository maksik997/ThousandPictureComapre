package pl.magzik.modules;

import com.formdev.flatlaf.util.SystemInfo;
import pl.magzik.modules.loader.Module;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

/**
 * The {@code SettingsModule} class manages application settings.
 * It handles loading, saving, and updating settings, as well as providing
 * default settings if none are present.
 * <p>
 * This class supports configuration management for application settings,
 * including supported languages and themes. It reads configuration from
 * a default file if no user configuration is found, and allows for
 * dynamic updates of settings.
 * </p>
 */
public class SettingsModule implements Module {
    private final static Path USER_SETTINGS_DIR = Path.of(System.getProperty("user.home"), ".ThousandPictureComapre");
    private final static String DEFAULT_CONFIG_FILE = "default.cfg";

    private final static Set<String> LANGUAGES = Set.of("en-US", "pl-PL"),
                                     THEMES = new HashSet<>(Set.of("dark", "light"));
    static {
        if (SystemInfo.isWindows)
            THEMES.add("system");
    }

    private List<Entry> settings;
    private Path configPath;

    /**
     * Constructs a new {@code SettingsModule} instance.
     * Initializes the settings list.
     */
    public SettingsModule() {
        this.settings = new ArrayList<>();
    }

    /**
     * Loads settings from the configuration file.
     * If the configuration file does not exist, it creates default settings.
     *
     * @throws IOException if an I/O error occurs while reading or writing files
     */
    @Override
    public void load() throws IOException {
        configPath = USER_SETTINGS_DIR.resolve("config.cfg");

        if (!Files.exists(configPath)) {
            try {
                defaultSettings();
            } catch (URISyntaxException e) {
                throw new IOException(e);
            }
        }

        loadSettings();
    }

    /**
     * Creates default settings by copying from the default configuration file.
     * Replaces placeholders with user directory path.
     *
     * @throws URISyntaxException if the default configuration file URI is invalid
     * @throws IOException if an I/O error occurs while reading or writing files
     */
    private void defaultSettings() throws URISyntaxException, IOException {
        if (Files.exists(USER_SETTINGS_DIR) || USER_SETTINGS_DIR.toFile().mkdirs()) {
            URL defaultConfigURL = SettingsModule.class.getClassLoader().getResource(DEFAULT_CONFIG_FILE);
            if (defaultConfigURL == null)
                throw new IOException("Default configuration file not found");

            URI defaultConfigUri = defaultConfigURL.toURI();

            List<String> lines = Files.readAllLines(Path.of(defaultConfigUri)).stream()
                .map(l -> l.contains("coutput") ? l.replace("_", USER_SETTINGS_DIR.toString()) : l)
                .toList();

            Files.write(configPath, lines);
        }
    }

    /**
     * Saves the current settings to the configuration file.
     *
     * @throws IOException if an I/O error occurs while writing the file
     */
    public void saveSettings()  throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(configPath)) {
            for (Entry e : settings) {
                writer.write(e.toString());
                writer.newLine();
            }
        }
    }

    /**
     * Loads settings from the configuration file into the settings list.
     *
     * @throws IOException if an I/O error occurs while reading the file
     */
    private void loadSettings() throws IOException {
        try (Stream<String> lines = Files.lines(configPath)) {
            settings = lines.map(Entry::create).toList();
        }
    }

    /**
     * Updates the value of a setting identified by the given key.
     *
     * @param key the setting key
     * @param newValue the new value to set
     */
    public void updateSetting(String key, String newValue) {
        settings.stream()
            .filter(s -> s.key().equals(key))
            .forEach(s -> s.set(newValue));
    }

    /**
     * Retrieves the value of a setting identified by the given key.
     *
     * @param key the setting key
     * @return the value associated with the key, or {@code null} if the key does not exist
     */
    public String getSetting(String key) {
        return settings.stream()
                .filter(s -> s.key().equals(key))
                .map(Entry::get)
                .findFirst()
                .orElse(null);
    }

    /**
     * Retrieves a comma-separated list of supported languages or themes.
     *
     * @param key the key for retrieving either "languages" or "themes"
     * @return a comma-separated list of supported languages or themes
     * @throws IllegalArgumentException if the key is not recognized
     */
    public String getSet(String key) {
        if (key.equals("languages")) return String.join(",", LANGUAGES);
        else if (key.equals("themes")) return String.join(",", THEMES);
        throw new IllegalArgumentException("Unknown key: " + key);
    }

    /**
     * Represents an entry in the settings with a key and a value.
     */
    private static class Entry {
        private final String key;
        private String value;

        /**
         * Constructs an {@code Entry} with the specified key and value.
         *
         * @param key the key of the entry
         * @param value the value of the entry
         */
        public Entry(String key, String value) {
            this.key = key;
            this.value = value;
        }

        /**
         * Returns the value of this entry.
         *
         * @return the value of this entry
         */
        public String get() {
            return value;
        }

        /**
         * Returns the key of this entry.
         *
         * @return the key of this entry
         */
        public String key() {
            return key;
        }

        /**
         * Sets the value of this entry.
         *
         * @param value the new value to set
         */
        public void set(String value) {
            this.value = value;
        }

        /**
         * Creates a new {@code Entry} with the specified key and value.
         *
         * @param key the key of the entry
         * @param value the value of the entry
         * @return a new {@code Entry} instance
         */
        public static Entry create(String key, String value) {
            return new Entry(key, value);
        }

        /**
         * Creates an {@code Entry} from a string representation in the format "key: value".
         *
         * @param wrapped the string representation of the entry
         * @return a new {@code Entry} instance
         * @throws IllegalArgumentException if the string is not in the correct format
         */
        public static Entry create(String wrapped) {
            if (!wrapped.matches("^.*:.*$"))
                throw new IllegalArgumentException("Invalid format of the wrapped argument.");

            String[] args = wrapped.split(":", 2);

            return create(args[0].toLowerCase(), args[1]);
        }

        @Override
        public String toString() {
            return String.format("%s:%s", key, value);
        }
    }
}
