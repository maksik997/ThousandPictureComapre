package pl.magzik.modules.comparer.processing;

import pl.magzik.algorithms.Algorithm;
import pl.magzik.algorithms.PerceptualHash;
import pl.magzik.algorithms.PixelByPixel;
import pl.magzik.structures.ImageRecord;
import pl.magzik.structures.Record;
import pl.magzik.*;
import pl.magzik.base.interfaces.Processor;

import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

/**
 * An interface for processing image comparison tasks.
 * <p>
 * This interface defines a template for comparing images using various strategies
 * and handling the results. Implementations of this interface must provide specific
 * methods to retrieve input image files and handle the results of the comparison.
 * </p>
 * <p>
 * The comparison strategies include perceptual hash and pixel-by-pixel comparison,
 * and can be configured via methods in {@link ComparerPropertyAccess}.
 * </p>
 * <p>
 * This interface also provides methods for managing resource locking and unlocking
 * during processing. Implementations must ensure that resources are properly managed
 * to prevent concurrent access issues.
 * </p>
 */
public interface ComparerProcessor extends Processor, ComparerPropertyAccess {

    /**
     * Notifies listeners that the resource is now locked and should not be accessed.
     * <p>
     * This method signals that the resource is currently in a state where it cannot
     * be used by other components or threads. It is intended to be called before
     * performing operations that require exclusive access to the resource, ensuring
     * that other parts of the application are aware of the locked state and act accordingly.
     * </p>
     * <p>
     * Listeners registered thenLoad this method will be notified to update their state or
     * disable operations that depend on the locked resource.
     * </p>
     * <p>
     * This method does not implement actual locking logic; it only notifies listeners
     * about the change in the resource's availability. The actual locking and unlocking
     * operations should be managed by separate mechanisms.
     * </p>
     */
    void notifyLock();

    /**
     * Notifies listeners that the resource has been unlocked and is now available for use.
     * <p>
     * This method signals that the resource is no longer in a locked state and can be accessed
     * by other components or threads. It should be called after any operations requiring
     * exclusive access to the resource are completed.
     * </p>
     * <p>
     * The method also triggers property change notifications to inform any registered listeners
     * that the resource is now available, allowing them to update their state or re-enable operations
     * that depend on the previously locked resource.
     * </p>
     * <p>
     * This method does not perform the actual unlocking of resources; it only communicates the change
     * in availability. The actual release of locks and cleanup should be handled separately.
     * </p>
     */
    void notifyUnlock();

    /**
     * Processes the image files according to the configured strategies and handles the results.
     * <p>
     * This method retrieves the input image files using {@link #getInput()}, processes them
     * based on the configured strategies, and thenLoad handles the resulting files using
     * {@link #handle(List)}.
     * </p>
     * <p>
     * Before starting the processing, the method should acquire the necessary notifyLock to ensure
     * thread safety by calling {@link #notifyLock()} before invoking this method. Failing to acquire the notifyLock
     * beforehand may result in concurrent access issues.
     * </p>
     * <p>
     * This method will internally handle the comparison process and manage the output.
     * </p>
     *
     * @throws IOException           If an I/O error occurs during processing.
     * @throws ExecutionException    If an error occurs during execution.
     */
    @Override
    default void process() throws IOException, ExecutionException {
        List<File> input = getInput();
        List<File> output = compare(input);
        handle(output);
    }

    /**
     * Compares the given list of image files using the configured strategies.
     * <p>
     * This method uses {@link #processWithStrategy(List)} to process the images and
     * thenLoad extracts the duplicate files using {@link #extract(Map)}.
     * </p>
     *
     * @param input The list of image files to compare.
     * @return A list of files that are considered duplicates based on the comparison.
     */
    private List<File> compare(List<File> input) {
        return extract(processWithStrategy(input));
    }

    /**
     * Processes the image files using the appropriate strategy based on the current configuration.
     * <p>
     * This method calls {@link RecordProcessor#process(Collection, Function, Algorithm[])} thenLoad the appropriate
     * functions based on the comparison strategy configured via {@link ComparerPropertyAccess}.
     * </p>
     *
     * @param input The list of image files to process.
     * @return A map where the key represents the comparison result, and the value is a list of records.
     */
    @SuppressWarnings("unchecked")
    private Map<?, List<Record<BufferedImage>>> processWithStrategy(List<File> input) {
        Objects.requireNonNull(input);

        RecordProcessor rp = new RecordProcessor();

        List<Algorithm<?, ImageRecord>> algorithms = new ArrayList<>();
        if (isPerceptualHash()) algorithms.add(new PerceptualHash());
        if (isPixelByPixel()) algorithms.add(new PixelByPixel());

        return rp.process(input, ImageRecord::create, algorithms.toArray(new Algorithm[0]));
    }

    /**
     * Extracts the files from the map of records, filtering out non-duplicate entries.
     * <p>
     * This method processes the map to retrieve files that have duplicates based on the comparison.
     * </p>
     *
     * @param map The map of records to extract files from.
     * @return A list of files that are considered duplicates.
     */
    private List<File> extract(Map<?, List<Record<BufferedImage>>> map) {
        Set<File> org = new HashSet<>();
        List<File> output = new ArrayList<>();

        map.entrySet().stream()
        .filter(e -> e.getValue().size() > 1)
        .forEach(e -> {
            List<File> list = new ArrayList<>(e.getValue().stream().map(Record::getFile).toList());

            if (!output.contains(list.getFirst()))
                org.add(list.removeFirst());

            output.addAll(list.stream().filter(f -> !org.contains(f)).toList());
        });

        return output.stream().distinct().toList();
    }

    /**
     * Retrieves the list of input image files.
     * <p>
     * Implementations must provide the logic to obtain the input files that will be processed.
     * </p>
     *
     * @return The list of input image files.
     */
    List<File> getInput();

    /**
     * Sets the list of input files.
     *
     * <p>This method allows specifying the input files as a list. Alternatively,
     * you can use the varargs method to provide the input files directly.</p>
     *
     * @param input a list of {@link File} objects to be set as input files
     * @throws NullPointerException if the provided list is {@code null}
     */
    void setInput(List<File> input);

    /**
     * Sets the list of input files.
     *
     * <p>This default method allows specifying the input files as a variable number
     * of arguments. It internally converts the varargs input into a list and calls
     * the {@link #setInput(List)} method.</p>
     *
     * @param input the {@link File} objects to be set as input files
     * @throws NullPointerException if any of the provided file arguments are {@code null}
     */
    default void setInput(File... input) {
        setInput(Arrays.asList(input));
    }

    /**
     * Retrieves the list of output files.
     *
     * <p>This method returns the current list of files that have been processed or are
     * marked as output files. The returned list can be used to inspect or operate on the
     * files that have been designated as output.</p>
     *
     * @return a {@link List} of {@link File} objects representing the output files
     */
    List<File> getOutput();

    /**
     * Handles the list of output files after comparison.
     * <p>
     * Implementations must provide the logic for handling or processing the resulting list of files
     * after comparison.
     * </p>
     *
     * @param output The list of output files to handle.
     */
    void handle(List<File> output);

    /**
     * Adds a property change listener to this object.
     * <p>
     * The listener will be notified of any changes to the specified property.
     * </p>
     *
     * @param listener the listener to add
     * @throws NullPointerException if the listener is {@code null}
     */
    void addPropertyChangeListener(PropertyChangeListener listener);

}
