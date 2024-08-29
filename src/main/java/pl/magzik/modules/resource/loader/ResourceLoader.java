package pl.magzik.modules.resource.loader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

/**
 * Interface for loading resources of type {@code T}.
 * Provides a default method to get a resource URL.
 *
 * @param <T> The type of resource to be loaded.
 */
public interface ResourceLoader <T> extends Loader<T> {

    /**
     * Retrieves the URL of the resource with the specified file name.
     *
     * @param fileName The name of the resource file.
     * @return The URL of the resource.
     * @throws IOException If the resource cannot be found.
     */
    default URL getResource(String fileName) throws IOException {
        URL resource = getClass().getClassLoader().getResource(fileName);
        if (resource == null)
            throw new FileNotFoundException(fileName);
        return resource;
    }
}
