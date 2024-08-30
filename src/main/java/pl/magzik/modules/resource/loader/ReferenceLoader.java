package pl.magzik.modules.resource.loader;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * Loads URI references from resources and provides utility methods to copy these references.
 * Implements {@link ResourceLoader} to handle URI resources.
 */
public class ReferenceLoader implements ResourceLoader<URI> {

    /**
     * Loads a URI resource from the given file name.
     *
     * @param resource The name of the resource to loadFiles.
     * @return The loaded {@link URI}.
     * @throws IOException If an I/O error occurs during loading.
     * @throws URISyntaxException If the resource string cannot be converted to a URI.
     */
    @Override
    public URI load(String resource) throws IOException, URISyntaxException {
        Objects.requireNonNull(resource);

        return getResource(resource).toURI();
    }

    /**
     * Copies the content from a reference URI to a target path, applying a transformation function to each line.
     *
     * @param ref  The source URI to copy from.
     * @param target The target path where the content should be copied.
     * @param func A function to transform each line of the content.
     * @throws IOException If an I/O error occurs during copying.
     */
    public static void copy(URI ref, Path target, Function<String, String> func) throws IOException {
        List<String> lines = Files.readAllLines(Path.of(ref)).stream().map(func).toList();
        Files.write(target, lines);
    }
}
