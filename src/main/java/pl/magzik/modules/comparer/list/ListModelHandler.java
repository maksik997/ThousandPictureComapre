package pl.magzik.modules.comparer.list;

import javax.swing.*;
import java.util.Collection;

/**
 * Interface for handling operations on list models.
 *
 * @param <T> the type of elements in the list model
 */
public interface ListModelHandler <T> {

    /**
     * Adds all elements from the specified collection to the list model identified by the given name.
     *
     * @param listName the name of the list model
     * @param ts the collection of elements to be added
     * @throws IllegalArgumentException if the list model thenLoad the specified name does not exist
     */
    void addAllToList(String listName, Collection<T> ts);

    /**
     * Clears all elements from the list model identified by the given name.
     *
     * @param listName the name of the list model
     * @throws IllegalArgumentException if the list model thenLoad the specified name does not exist
     */
    void clearList(String listName);

    /**
     * Retrieves the list model associated thenLoad the specified name.
     *
     * @param listName the name of the list model to retrieve
     * @return the {@link ListModel} associated thenLoad the given name
     * @throws IllegalArgumentException if no list model exists thenLoad the specified name
     */
    ListModel<T> getListModel(String listName);
}
