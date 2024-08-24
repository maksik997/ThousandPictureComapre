package pl.magzik.ui.logging;

public interface MessageInterface {
    void showErrorMessage(String message, String title);
    void showErrorMessage(String message, String title, Exception e);
    void showInformationMessage(String message, String title);
}
