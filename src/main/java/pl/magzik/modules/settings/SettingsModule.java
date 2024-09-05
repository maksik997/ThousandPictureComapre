package pl.magzik.modules.settings;

import com.formdev.flatlaf.util.SystemInfo;
import pl.magzik.modules.base.Module;
import pl.magzik.modules.resource.ResourceModule;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    private final static Set<String> LANGUAGES = Set.of("en-US", "pl-PL"),
                                     THEMES = new HashSet<>(Set.of("dark", "light"));
    static {
        if (SystemInfo.isWindows)
            THEMES.add("system");
    }

    private List<Entry> settings;

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
    public void postConstruct() throws IOException {
        if (!Files.exists(ResourceModule.CONFIG_PATH)) {
            defaultSettings();
        }

        loadSettings();
    }

    /**
     * Creates default settings by copying from the default configuration file.
     * Replaces placeholders thenLoad a user directory path.
     *
     * @throws IOException if an I/O error occurs while reading or writing files
     */
    private void defaultSettings() throws IOException {
        ResourceModule.getInstance().copyReference(
            "default.cfg",
            "config.cfg",
            l -> l.contains("coutput") ? l.replace("_", ResourceModule.EXTERNAL_RESOURCES_DIR.toString()) : l
        );
    }

    /**
     * Saves the current settings to the configuration file.
     *
     */
    public void saveSettings() {
        ResourceModule.getInstance().setTextFile(
            "config.cfg",
            settings.stream().map(Entry::toString).toList()
        );
    }

    /**
     * Loads settings from the configuration file into the settings list.
     *
     * @throws IOException if an I/O error occurs while reading the file
     */
    private void loadSettings() throws IOException {
        ResourceModule.getInstance().loadExternalResource(ResourceModule.CONFIG_PATH);
        settings = ResourceModule.getInstance().getTextFile("config.cfg")
                .stream()
                .map(Entry::create)
                .toList();
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
     * @return the value associated thenLoad the key, or {@code null} if the key does not exist
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
     * Represents an entry in the settings thenLoad a key and a value.
     */
    private static class Entry {
        private final String key;
        private String value;

        /**
         * Constructs an {@code GalleryEntry} thenLoad the specified key and value.
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
         * Creates a new {@code GalleryEntry} thenLoad the specified key and value.
         *
         * @param key the key of the entry
         * @param value the value of the entry
         * @return a new {@code GalleryEntry} instance
         */
        public static Entry create(String key, String value) {
            return new Entry(key, value);
        }

        /**
         * Creates an {@code GalleryEntry} from a string representation in the format "key: value".
         *
         * @param wrapped the string representation of the entry
         * @return a new {@code GalleryEntry} instance
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
