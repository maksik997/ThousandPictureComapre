package pl.magzik;

import com.formdev.flatlaf.util.SystemInfo;
import pl.magzik.modules.gallery.GalleryTableModel;
import pl.magzik.ui.components.general.FileChooser;
import pl.magzik.ui.interfaces.UiManagerInterface;
import pl.magzik.ui.components.Utility;
import pl.magzik.ui.interfaces.logging.MessageInterface;
import pl.magzik.ui.views.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;

public class View extends JFrame implements MessageInterface, UiManagerInterface {

    // Different Panels
    private final ComparerView comparerView;
    private final SettingsView settingsView;
    private final GalleryView galleryView;
    private final MenuView menuView;
    private final CreditsView creditsView;
    private final List<JPanel> scenes;
    private final ResourceBundle resourceBundle;

    public View(ResourceBundle resourceBundle) throws HeadlessException {
        scenes = new ArrayList<>();

        menuView = MenuView.Factory.create();
        galleryView = GalleryView.Factory.create();
        settingsView = SettingsView.Factory.create();
        comparerView = ComparerView.Factory.create();
        creditsView = new CreditsView();
        this.resourceBundle = resourceBundle;

        scenes.add(galleryView);
        scenes.add(settingsView);
        scenes.add(comparerView);
        scenes.add(menuView);
        scenes.add(creditsView);

        ImageIcon icon = new ImageIcon("data/thumbnail_64x64.png");

        this.add(menuView);

        if (SystemInfo.isMacOS) {
            getRootPane().putClientProperty("apple.awt.windowTitleVisible", false);
        }
        this.setTitle("general.title");
        this.setIconImage(icon.getImage());
        this.setMinimumSize(new Dimension(800, 650));
        this.pack();
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        addListeners();
    }

    private void addListeners() {
        scenes.forEach( p -> {
            if (p instanceof AbstractView) {
                ((AbstractView) p)
                    .getBackButton()
                    .addActionListener(_ -> toggleScene(Utility.Scene.MENU));
            }
        });
    }

    public ComparerView getComparerView() {
        return comparerView;
    }

    public SettingsView getSettingsView() {
        return settingsView;
    }

    public GalleryView getGalleryView() {
        return galleryView;
    }

    public MenuView getMenuView() {
        return menuView;
    }

    /**
     * Returns an unmodifiable view of the list of scenes.
     * <p>
     * This method provides access to the current list of scenes represented by {@code JPanel} instances.
     * The list returned by this method is a copy of the internal list to prevent external modifications.
     *
     * @return An unmodifiable {@code List} of {@code JPanel} instances representing the scenes.
     *
     * @see List#copyOf(Collection)
     */
    @Override
    public List<JPanel> getScenes() {
        return List.copyOf(scenes);
    }

    /**
     * Toggles the current UI scene to the specified scene.
     * This method removes all currently displayed scenes from the view and adds the new scene based on the given parameter.
     * After updating the scene, it repaints and revalidates the UI to reflect the changes.
     *
     * @param scene The scene to switch to, represented by the {@link Utility.Scene} enumeration.
     *              Valid values are SETTINGS, COMPARER, GALLERY, MENU, and CREDITS.
     */
    @Override
    public void toggleScene(Utility.Scene scene) {
        scenes.forEach(this::remove);

        switch (scene) {
            case SETTINGS -> add(settingsView);
            case COMPARER -> add(comparerView);
            case GALLERY -> add(galleryView);
            case MENU -> add(menuView);
            case CREDITS -> add(creditsView);
        }
        
        repaint();
        revalidate();
    }

    /**
     * Sets the cursor for the entire UI.
     * This method updates the cursor displayed over the UI components.
     *
     * @param cursor The cursor to be used, provided as an instance of {@link Cursor}.
     */
    @Override
    public void useCursor(Cursor cursor) {
        super.setCursor(cursor);
    }

    @Override
    public void showErrorMessage(String message, String title) {
        JOptionPane.showMessageDialog(
            this,
            String.format(message),
            title,
            JOptionPane.ERROR_MESSAGE
        );
    }

    @Override
    public void showErrorMessage(String message, String title, Exception e) {
        JOptionPane.showMessageDialog(
            this,
            String.format(message, e.getMessage()),
            title,
            JOptionPane.ERROR_MESSAGE
        );
    }

    @Override
    public void showInformationMessage(String message, String title) {
        JOptionPane.showMessageDialog(
            this,
            String.format(message),
            title,
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    @Override
    public int showConfirmationMessage(Object message, String title) {
        return JOptionPane.showConfirmDialog(
            this,
            message,
            title,
            JOptionPane.YES_NO_OPTION
        );
    }

    /**
     * Translates the components of the user interface to the current locale.
     * <p>
     * This method iterates through all components within the `scenes` collection and translates their titles, labels,
     * and other text attributes. It also translates the text of file choosers used within the `comparerView`, `settingsView`,
     * and `galleryView` components. This method is typically called after the UI components have been fully constructed to
     * ensure that all text is translated to the current locale.
     * </p>
     */
    public void translateComponents() {
        setTitle(translate(getTitle()));
        for (JPanel panel : scenes) translateComponents(panel);

        FileChooser<?>[] fileChoosers = {
            comparerView.getFileChooser(),
            settingsView.getDestinationEntry().getFileChooser(),
            galleryView.getFileChooser()
        };

        translateComponents(fileChoosers);
    }

    /**
     * Recursively translates the components within the specified container.
     * <p>
     * This method traverses all components contained within the provided {@link Container}. It translates the text of
     * {@link JLabel}, {@link JButton}, {@link JCheckBox}, and {@link TitledBorder} components. Additionally, it handles
     * translation of tab titles in {@link JTabbedPane} components. For containers, it recursively processes their child
     * components.
     * </p>
     *
     * @param container the {@link Container} whose components are to be translated. Must not be {@code null}.
     */
    private void translateComponents(Container container) {
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
     * Translates the text attributes of the specified file choosers.
     * <p>
     * This method updates the dialog title and approve button text of each {@link JFileChooser} contained within the provided
     * {@link FileChooser} instances. It ensures that the file choosers reflect the current locale settings.
     * </p>
     *
     * @param fileChoosers an array of {@link FileChooser} instances whose {@link JFileChooser} components are to be translated.
     *                    Must not be {@code null}.
     */
    private void translateComponents(FileChooser<?>... fileChoosers) {
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
     * This method iterates through each column in the given {@link GalleryTableModel}, retrieves the column name,
     * translates it using the {@link #translate(String)} method, and sets the translated name back to the column.
     * After all column names have been translated, the method refreshes the table model to ensure the changes are
     * reflected in the user interface.
     * </p>
     *
     * @param gtm the {@link GalleryTableModel} whose column names are to be translated. Must not be {@code null}.
     * @throws NullPointerException if {@code gtm} is {@code null}.
     */
    public void translateComponents(GalleryTableModel gtm) {
        String key;
        for (int i = 0; i < gtm.getColumnCount(); i++) {
            key = gtm.getColumnName(i);
            gtm.setColumnName(i, translate(key));
        }
        gtm.refresh();
    }

    /**
     * Translates the provided key into the corresponding localized string.
     * <p>
     * This method looks up the specified key in the resource bundle and returns the translated string. If the key is not
     * found in the resource bundle or if the key is {@code null}, the original key is returned.
     * </p>
     *
     * @param key the key to be translated. Can be {@code null}.
     * @return the translated string if the key exists in the resource bundle; otherwise, returns the original key.
     */
    private String translate(String key) {
        if (key == null || !resourceBundle.containsKey(key)) return key;
        return resourceBundle.getString(key);
    }
}
