package pl.magzik.ui.interfaces;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * A functional interface that serves as a unified {@link DocumentListener}.
 * <p>
 * This interface provides a single method, {@link #onUpdate(DocumentEvent)},
 * which is invoked for all three types of document updates: insert, remove,
 * and change. This simplifies the process of handling document changes
 * by allowing you to implement only one method instead of three.
 * </p>
 *
 * <h3>Usage Example:</h3>
 * <pre>{@code
 * JTextField textField = new JTextField();
 * textField.getDocument().addDocumentListener((UnifiedDocumentListener) event -> {
 *     // Handle document update
 *     System.out.println("Document updated!");
 * });
 * }</pre>
 *
 * <p>Note: This interface is marked as a {@link FunctionalInterface},
 * allowing you to use lambda expressions or method references when
 * implementing it.</p>
 *
 * @see DocumentListener
 * @see DocumentEvent
 */
@FunctionalInterface
public interface UnifiedDocumentListener extends DocumentListener {

    /**
     * Called when the document is updated, regardless of whether
     * the update was an insertion, removal, or style change.
     *
     * @param e the document event providing details about the change
     */
    void onUpdate(DocumentEvent e);

    @Override
    default void insertUpdate(DocumentEvent e) {
        onUpdate(e);
    }

    @Override
    default void removeUpdate(DocumentEvent e) {
        onUpdate(e);
    }

    @Override
    default void changedUpdate(DocumentEvent e) {
        onUpdate(e);
    }
}
