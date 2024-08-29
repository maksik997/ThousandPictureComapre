package pl.magzik.modules.resource.loader;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Interface for loading and saving external resources of type {@code T}.
 * Provides default methods for handling file paths.
 *
 * @param <T> The type of external resource to be handled.
 */
public interface ExternalResourceLoader <T> extends Loader<T> {

    /**
     * Saves the data to a specified path.
     *
     * @param data The data to be saved.
     * @param path The path where the data should be saved.
     * @throws IOException If an I/O error occurs during saving.
     */
    void save(T data, Path path) throws IOException;

    /**
     * Converts a string representation of a path to an absolute {@link Path} object.
     *
     * @param s The string representation of the path.
     * @return The absolute {@link Path} object.
     */
    default Path getPath(String s) {
        Path path = Paths.get(s);
        return path.toAbsolutePath();
    }
}
