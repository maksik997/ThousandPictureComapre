package pl.magzik.modules.comparer;

/**
 * Interface for accessing and modifying comparer properties.
 * Provides methods to get and set input and output paths and the mode of operation.
 */
public interface ComparerPropertyAccess {

    /**
     * Gets the path where the output should be stored.
     *
     * @return the output path as a {@link String}
     */
    String getOutputPath();

    /**
     * Sets the path where the output should be stored.
     *
     * @param path the output path to set
     */
    void setOutputPath(String path);

    /**
     * Gets the mode of operation.
     *
     * @return the current mode as a {@link Mode}
     */
    Mode getMode();

    /**
     * Sets the mode of operation.
     *
     * @param mode the mode to set
     */
    void setMode(Mode mode);

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

    /**
     * Enum representing different modes of operation.
     */
    enum Mode {

        /**
         * Represents the recursive mode.
         */
        RECURSIVE,

        /**
         * Represents the non-recursive mode.
         */
        NOT_RECURSIVE;

        /**
         * Checks if the current mode is recursive.
         *
         * @return {@code true} if the mode is {@link #RECURSIVE}, {@code false} otherwise
         */
        public boolean isRecursive() {
            return this == RECURSIVE;
        }
    }
}
