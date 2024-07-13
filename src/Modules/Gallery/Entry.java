package Modules.Gallery;

import Modules.Utility;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class Entry {
    private final Path path;
    private final String name;
    private final String size;
    private final String modificationDate;

    public Entry(Path path) throws IOException {
        this.path = path;
        this.name = path.toFile().getName();
        this.size = Utility.formatInto(Files.size(path));
        this.modificationDate = Utility.formatDate(Files.getLastModifiedTime(path));
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
