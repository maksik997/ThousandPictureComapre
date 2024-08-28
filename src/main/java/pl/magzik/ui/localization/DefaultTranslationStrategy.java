package pl.magzik.ui.localization;

import java.util.ResourceBundle;

/**
 * Provides a default implementation for translating keys to localized strings and vice versa.
 * <p>
 * This class uses a {@link ResourceBundle} to perform translations and reverse translations, adhering
 * to the {@link TranslationStrategy} interface.
 * </p>
 */
public class DefaultTranslationStrategy implements TranslationStrategy {
    private final ResourceBundle resourceBundle;

    /**
     * Constructs a new {@code DefaultTranslationStrategy} with the specified {@link ResourceBundle}.
     *
     * @param resourceBundle the {@link ResourceBundle} containing translations. Must not be {@code null}.
     * @throws IllegalArgumentException if {@code resourceBundle} is {@code null}.
     */
    public DefaultTranslationStrategy(ResourceBundle resourceBundle) {
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
