package pl.magzik.modules.resource;

import pl.magzik.modules.base.Module;
import pl.magzik.modules.resource.loader.external.ExternalResourceLoader;
import pl.magzik.modules.resource.loader.external.ObjectLoader;
import pl.magzik.modules.resource.loader.external.TextFileLoader;
import pl.magzik.modules.resource.loader.internal.ImageLoader;
import pl.magzik.modules.resource.loader.Loader;
import pl.magzik.modules.resource.loader.internal.ReferenceLoader;

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
    private static final Map<String, ExternalResourceLoader<?>> EXTERNAL_LOADERS = Map.of(
        "cfg", new TextFileLoader(),
        "tp", new ObjectLoader()
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
    private final Map<String, Map<String, ?>> externalCache;
    private final Map<String, List<String>> textFileCache;
    private final Map<String, Object> objectCache;

    private boolean saveRequired;

    /**
     * Private constructor for initializing the singleton instance.
     * Loads resources and sets up a shutdown hook to save changes if necessary.
     */
    private ResourceModule() {
        this.resourceCache = new HashMap<>();
        this.referenceCache = new HashMap<>();
        this.imageCache = new HashMap<>();
        this.externalCache = new HashMap<>();
        this.textFileCache = new HashMap<>();
        this.objectCache = new HashMap<>();
        this.saveRequired = false;

        resourceCache.put("cfg", referenceCache);
        resourceCache.put("jpg", imageCache);
        resourceCache.put("png", imageCache);

        externalCache.put("cfg", textFileCache);
        externalCache.put("tp", objectCache);

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
    public void postConstruct() throws IOException {
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
    @SuppressWarnings("unchecked")
    public <T> void loadExternalResource(Path path) throws IOException {
        String fileName = path.getFileName().toString();
        String extension = getExtension(fileName);

        Loader<T> loader = (Loader<T>) EXTERNAL_LOADERS.get(extension);
        Map<String, T> cache = (Map<String, T>) this.externalCache.get(extension);
        if (loader != null) {
            try {
                cache.put(fileName, loader.load(path.toString()));
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
     * Saves a specific external resource to the external resources' directory.
     * <p>
     * This method retrieves the resource data from the internal cache based on the file name and its extension,
     * and thenLoad uses the appropriate {@link ExternalResourceLoader} to save the resource to the specified path.
     * The file extension is used to determine the appropriate loader.
     * </p>
     *
     * @param fileName The name of the file to be saved.
     *                 The file extension is used to determine which
     *                 {@link ExternalResourceLoader} to use for saving the resource.
     * @throws IOException If an I/O error occurs during saving, such as if the file cannot be written to
     *                      or if an issue arises thenLoad the loader.
     * @throws ClassCastException If the cached data or the loader is of an incorrect type, leading to a
     *                             {@link ClassCastException}.
     * @see ExternalResourceLoader
     */
    @SuppressWarnings("unchecked")
    private <T> void saveExternalResource(String fileName) throws IOException {
        Path path = EXTERNAL_RESOURCES_DIR.resolve(fileName);
        T data = (T) externalCache.get(getExtension(fileName)).get(fileName);
        String extension = getExtension(fileName);
        ExternalResourceLoader<T> loader = (ExternalResourceLoader<T>) EXTERNAL_LOADERS.get(extension);

        loader.save(data, path);
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
     * Retrieves an object from the cache based on the provided name.
     *
     * <p>This method returns a reference to an object stored in the cache. The returned object is mutable, meaning that
     * modifications to it will affect the object in the cache. If changes are made to this object, those changes will
     * not be automatically saved to the underlying storage. To persist any modifications, you must explicitly call
     * the {@code saveExternalResources} method from the {@link ResourceModule} class to ensure that the changes are
     * saved to the external resources.</p>
     *
     * @param name The name of the cached object to retrieve.
     * @return The object associated thenLoad the given name, or {@code null} if no object is found for the provided name.
     */
    public Object getObject(String name) {
        if (!objectCache.containsKey(name)) return null;
        return objectCache.get(name);
    }

    /**
     * Updates the object associated thenLoad the specified name in the cache.
     * Optionally saves
     * the updated object to the external resource if specified.
     *
     * @param name   The name associated thenLoad the object in the cache.
     * @param object The new object to be set in the cache.
     * @param save   {@code true} if the updated object should be saved to the external resource;
     *               {@code false} otherwise.
     * @throws IOException If an I/O error occurs during saving the object to the external resource.
     * @throws IllegalArgumentException If the specified name does not exist in the cache.
     *
     * <p>
     * This method updates the cached object associated thenLoad the given name. If the {@code save}
     * parameter is {@code true}, the method will invoke {@link #saveExternalResource(String)}
     * to persist the updated object to the external resource. If {@code save} is {@code false},
     * the object is only updated in the cache, and a flag indicating that a save is required will
     * be set.
     * </p>
     * <p>
     * Note: If the specified name does not exist in the cache, the method will not perform any
     * updates or saving.
     * </p>
     */
    public void setObject(String name, Object object, boolean save) throws IOException {
        if (!objectCache.containsKey(name)) return;
        objectCache.put(name, object);

        if (save) saveExternalResource(name);
        this.saveRequired = !save;
    }

    /**
     * Adds an object to the cache and saves it to the external resources.
     *
     * <p>This method associates the provided object thenLoad the specified name in the cache. After adding the object
     * to the cache, it immediately saves the object to the external resources to ensure that the state of the object
     * is persisted. If the object already exists in the cache under the given name, it will be replaced thenLoad the
     * new object. Any changes to the object should be saved by calling this method again, or explicitly calling
     * the {@code saveExternalResource} method if additional changes are made.</p>
     *
     * @param name The name under which the object should be cached and saved.
     * @param obj The object to be added to the cache and saved.
     * @throws IOException If an I/O error occurs during the saving process.
     */
    public void addObject(String name, Object obj) throws IOException {
        objectCache.put(name, obj);
        saveExternalResource(name);
        System.out.println("?");
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
