package pl.magzik.modules.gallery;

import pl.magzik.modules.base.ModuleLoadException;
import pl.magzik.modules.base.Package;
import pl.magzik.modules.gallery.management.GalleryManagementModule;
import pl.magzik.modules.gallery.operations.GalleryOperationsModule;
import pl.magzik.modules.gallery.persistence.GalleryFileModule;
import pl.magzik.modules.gallery.table.GalleryEntry;
import pl.magzik.modules.gallery.table.GalleryTableModelHandler;
import pl.magzik.modules.resource.ResourceModule;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The {@code GalleryPackage} class is a package that combines various modules
 * for managing, operating, and persisting a gallery.
 * This package handles the
 * loading and saving of gallery items, ensuring that all necessary files are present.
 */
@SuppressWarnings({"unused", "FieldCanBeLocal"})
public class GalleryPackage extends Package {

    private final GalleryManagementModule galleryManagementModule;
    private final GalleryFileModule galleryFileModule;
    private final GalleryOperationsModule galleryOperationsModule;

    /**
     * Constructs a {@code GalleryPackage} with the specified management, file, and operations modules.
     *
     * @param galleryManagementModule  the module responsible for managing gallery items
     * @param galleryFileModule        the module responsible for file handling in the gallery
     * @param galleryOperationsModule  the module responsible for gallery operations (e.g., opening images)
     */
    public GalleryPackage(GalleryManagementModule galleryManagementModule, GalleryFileModule galleryFileModule, GalleryOperationsModule galleryOperationsModule) {
        super(List.of(galleryManagementModule, galleryFileModule, galleryOperationsModule));
        this.galleryManagementModule = galleryManagementModule;
        this.galleryFileModule = galleryFileModule;
        this.galleryOperationsModule = galleryOperationsModule;
    }

    @Override
    public void onModulesLoaded() throws ModuleLoadException {
        try {
            loadGalleryItems();
        } catch (IOException e) {
            throw new ModuleLoadException(e);
        }
    }

    /**
     * Loads gallery items from persistent storage. Validates the entries and adds them to the gallery table model.
     *
     * @throws IOException if the gallery entries contain paths to missing files
     */
    private void loadGalleryItems() throws IOException {
        GalleryTableModelHandler gtmh = galleryManagementModule.getTableModel();

        Object obj = ResourceModule.getInstance().getObject("gallery.tp");
        if (obj == null) {
            ResourceModule.getInstance().addObject("gallery.tp", new ArrayList<>());
            return;
        }

        List<GalleryEntry> entries = validateEntryList(obj);
        if (!entries.stream().map(GalleryEntry::getPath).allMatch(Files::exists))
            throw new IOException(
                    entries.stream()
                            .map(e -> String.format("Missing file: %s", e.getPath().toString()))
                            .collect(Collectors.joining("\n"))
            );

        gtmh.addEntries(entries);
    }

    /**
     * Saves the current gallery items to persistent storage.
     *
     * @throws IOException if an error occurs during saving
     */
    public void saveGalleryItems() throws IOException {
        ResourceModule.getInstance().setObject("gallery.tp", galleryManagementModule.getEntries(), true);
    }

    /**
     * Validates that the provided object is a {@link List} of {@link String}.
     *
     * @param obj The object to validate.
     * @return The object cast to a {@link List<String>} if it is valid.
     * @throws IllegalArgumentException if the object is not a {@link List<String>}.
     */
    @SuppressWarnings("unchecked")
    private List<GalleryEntry> validateEntryList(Object obj) {
        if (obj instanceof List<?> list) {
            if (list.isEmpty() || list.stream().allMatch(e -> e instanceof GalleryEntry))
                return (List<GalleryEntry>) obj;
        }

        throw new IllegalArgumentException("gallery.tp file is not of expected type.");
    }
}
