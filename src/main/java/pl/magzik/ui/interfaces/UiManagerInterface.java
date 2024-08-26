package pl.magzik.ui.interfaces;

import pl.magzik.ui.components.Utility;

import java.awt.*;

/**
 * Interface defining methods for managing user interface scenes and cursor changes.
 */
public interface UiManagerInterface {
    /**
     * Equivalent to {@code Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR)}
     * 
     * @see Cursor#getPredefinedCursor(int)
     * */
    Cursor DEFAULT_CURSOR = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR),
    /**
     * Equivalent to {@code Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR)}
     *
     * @see Cursor#getPredefinedCursor(int)
     * */
           WAIT_CURSOR = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);

    /**
     * Switches the current scene to the specified one.
     *
     * @param scene The scene to be displayed.
     *             This should be one of the predefined scenes in the {@link Utility.Scene} enumeration.
     */
    void toggleScene(Utility.Scene scene);
    /**
     * Sets the cursor for the UI.
     *
     * @param cursor The cursor to be set. This should be an instance of {@link Cursor}.
     */
    void useCursor(Cursor cursor);
}
