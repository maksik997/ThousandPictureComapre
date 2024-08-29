package pl.magzik.modules.resource.loader;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Loads and saves text files.
 * Implements {@link ExternalResourceLoader} to handle text file resources.
 */
public class TextFileLoader implements ExternalResourceLoader<List<String>> {

    /**
     * Loads a text file resource into a list of strings.
     *
     * @param resource The name of the text file resource to load.
     * @return A {@link List} of strings representing the content of the text file.
     * @throws IOException If an I/O error occurs during loading.
     */
    @Override
    public List<String> load(String resource) throws IOException {
        return Files.readAllLines(getPath(resource));
    }

    /**
     * Saves a list of strings to a text file at the specified path.
     *
     * @param data The data to be written to the file.
     * @param path The path of the file where data should be saved.
     * @throws IOException If an I/O error occurs during saving.
     */
    @Override
    public void save(List<String> data, Path path) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            for (String line : data) {
                writer.write(line);
                writer.newLine();
            }
        }
    }
}
