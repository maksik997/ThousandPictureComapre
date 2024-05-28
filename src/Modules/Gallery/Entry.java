package Modules.Gallery;

import Modules.Utility;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

public class Entry {
    private final Path path;
    private final String name;
    private final String size;
    private final String modificationDate;
    private final Set<String> tags;

    public Entry(Path path, Set<String> tags) throws IOException {
        this.path = path;
        this.name = path.toFile().getName();
        this.size = Utility.formatInto(Files.size(path));
        this.modificationDate = Utility.formatDate(Files.getLastModifiedTime(path));
        this.tags = tags;
    }

    public Path getPath() {
        return path;
    }

    public String getName() {
        return name;
    }

    public Set<String> getTags() {
        return tags;
    }

    public String getSize() {
        return size;
    }

    public String getModificationDate() {
        return modificationDate;
    }

    public void addTag(String tag) {
        tags.add(tag);
    }

    public void removeTag(String tag) {
        tags.remove(tag);
    }
}
