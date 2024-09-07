package pl.magzik.modules.gallery.table;

import pl.magzik.base.FormatUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.*;

/**
 * Represents an entry in a gallery, consisting of a file path and associated tags.
 * The entry stores the file's path, name, size, modification date, and a set of tags.
 * <p>
 * This class implements {@link Externalizable} to allow custom serialization and deserialization.
 */
public class GalleryEntry implements Externalizable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Path path;
    private Set<String> tags;

    private transient String name;
    private transient String size;
    private transient String modificationDate;

    /**
     * Default constructor for {@link Externalizable}.
     * This is required for deserialization to work properly.
     */
    public GalleryEntry() { }

    /**
     * Constructs a new {@code Entry} for the given file path.
     *
     * @param path the file path, which is assumed to exist.
     */
    public GalleryEntry(Path path) {
        // The Path is considered existing.
        this.path = path;
        this.name = path.toFile().getName();
        this.size = calculateSize(path);
        this.modificationDate = calculateModificationDate(path);
        this.tags = new LinkedHashSet<>();
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

    /**
     * Returns the set of tags associated with this entry.
     *
     * @return a {@link Set} of tags.
     */
    public Set<String> getTags() {
        return tags;
    }

    /**
     * Adds a tag to this entry.
     *
     * @param tag the tag to be added.
     */
    public void addTag(String tag) {
        tags.add(tag);
    }

    /**
     * Removes a tag from this entry.
     *
     * @param tag the tag to be removed.
     */
    public void removeTag(String tag) {
        tags.remove(tag);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GalleryEntry entry = (GalleryEntry) o;
        return Objects.equals(path, entry.path);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(path);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(path.toAbsolutePath().toString());
        out.writeObject(new ArrayList<>(tags));
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        path = Path.of((String) in.readObject());
        tags = new LinkedHashSet<>(validateEntryList(in.readObject()));
        name = path.getFileName().toString();
        size = calculateSize(path);
        modificationDate = calculateModificationDate(path);
    }

    /**
     * Calculates the size of the file at the given path in bytes.
     * If the size cannot be determined, returns 0.
     *
     * @param p the path of the file.
     * @return the size as a formatted string.
     */
    private String calculateSize(Path p) {
        long bytes;
        try {
            bytes = Files.size(p);
        } catch (IOException e) {
            bytes = 0;
        }

        return FormatUtils.formatInto(bytes);
    }

    /**
     * Calculates the last modification date of the file at the given path.
     * If the date cannot be determined, returns a default date.
     *
     * @param p the path of the file.
     * @return the modification date as a formatted string.
     */
    private String calculateModificationDate(Path p) {
        FileTime ft;
        try {
            ft = Files.getLastModifiedTime(p);
        } catch (IOException e) {
            ft = FileTime.fromMillis(0);
        }

        return FormatUtils.formatDate(ft);
    }

    /**
     * Validates that the given object is a list of strings.
     *
     * @param obj the object to validate.
     * @return the object cast to a {@code List<String>} if valid.
     * @throws ClassNotFoundException if the object is not a list of strings.
     */
    @SuppressWarnings("unchecked")
    private List<String> validateEntryList(Object obj) throws ClassNotFoundException {
        if (obj instanceof List<?> list && (list.isEmpty() || list.stream().allMatch(e -> e instanceof String))) {
            return (List<String>) obj;
        }

        throw new ClassNotFoundException("Expected a List<String> but found: " + obj.getClass().getName());
    }
}
