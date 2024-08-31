package pl.magzik.modules.comparer.file;

/**
 * Provides methods for accessing and configuring file properties related to the comparison process.
 */
public interface ComparerFilePropertyAccess {

    /**
     * Sets the path where the output files should be stored.
     *
     * @param path the output path to set. Must not be {@code null}.
     * @throws NullPointerException if {@code path} is {@code null}.
     */
    void setOutputPath(String path);

    /**
     * Sets the mode of operation for file handling.
     *
     * @param mode the mode to set. Must not be {@code null}.
     * @throws NullPointerException if {@code mode} is {@code null}.
     */
    void setMode(Mode mode);

    /**
     * Enum representing different modes of file load operation.
     */
    enum Mode {

        /**
         * Represents the recursive mode where files are loaded recursively.
         */
        RECURSIVE,

        /**
         * Represents the non-recursive mode where files are loaded only at the top level.
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
