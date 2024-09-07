package pl.magzik.ui.cursor;

import java.awt.*;

/**
 * Interface defining methods for managing user interface scenes and cursor changes.
 */
public interface CursorManagerInterface {
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
     * Sets the cursor for the UI.
     *
     * @param cursor The cursor to be set. This should be an instance of {@link Cursor}.
     */
    void useCursor(Cursor cursor);
}
