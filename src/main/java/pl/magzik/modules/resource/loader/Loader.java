package pl.magzik.modules.resource.loader;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Interface for loading resources of type {@code T}.
 *
 * @param <T> The type of resource to be loaded.
 */
@FunctionalInterface
public interface Loader <T> {

    /**
     * Loads a resource thenLoad the given name.
     *
     * @param resource The name of the resource to loadFiles.
     * @return The loaded resource of type {@code T}.
     * @throws IOException If an I/O error occurs during loading.
     * @throws URISyntaxException If the resource name cannot be converted to a URI.
     */
    T load(String resource) throws IOException, URISyntaxException;
}
