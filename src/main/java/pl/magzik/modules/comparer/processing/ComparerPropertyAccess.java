package pl.magzik.modules.comparer.processing;

/**
 * Interface for accessing and modifying comparer properties.
 * Provides methods to get and set input and output paths and the mode of operation.
 */
public interface ComparerPropertyAccess {

    /**
     * Checks if perceptual hash comparison is enabled.
     *
     * @return {@code true} if perceptual hash comparison is enabled, {@code false} otherwise
     */
    boolean isPerceptualHash();

    /**
     * Enables or disables perceptual hash comparison.
     *
     * @param perceptualHash {@code true} to enable perceptual hash comparison, {@code false} to disable it
     */
    void setPerceptualHash(boolean perceptualHash);

    /**
     * Checks if pixel-by-pixel comparison is enabled.
     *
     * @return {@code true} if pixel-by-pixel comparison is enabled, {@code false} otherwise
     */
    boolean isPixelByPixel();

    /**
     * Enables or disables pixel-by-pixel comparison.
     *
     * @param pixelByPixel {@code true} to enable pixel-by-pixel comparison, {@code false} to disable it
     */
    void setPixelByPixel(boolean pixelByPixel);
}
