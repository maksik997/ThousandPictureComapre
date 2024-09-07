package pl.magzik.modules.gallery.operations;

/**
 * Provides access to the properties of a gallery, including options for
 * normalizing file names and extensions.
 * <p>
 * This interface defines methods for setting and retrieving properties related to
 * the naming and extension normalization of files in a gallery. Implementations
 * of this interface should provide the logic to handle these properties and ensure
 * that they are applied appropriately.
 * </p>
 */
public interface GalleryPropertyAccess {

    /**
     * Sets the template for normalizing file names.
     * <p>
     * This method allows specifying a template that will be used to normalize
     * file names. The template defines how file names should be formatted or
     * adjusted to ensure consistency across the gallery.
     * </p>
     *
     * @param normalizedNameTemplate the template to use for normalizing file names
     */
    void setNormalizedNameTemplate(String normalizedNameTemplate);

    /**
     * Sets whether file extensions should be normalized to lowercase.
     * <p>
     * This method allows enabling or disabling the normalization of file extensions.
     * When set to {@code true}, all file extensions will be converted to lowercase
     * to ensure consistency and avoid case sensitivity issues.
     * </p>
     *
     * @param normalizedFileExtensions {@code true} to normalize file extensions to lowercase, {@code false} otherwise
     */
    void setNormalizedFileExtensions(boolean normalizedFileExtensions);
}
