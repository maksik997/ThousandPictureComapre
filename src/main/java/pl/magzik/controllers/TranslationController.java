package pl.magzik.controllers;

import pl.magzik.ui.interfaces.TranslationInterface;

import java.util.ResourceBundle;

/**
 * Controller responsible for handling translations within the application.
 * <p>
 * The {@code TranslationController} class provides methods for translating keys to their localized strings
 * using a {@link ResourceBundle}. It implements the {@link TranslationInterface}, providing a standardized
 * way to translate keys and reverse translate localized strings back to their original keys.
 * </p>
 */
public class TranslationController implements TranslationInterface {
    private final ResourceBundle resourceBundle;

    /**
     * Constructs a new {@code TranslationController} with the specified {@link ResourceBundle}.
     * <p>
     * The provided {@code ResourceBundle} is used to look up translations for keys and perform reverse lookups.
     * </p>
     *
     * @param resourceBundle the {@link ResourceBundle} containing the translations. Must not be {@code null}.
     */
    public TranslationController(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
    }

    /**
     * Translates the provided key into its corresponding localized string.
     * <p>
     * This method looks up the specified key in the {@link ResourceBundle} and returns the corresponding
     * localized string. If the key is not found or is {@code null}, the original key is returned.
     * </p>
     *
     * @param key the key to be translated. Can be {@code null}.
     * @return the translated string if the key exists in the {@link ResourceBundle}; otherwise, returns the original key.
     */
    @Override
    public String translate(String key) {
        if (key != null && resourceBundle.containsKey(key)) {
            return resourceBundle.getString(key);
        }
        return key;
    }

    /**
     * Reverse-translates a localized string back to its original key.
     * <p>
     * This method searches the {@link ResourceBundle} for the original key corresponding to the given localized string.
     * If a match is found, the original key is returned; otherwise, the input string is returned unchanged.
     * </p>
     *
     * @param key the localized string to reverse-translate. Can be {@code null}.
     * @return the original key if found; otherwise, returns the input string.
     */
    @Override
    public String reverseTranslate(String key) {
        return resourceBundle.keySet().stream()
            .filter(k -> resourceBundle.getString(k).equals(key))
            .findFirst()
            .orElse(key);
    }
}
