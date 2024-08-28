package pl.magzik.modules;

import pl.magzik.modules.loader.Module;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

public class SettingsModule implements Module {
    private final Path configPath;

    private List<Entry> setts;

    public SettingsModule(String configPath) {
        this.configPath = Path.of(configPath);
    }

    @Override
    public void load() throws IOException {
        this.setts = new ArrayList<>();

        if (!this.configPath.toFile().exists()) {
            Files.createFile(this.configPath);

            defaultSettings();
            saveSettings();
        }
    }

    private void defaultSettings() {
        setts.add(Entry.create(
            "language", Locale.getDefault().getLanguage()+"-"+Locale.getDefault().getCountry()
        ));
        setts.add(Entry.create(
            "languages", String.join(",", "pl-PL", "en-US")
        ));
        setts.add(Entry.create(
            "theme", "dark"
        ));
        setts.add(Entry.create(
            "themes", String.join(",", "dark", "light", "system")
        ));
        setts.add(Entry.create(
            "destination-for-pc", System.getProperty("user.home")
        ));
        setts.add(Entry.create(
            "mode", "not-recursive"
        ));
        setts.add(Entry.create(
            "phash", "yes"
        ));
        setts.add(Entry.create(
            "pbp", "yes"
        ));
        setts.add(Entry.create(
            "unify-names-prefix", "tp_img_"
        ));
        setts.add(Entry.create(
            "unify-names-lowercase", "no"
        ));
    }

    public void saveSettings()  throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(configPath)) {
            for (Entry e : setts) {
                writer.write(e.toString());
                writer.newLine();
            }
        }
    }

    public void loadSettings() throws IOException {
        try (Stream<String> lines = Files.lines(configPath)) {
            setts = lines.map(Entry::create).toList();
        }
    }

    public void updateSetting(String key, String newValue) {
        setts.stream()
            .filter(s -> s.key().equals(key))
            .forEach(s -> s.set(newValue));
    }

    public String getSetting(String key) {
        return setts.stream()
                .filter(s -> s.key().equals(key))
                .map(Entry::get)
                .findFirst()
                .orElse(null);
    }

    static class Entry {
        private final String key;
        private String value;

        public Entry(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public String get() {
            return value;
        }

        public String key() {
            return key;
        }

        public void set(String value) {
            this.value = value;
        }

        public static Entry create(String key, String value) {
            return new Entry(key, value);
        }

        public static Entry create(String wrapped) {
            // String argument should be in the format "key:value",
            // otherwise an IllegalArgumentException will be thrown.
            // Element before ":" will be a key, and after will become a value.
            // Key isn't case-sensitive.

            if (!wrapped.matches("^.*->.*$"))
                throw new IllegalArgumentException("Bad format of the wrapped argument.");

            String[] args = wrapped.split("->");

            return create(args[0].toLowerCase(), args[1]);
        }

        @Override
        public String toString() {
            return String.format("%s->%s", key, value);
        }
    }
}
