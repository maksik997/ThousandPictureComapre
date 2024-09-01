package pl.magzik.ui.localization;

import pl.magzik.View;
import pl.magzik.modules.gallery.table.TablePropertyAccess;
import pl.magzik.modules.gallery.table.GalleryTableModel;
import pl.magzik.ui.components.filechoosers.FileChooser;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ResourceBundle;

/**
 * Strategy for translating components in the user interface to the current locale.
 * <p>
 * This class extends {@link DefaultTranslationStrategy} and provides methods to translate the text
 * attributes of various UI components such as labels, buttons, checkboxes, file choosers, and table columns.
 * </p>
 */
public class ComponentTranslationStrategy extends DefaultTranslationStrategy {

    /**
     * Constructs a new {@code ComponentTranslationStrategy} thenLoad the specified {@link ResourceBundle}.
     *
     * @param resourceBundle the {@link ResourceBundle} containing translations. Must not be {@code null}.
     * @throws IllegalArgumentException if {@code resourceBundle} is {@code null}.
     */
    public ComponentTranslationStrategy(ResourceBundle resourceBundle) {
        super(resourceBundle);
    }

    /**
     * Translates the components of the provided {@link View} instance to the current locale.
     * <p>
     * This method translates the title of the view, and recursively translates all components within
     * the scenes managed by the view. It also translates the text in file choosers used in the view's
     * different components.
     * </p>
     *
     * @param view the {@link View} whose components are to be translated. Must not be {@code null}.
     * @throws IllegalArgumentException if {@code view} is {@code null}.
     */
    public void translateComponents(View view) {
        view.setTitle(translate(view.getTitle()));
        for (JPanel panel : view.getSceneManager().getScenes()) translateComponents(panel);

        FileChooser<?>[] fileChoosers = {
            view.getComparerView().getFileChooser(),
            view.getSettingsView().getDestinationEntry().getFileChooser(),
            view.getGalleryView().getFileChooser()
        };

        translateComponents(fileChoosers);
    }

    /**
     * Recursively translates the text attributes of all components within the specified container.
     * <p>
     * This method processes various types of components, including {@link JLabel}, {@link JButton},
     * {@link JCheckBox}, and {@link TitledBorder}. It also handles tab titles in {@link JTabbedPane}
     * components and recursively processes child components of containers.
     * </p>
     *
     * @param container the {@link Container} whose components are to be translated. Must not be {@code null}.
     * @throws IllegalArgumentException if {@code container} is {@code null}.
     */
    public void translateComponents(Container container) {
        String key;
        for (Component component : container.getComponents()) {
            if (component instanceof JComponent c) {
                if (c.getBorder() instanceof TitledBorder border) {
                    key = border.getTitle();
                    border.setTitle(translate(key));
                }
            }

            if (component instanceof JLabel label) {
                key = label.getText();
                label.setText(translate(key));
            } else if (component instanceof JButton button) {
                key = button.getText();
                button.setText(translate(key));
            } else if (component instanceof JCheckBox checkBox) {
                key = checkBox.getText();
                checkBox.setText(translate(key));
            } else if (component instanceof Container) {
                if (component instanceof JTabbedPane tabbedPane) {
                    for (int i = 0; i < tabbedPane.getTabCount(); i++) {
                        key = tabbedPane.getTitleAt(i);
                        tabbedPane.setTitleAt(i, translate(key));
                    }
                }
                translateComponents((Container) component);
            }
        }
    }

    /**
     * Translates the text attributes of the specified {@link FileChooser} instances.
     * <p>
     * This method updates the dialog title and approve button text of each {@link JFileChooser} used
     * in the provided {@link FileChooser} instances to reflect the current locale settings.
     * </p>
     *
     * @param fileChoosers an array of {@link FileChooser} instances whose {@link JFileChooser} components are to be translated.
     *                    Must not be {@code null}.
     * @throws IllegalArgumentException if {@code fileChoosers} is {@code null}.
     */
    public void translateComponents(FileChooser<?>... fileChoosers) {
        for (FileChooser<?> fc : fileChoosers) {
            JFileChooser fileChooser = fc.getFileChooser();

            String titleKey = fileChooser.getDialogTitle();
            String approveButtonKey = fileChooser.getApproveButtonText();

            fileChooser.setDialogTitle(translate(titleKey));
            fileChooser.setApproveButtonText(translate(approveButtonKey));
        }
    }

    /**
     * Translates the column names of the provided {@link GalleryTableModel} to the current locale.
     * <p>
     * This method updates the column names in the {@link GalleryTableModel} using the current locale
     * and refreshes the table model to reflect the changes in the user interface.
     * </p>
     *
     * @param gtm the {@link GalleryTableModel} whose column names are to be translated. Must not be {@code null}.
     * @throws NullPointerException if {@code gtm} is {@code null}.
     */
    public void translateComponents(TablePropertyAccess gtm) {
        String key;
        for (int i = 0; i < gtm.getColumnCount(); i++) {
            key = gtm.getColumnName(i);
            gtm.setColumnName(i, translate(key));
        }
        gtm.refresh();
    }
}
