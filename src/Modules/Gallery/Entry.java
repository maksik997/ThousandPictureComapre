package Modules.Gallery;

import Modules.Utility;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Entry {
    private final Path path;
    private final String name;
    private final String size;
    private final String modificationDate;
    private final Set<String> tags;

    public Entry(Path path) throws IOException {
        this.path = path;
        this.name = path.toFile().getName();
        this.size = Utility.formatInto(Files.size(path));
        this.modificationDate = Utility.formatDate(Files.getLastModifiedTime(path));
        this.tags = new LinkedHashSet<>();
    }

    public Entry(Path path, String... tags) throws IOException {
        this(path);
        this.tags.addAll(Arrays.asList(tags));
    }

    public Path getPath() {
        return path;
    }

    public String getName() {
        return name;
    }

    public String getSize() {
        return size;
    }

    public String getModificationDate() {
        return modificationDate;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void addTag(String tag) {
        tags.add(tag);
    }

    public void removeTag(String tag) {
        tags.remove(tag);
    }

    public String serialize() {
        if (tags.isEmpty()) return path.toAbsolutePath().toString();
        return String.format("%s : %s", path.toAbsolutePath(), String.join(",", tags));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Entry entry = (Entry) o;
        return Objects.equals(path, entry.path);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(path);
    }
}
