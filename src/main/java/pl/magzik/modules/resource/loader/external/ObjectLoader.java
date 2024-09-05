package pl.magzik.modules.resource.loader.external;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Provides functionality to load and save objects using Java serialization.
 * This class implements the {@link ExternalResourceLoader} interface for handling
 * object serialization and deserialization from/to file resources.
 *
 * <p> The objects are loaded from a file using {@link ObjectInputStream} and
 * saved to a file using {@link ObjectOutputStream}. The file path is specified
 * as a {@link String} for loading and as a {@link Path} for saving.
 */
public class ObjectLoader implements ExternalResourceLoader<Object> {

    /**
     * Loads an object from the specified file resource.
     *
     * @param resource The name of the file resource from which the object is to be loaded.
     * @return The loaded object.
     * @throws IOException If an I/O error occurs during loading, or if the file is not found.
     */
    @Override
    public Object load(String resource) throws IOException {
        Path path = Path.of(resource);

        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(path))) {
            return ois.readObject();
        } catch (ClassNotFoundException e) {
            throw new IOException(e);
        }
    }

    /**
     * Saves an object to the specified file path.
     *
     * @param data The object to be saved.
     * @param path The path of the file where the object should be saved.
     * @throws IOException If an I/O error occurs during saving.
     */
    @Override
    public void save(Object data, Path path) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(path))) {
            oos.writeObject(data);
        }
    }
}
