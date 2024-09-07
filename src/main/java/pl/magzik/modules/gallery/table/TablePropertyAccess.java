package pl.magzik.modules.gallery.table;

/**
 * Interface for accessing and modifying column properties in a table model.
 * <p>
 * This interface provides methods to get and set the names of columns by their index,
 * obtain the total number of columns, and refresh the table model to update the view.
 * It is useful for localizing or dynamically updating column names and ensuring the table view
 * reflects the current state of the table model.
 * </p>
 */
public interface TablePropertyAccess {

    /**
     * Retrieves the name of the column at the specified index.
     *
     * @param column the index of the column
     * @return the name of the column
     * @throws IndexOutOfBoundsException if the column index is out of range
     */
    String getColumnName(int column);

    /**
     * Sets the name of the column at the specified index.
     *
     * @param column the index of the column
     * @param name the new name for the column
     * @throws IndexOutOfBoundsException if the column index is out of range
     */
    void setColumnName(int column, String name);

    /**
     * Returns the number of columns in the table model.
     * <p>
     * This method provides the total count of columns available in the table model.
     * It is typically used to determine the size of the table and to iterate over the columns.
     * </p>
     *
     * @return the number of columns in the table model
     */
    int getColumnCount();

    /**
     * Returns the number of rows in the table model.
     * @return the number of rows in the table model.
     * */
    int getRowCount();

    /**
     * Refreshes the table model, causing the table to update its view to reflect the current state.
     * This can be useful after batch updates or changes to ensure the table view is consistent with the model.
     */
    void refresh();
}
