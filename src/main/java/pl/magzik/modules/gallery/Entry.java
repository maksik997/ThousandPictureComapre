package pl.magzik.modules.gallery;

import pl.magzik.Comparator.FilePredicate;
import pl.magzik.modules.Utility;

import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Entry implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final String path;
    private final String name;
    private final String size;
    private final String modificationDate;
    private final Set<String> tags;

    public Entry(Path path) {

        // Path must exist, and be checked.

        this.path = path.toAbsolutePath().toString();
        this.name = path.toFile().getName();
        String size;
        try {
            size = Utility.formatInto(Files.size(path));
        } catch (IOException e) {
            size = "0.00 KB";
        }
        this.size = size;
        String modificationDate;
        try {
            modificationDate = Utility.formatDate(Files.getLastModifiedTime(path));
        } catch (IOException e) {
            modificationDate = "1970/01/01 00:00:00";
        }
        this.modificationDate = modificationDate;
        this.tags = new LinkedHashSet<>();
    }

    public Entry(Path path, String... tags) throws IOException {
        this(path);
        this.tags.addAll(Arrays.asList(tags));
    }

    public Path getPath() {
        return Path.of(path);
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

    /*public String serialize() {
        if (tags.isEmpty()) return path.toAbsolutePath().toString();
        return String.format("%s : %s", path.toAbsolutePath(), String.join(",", tags));
    }*/

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

    public static Entry create(String line) {
        // Create a path
        String[] split = line.split(" : ", 2);
        Path p = Path.of(split[0]);
        String[] tags;
        if (split.length == 2) tags = split[1].split(",");
        else tags = new String[0];

        // Create entry
        try {
            if (tags.length == 0) return new Entry(p);
            else return new Entry(p, tags);
        } catch (IOException e) {
            throw new UncheckedIOException(new IOException(
                    "Could not find image: " + p + ".\nIf you decide to continue, gallery will load without pictures.\nAny change to gallery (adding pictures) will overwrite current picture references.",
                    e
            ));
        }
    }

    public static boolean testEntry(FilePredicate fp, Entry entry) {
        try {
            return fp.test(entry.getPath().toFile());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
