package pl.magzik.modules.comparer;

import pl.magzik.base.async.AsyncTaskFactory;
import pl.magzik.base.interfaces.FileHandler;
import pl.magzik.modules.base.Package;
import pl.magzik.modules.comparer.list.ComparerListModule;
import pl.magzik.modules.comparer.list.ListModelHandler;
import pl.magzik.modules.comparer.persistence.ComparerFileModule;
import pl.magzik.modules.comparer.persistence.ComparerFilePropertyAccess;
import pl.magzik.modules.comparer.processing.ComparerModule;
import pl.magzik.modules.comparer.processing.ComparerProcessor;
import pl.magzik.modules.comparer.processing.ComparerPropertyAccess;

import javax.swing.*;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;

/**
 * The {@code ComparerCoordinator} class serves as a high-level coordinator for file comparison tasks.
 * It manages the interactions between file loading, processing (comparing files), moving, and deletion,
 * as well as updating list models and handling UI locking mechanisms.
 * <p>
 * This class integrates multiple modules such as {@link ComparerListModule} for handling list models,
 * {@link ComparerFileModule} for file-related operations, and {@link ComparerModule} for file comparison logic.
 * It also provides a unified interface for performing asynchronous tasks with error handling.
 *
 * <h3>Main Responsibilities:</h3>
 * <ul>
 *   <li>Load files and update internal input lists</li>
 *   <li>Compare files and update the output lists</li>
 *   <li>Move, delete, or manage files post-comparison</li>
 *   <li>Lock and unlock the UI components during long-running tasks</li>
 * </ul>
 *
 * <h3>Modules used:</h3>
 * <ul>
 *   <li>{@link ComparerListModule}: Manages list models and UI updates</li>
 *   <li>{@link ComparerFileModule}: Handles file loading, moving, and deletion</li>
 *   <li>{@link ComparerModule}: Performs file comparison logic</li>
 * </ul>
 *
 * @see ComparerModule
 * @see ComparerFileModule
 * @see ComparerListModule
 */
public class ComparerCoordinator implements AsyncTaskFactory {

    private final ListModelHandler<String> lmh;
    private final FileHandler fh;
    private final ComparerFilePropertyAccess fpa;
    private final ComparerProcessor comp;

    private final ComparerPackage cp;

    /**
     * Initializes the {@code ComparerCoordinator}, sets up the necessary modules for file comparison,
     * file handling, and list model handling.
     */
    public ComparerCoordinator() {
        ComparerListModule clm = new ComparerListModule();
        this.lmh = clm;

        ComparerFileModule cfm = new ComparerFileModule();
        this.fh = cfm;
        this.fpa = cfm;

        ComparerModule cm = new ComparerModule();
        this.comp = cm;

        this.cp = new ComparerPackage(clm, cfm, cm);
    }

    /**
     * Returns the comparer package which contains all related modules for file handling, list management, and comparison.
     *
     * @return The {@link ComparerPackage} containing core modules.
     */
    public Package getPackage() {
        return cp;
    }

    // Handle Tasks

    /**
     * Loads files into the comparison system. It fetches the input files using the {@link FileHandler},
     * processes them, and sets them into the {@link ComparerProcessor} for further comparison.
     * <p>
     * This method handles any {@link IOException} thrown during file loading by wrapping it in a {@link CompletionException}.
     */
    public void handleLoadFiles() {
        try {
            List<File> in = comp.getInput();
            List<File> out = fh.loadFiles(in);

            comp.setInput(out);
        } catch (IOException e) {
            throw new CompletionException(e);
        }
    }

    /**
     * Compares the input files using the {@link ComparerProcessor}.
     * This method processes the files and updates the output list for post-processing tasks.
     * <p>
     * Handles {@link IOException} and {@link ExecutionException} during the comparison process.
     */
    public void handleCompare() {
        try {
            comp.process();
        } catch (IOException | ExecutionException e) {
            throw new CompletionException(e);
        }
    }

    /**
     * Moves the compared output files to their respective destination using the {@link FileHandler}.
     * <p>
     * Any {@link IOException} encountered during the move operation is wrapped in a {@link CompletionException}.
     */
    public void handleMoveFiles() {
        try {
            fh.moveFiles(comp.getOutput());
        } catch (IOException e) {
            throw new CompletionException(e);
        }
    }

    /**
     * Deletes the compared output files using the {@link FileHandler}.
     * <p>
     * Any {@link IOException} encountered during the deletion process is wrapped in a {@link CompletionException}.
     */
    public void handleDeleteFiles() {
        try {
            fh.deleteFiles(comp.getOutput());
        } catch (IOException e) {
            throw new CompletionException(e);
        }
    }

    /**
     * Updates the list model with the provided source files and associates them with the given list name.
     *
     * @param listName The name of the list model to update.
     * @param src      The list of files to add to the list model.
     */
    public void handleFulfilList(String listName, List<File> src) {
        lmh.clearList(listName);
        lmh.addAllToList(listName, src.stream().map(File::getName).toList());
    }

    /**
     * Clears the list associated with the given list name.
     *
     * @param listName The name of the list to clear.
     */
    public void handleClearList(String listName) {
        lmh.clearList(listName);
    }

    /**
     * Locks the comparison module, preventing further modifications until the lock is released.
     * Typically called before long-running operations.
     */
    public void notifyLock() {
        comp.notifyLock();
    }

    /**
     * Unlocks the comparison module, allowing further modifications.
     * Typically called after long-running operations are complete.
     */
    public void notifyUnlock() {
        comp.notifyUnlock();
    }

    // Delegated setters, getters

    /**
     * Retrieves the input files currently loaded in the comparison processor.
     *
     * @return A list of input files.
     */
    public List<File> getInput() {
        return comp.getInput();
    }

    /**
     * Sets the input files for the comparison processor.
     *
     * @param input A list of files to set as input.
     */
    public void setInput(List<File> input) {
        comp.setInput(input);
    }

    /**
     * Sets the input files for the comparison processor by converting the provided file paths to {@link File} objects.
     *
     * @param path Varargs of file paths to convert and set as input.
     */
    public void setInput(String... path) {
        setInput(Arrays.stream(path).map(File::new).toList());
    }

    /**
     * Retrieves the output files generated after comparison.
     *
     * @return A list of output files.
     */
    public List<File> getOutput() {
        return comp.getOutput();
    }

    /**
     * Retrieves the list model associated with the specified list name.
     *
     * @param listName The name of the list model to retrieve.
     * @return The {@link ListModel} for the specified list name.
     */
    public ListModel<String> getListModel(String listName) {
        return lmh.getListModel(listName);
    }

    /**
     * Adds a property change listener to the comparison processor.
     * This listener is notified when properties of the comparison processor change.
     *
     * @param listener The listener to add.
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        comp.addPropertyChangeListener(listener);
    }

    /**
     * Provides access to the comparer property interface, which allows retrieving
     * and managing properties related to the comparison process.
     *
     * @return An instance of {@link ComparerPropertyAccess}.
     */
    public ComparerPropertyAccess getComparerPropertyAccess() {
        return comp;
    }

    /**
     * Provides access to the file property interface, allowing access to file properties
     * managed during the comparison process.
     *
     * @return An instance of {@link ComparerFilePropertyAccess}.
     */
    public ComparerFilePropertyAccess getComparerFilePropertyAccess() {
        return fpa;
    }
}
