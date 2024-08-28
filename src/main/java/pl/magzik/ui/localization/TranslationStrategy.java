package pl.magzik.ui.localization;

/**
 * Defines methods for translating and reverse translating text.
 * <p>
 * Implementations of this interface provide a standardized way to handle translations of text and
 * reverse-translation of localized strings back to their original keys.
 * </p>
 */
public interface TranslationStrategy {

    /**
     * Translates the given text into the current locale.
     *
     * @param key the {@link String} to be translated. Can be {@code null}.
     * @return the translated value. If the key is {@code null} or not found, returns the original key.
     */
    String translate(String key);

    /**
     * Reverse-translates the given localized value to its original key.
     *
     * @param text the localized value to be reverse-translated. Can be {@code null}.
     * @return the original key if found; otherwise, returns the input string.
     */
    String reverseTranslate(String text);
}
