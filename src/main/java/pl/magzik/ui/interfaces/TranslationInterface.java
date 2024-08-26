package pl.magzik.ui.interfaces;

/**
 * Provides methods for translating and reverse translating.
 * Could be used to flatten other classes when needed.
 * */
public interface TranslationInterface {
   /**
    * Translates the given text.
    * @param key The {@link String} to be translated.
    * @return The translated value.
    * */
    String translate(String key);

    /**
     * Reversely translates the given value to its key form.
     *
     * @param text Translated value to be reverse translated.
     * @return The retrieved key.
     * */
    String reverseTranslate(String text);
}
