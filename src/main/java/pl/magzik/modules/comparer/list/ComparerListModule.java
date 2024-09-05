package pl.magzik.modules.comparer.list;

import pl.magzik.modules.base.Module;

import javax.swing.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of {@link Module} and {@link ListModelHandler} that manages {@link DefaultListModel} instances.
 *
 * <p>This module initializes and manages list models for different purposes, such as displaying output and duplicates.</p>
 */
public class ComparerListModule implements Module, ListModelHandler<String> {
    private final Map<String, DefaultListModel<String>> listModels;

    /**
     * Constructs a {@code ComparerListModule} thenLoad an empty list of models.
     */
    public ComparerListModule() {
        listModels = new HashMap<>();
    }

    @Override
    public void postConstruct() {
        listModels.put("Output", new DefaultListModel<>());
        listModels.put("Duplicates", new DefaultListModel<>());
    }

    @Override
    public void addAllToList(String listName, Collection<String> strings) {
        if (!listModels.containsKey(listName))
            throw new IllegalArgumentException("List " + listName + " does not exist");

        listModels.get(listName).addAll(strings);
    }

    @Override
    public void clearList(String listName) {
        if (!listModels.containsKey(listName))
            throw new IllegalArgumentException("List " + listName + " does not exist");

        listModels.get(listName).clear();
    }

    @Override
    public ListModel<String> getListModel(String listName) {
        if (!listModels.containsKey(listName))
            throw new IllegalArgumentException("List " + listName + " does not exist");

        return listModels.get(listName);
    }
}
