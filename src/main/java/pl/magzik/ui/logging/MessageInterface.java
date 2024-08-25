package pl.magzik.ui.logging;

/**
 * Provides methods for information and error showing.
 * */
public interface MessageInterface {
    /**
     * Shows error message. With given title and message.
     * @param title A {@link String} to be used as title.
     * @param message A {@link String} to be used as a message.
     * */
    void showErrorMessage(String message, String title);

    /**
     * Shows error message.
     * With given title, message and exception.
     * <p>
     * (Message must give place for an Exception)
     * @param title A {@link String} to be used as title.
     * @param message A {@link String} to be used as a message.
     * @param e A {@link Exception} to be used along with a message.
     * */
    void showErrorMessage(String message, String title, Exception e);

    /**
     * Shows information message. With given title and message.
     * @param title A {@link String} to be used as title.
     * @param message A {@link String} to be used as a message.
     * */
    void showInformationMessage(String message, String title);
}
