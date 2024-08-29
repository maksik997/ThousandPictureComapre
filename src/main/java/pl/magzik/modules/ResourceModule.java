package pl.magzik.modules;

import pl.magzik.modules.loader.Module;
import pl.magzik.modules.resource.loader.*;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.function.Function;

/**
 * Manages the loading, caching, and saving of resources such as images, configuration files, and text files.
 * This module follows the Singleton design pattern to ensure only one instance exists throughout the application.
 */
public class ResourceModule implements Module {

    /**
     * Holds the singleton instance of the {@code ResourceModule}.
     */
    private static final class InstanceHolder {
        private static final ResourceModule INSTANCE = new ResourceModule();
    }

    /**
     * Returns the singleton instance of {@code ResourceModule}.
     *
     * @return The singleton {@code ResourceModule} instance.
     */
    public static ResourceModule getInstance() {
        return InstanceHolder.INSTANCE;
    }

    /**
     * Internal loaders for different resource types.
     */
    private static final Map<String, Loader<?>> INTERNAL_LOADERS = Map.of(
        "cfg", new ReferenceLoader(),
        "png", new ImageLoader(),
        "jpg", new ImageLoader()
    );

    /**
     * External loaders for text files.
     */
    private static final Map<String, ExternalResourceLoader<List<String>>> EXTERNAL_LOADERS = Map.of(
        "cfg", new TextFileLoader(),
        "tp", new TextFileLoader()
    );

    /**
     * Directory path for external resources.
     */
    public static final Path EXTERNAL_RESOURCES_DIR = Path.of(System.getProperty("user.home"), ".ThousandPictureComapre");

    /**
     * Path to the configuration file.
     */
    public static final Path CONFIG_PATH = EXTERNAL_RESOURCES_DIR.resolve("config.cfg");

    // Unmodifiable
    private final Map<String, Map<String, ?>> resourceCache;
    private final Map<String, URI> referenceCache;
    private final Map<String, Image> imageCache;

    // Modifiable
    private final Map<String, List<String>> textFileCache;

    private boolean saveRequired;

    /**
     * Private constructor for initializing the singleton instance.
     * Loads resources and sets up a shutdown hook to save changes if necessary.
     */
    private ResourceModule() {
        this.resourceCache = new HashMap<>();
        this.referenceCache = new HashMap<>();
        this.imageCache = new HashMap<>();
        this.textFileCache = new HashMap<>();
        this.saveRequired = false;

        resourceCache.put("cfg", referenceCache);
        resourceCache.put("jpg", imageCache);
        resourceCache.put("png", imageCache);

        try {
            loadResources();
        } catch (IOException e) {
            throw new RuntimeException(e); // TODO FOR NOW
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                saveIfRequired();
            } catch (IOException e) {
                throw new RuntimeException(e); // TODO FOR NOW
            }
        }));
    }

    @Override
    public void load() throws IOException {
        loadExternalResources();
    }

    /**
     * Loads internal resources from the classpath.
     * Resources are loaded based on their file extensions using the appropriate loaders.
     *
     * @throws IOException If an I/O error occurs during loading or if resources are not found.
     */
    private void loadResources() throws IOException {
        URL resourcesURL = getClass().getClassLoader().getResource("");
        if (resourcesURL == null)
            throw new FileNotFoundException("Resources not found");

        Path resourcesPath;
        try {
            resourcesPath = Paths.get(resourcesURL.toURI());
        } catch (URISyntaxException e) {
            throw new IOException(e);
        }

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(resourcesPath)) {
            for (Path path : stream) {
                if (Files.isDirectory(path)) continue;

                loadResource(path);
            }
        }
    }

    /**
     * Loads a specific resource file using the appropriate loader based on its file extension.
     *
     * @param path The path of the resource file to be loaded.
     * @param <T>  The type of resource being loaded.
     * @throws IOException If an I/O error occurs during loading.
     */
    @SuppressWarnings("unchecked")
    private <T> void loadResource(Path path) throws IOException {
        String fileName = path.getFileName().toString();
        String extension = getExtension(fileName);

        Loader<T> loader = (Loader<T>) INTERNAL_LOADERS.get(extension);
        Map<String, T> cache = (Map<String, T>) this.resourceCache.get(extension);
        if (loader != null) {
            try {
                cache.put(
                    fileName,
                    loader.load(fileName)
                );
            } catch (URISyntaxException e) {
                throw new IOException(e);
            }
        }
    }

    /**
     * Loads external resources from the external resources directory.
     * This method specifically handles text files and updates the text file cache.
     *
     * @throws IOException If an I/O error occurs during loading.
     */
    private void loadExternalResources() throws IOException {
        Path resourcesPath = EXTERNAL_RESOURCES_DIR;

        if (!Files.exists(resourcesPath)) {
            Files.createDirectory(resourcesPath);
            return;
        }

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(resourcesPath)) {
            for (Path path : stream) {
                if (Files.isDirectory(path)) continue;
                if (textFileCache.containsKey(path.getFileName().toString())) continue;

                loadExternalResource(path);
            }
        }
    }

    /**
     * Loads an external resource (specifically a text file) and updates the text file cache.
     *
     * @param path The path of the external resource to be loaded.
     * @throws IOException If an I/O error occurs during loading.
     */
    public void loadExternalResource(Path path) throws IOException {
        String fileName = path.getFileName().toString();
        String extension = getExtension(fileName);

        Loader<List<String>> loader = EXTERNAL_LOADERS.get(extension);
        if (loader != null) {
            try {
                textFileCache.put(fileName, loader.load(path.toString()));
            } catch (URISyntaxException e) {
                throw new IOException(e);
            }
        }
    }

    /**
     * Saves external resources if any changes are marked as required.
     *
     * @throws IOException If an I/O error occurs during saving.
     */
    private void saveIfRequired() throws IOException {
        if (!saveRequired) return;

        for (String s : textFileCache.keySet()) {
            saveExternalResource(s);
        }
    }

    /**
     * Saves a specific external resource (text file) to the external resources' directory.
     *
     * @param fileName The name of the file to be saved.
     * @throws IOException If an I/O error occurs during saving.
     */
    private void saveExternalResource(String fileName) throws IOException {
        Path path = EXTERNAL_RESOURCES_DIR.resolve(fileName);
        List<String> data = textFileCache.get(fileName);
        String extension = getExtension(fileName);

        EXTERNAL_LOADERS.get(extension).save(data, path);
    }

    /**
     * Retrieves an image resource from the cache.
     *
     * @param name The name of the image resource.
     * @return The cached image resource, or {@code null} if not found.
     */
    public Image getImage(String name) {
        if (!imageCache.containsKey(name)) return null;
        return imageCache.get(name);
    }

    /**
     * Retrieves a reference resource from the cache.
     *
     * @param name The name of the reference resource.
     * @return The cached URI of the reference resource, or {@code null} if not found.
     */
    public URI getReference(String name) {
        if (!referenceCache.containsKey(name)) return null;
        return referenceCache.get(name);
    }

    /**
     * Retrieves a text file resource from the cache.
     *
     * @param name The name of the text file resource.
     * @return An unmodifiable list of strings representing the text file content, or {@code null} if not found.
     */
    public List<String> getTextFile(String name) {
        if (!textFileCache.containsKey(name)) return null;
        return Collections.unmodifiableList(textFileCache.get(name));
    }

    /**
     * Updates the content of a text file resource in the cache.
     * @param name The name of the text file resource.
     * @param list The new content to be set
     */
    public void setTextFile(String name, List<String> list) {
        try {
            setTextFile(name, list, false);
        } catch (IOException ignored) { } // won't happen
    }

    /**
     * Updates the content of a text file resource in the cache. Optionally, the updated content can be saved to disk.
     *
     * @param name The name of the text file resource.
     * @param list The new content to be set.
     * @param save {@code true} if the content should be saved to disk; {@code false} otherwise.
     * @throws IOException If an I/O error occurs during saving.
     */
    public void setTextFile(String name, List<String> list, boolean save) throws IOException {
        if (!textFileCache.containsKey(name)) return;
        textFileCache.put(name, list);

        if (save) saveExternalResource(name);
        this.saveRequired = !save;
    }

    /**
     * Adds a new text file resource to the cache and saves it to disk.
     *
     * @param name The name of the new text file resource.
     * @param list The content of the text file resource.
     * @throws IOException If an I/O error occurs during saving.
     */
    public void addTextFile(String name, List<String> list) throws IOException {
        textFileCache.put(name, list);
        saveExternalResource(name);
    }

    /**
     * Copies a reference resource to a new location, applying a transformation function to the data.
     *
     * @param refName The name of the reference resource to be copied.
     * @param target  The target path where the reference should be copied.
     * @param func    A function to transform the reference URI.
     * @throws IOException If an I/O error occurs during copying.
     */
    public void copyReference(String refName, String target, Function<String, String> func) throws IOException {
        if (!referenceCache.containsKey(refName)) return;

        Path path =  EXTERNAL_RESOURCES_DIR.resolve(target);
        ReferenceLoader.copy(referenceCache.get(refName), path, func);
    }

    /**
     * Extracts the file extension from a given file name.
     *
     * @param fileName The name of the file.
     * @return The file extension in lowercase, or an empty string if no extension is found.
     */
    private String getExtension(String fileName) {
        int dot = fileName.lastIndexOf('.');
        return dot == -1 ? "" : fileName.substring(dot + 1).toLowerCase();
    }
}
