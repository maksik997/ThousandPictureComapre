package pl.magzik.ui.views;

import pl.magzik.modules.resource.ResourceModule;
import pl.magzik.ui.components.ComponentUtils;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;

/**
 * Represents a menu view in the application's user interface.
 * This view contains buttons for various menu options and displays an icon and title.
 */
public class MenuView extends JPanel {

    private final JButton comparerButton, galleryButton, settingsButton, creditsButton, exitButton;

    /**
     * Constructs a {@code MenuView} thenLoad the specified buttons.
     *
     * @param comparerButton The button for the comparer option.
     * @param galleryButton The button for the gallery option.
     * @param settingsButton The button for the settings option.
     * @param creditsButton The button for the credit option.
     * @param exitButton The button for the exit option.
     */
    private MenuView(JButton comparerButton, JButton galleryButton, JButton settingsButton, JButton creditsButton, JButton exitButton) {
        this.comparerButton = comparerButton;
        this.galleryButton = galleryButton;
        this.settingsButton = settingsButton;
        this.creditsButton = creditsButton;
        this.exitButton = exitButton;

        initialize();
    }

    /**
     * Initializes the layout and components of the menu view.
     */
    private void initialize() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        Insets baseInsets = new Insets(5, 0, 5, 0);

        addIcon(gbc, baseInsets);
        addTitleLabel(gbc);
        addVersionTagLabel(gbc);
        addButtons(gbc, baseInsets);
    }

    /**
     * Adds the icon label to the menu view.
     *
     * @param gbc The {@code GridBagConstraints} used for layout management.
     * @param insets The insets to be applied for the icon label.
     */
    private void addIcon(GridBagConstraints gbc, Insets insets) {
        ImageIcon icon = ComponentUtils.getScaledImage(new ImageIcon(ResourceModule.getInstance().getImage("thumbnail.png")), 200, 200);
        JLabel iconLabel = new JLabel(icon);

        gbc.insets = insets;
        add(iconLabel, gbc);
        gbc.gridy++;
    }

    /**
     * Adds the title label to the menu view.
     *
     * @param gbc The {@code GridBagConstraints} used for layout management.
     */
    private void addTitleLabel(GridBagConstraints gbc) {
        JLabel title = new JLabel("general.title");
        title.setFont(ComponentUtils.fontBigHelveticaBold);
        title.setBorder(
            new MatteBorder(0,0,1,0, Color.GRAY)
        );

        gbc.insets = new Insets(5, 0, 0, 0);
        add(title, gbc);
        gbc.gridy++;
    }

    /**
     * Adds the version tag label to the menu view.
     *
     * @param gbc The {@code GridBagConstraints} used for layout management.
     */
    private void addVersionTagLabel(GridBagConstraints gbc) {
        JLabel versionTag = new JLabel("general.version");
        versionTag.setFont(ComponentUtils.fontSmallHelveticaBold);

        gbc.insets = new Insets(0, 0, 10, 0);
        add(versionTag, gbc);
        gbc.gridy++;
    }

    /**
     * Adds the buttons to the menu view.
     *
     * @param gbc The {@code GridBagConstraints} used for layout management.
     * @param insets The insets to be applied for the buttons.
     */
    private void addButtons(GridBagConstraints gbc, Insets insets) {
        gbc.insets = insets;
        add(comparerButton, gbc);
        gbc.gridy++;
        add(galleryButton, gbc);
        gbc.gridy++;
        add(settingsButton, gbc);
        gbc.gridy++;
        add(creditsButton, gbc);
        gbc.gridy++;
        add(exitButton, gbc);
    }

    /**
     * Gets the comparer button.
     *
     * @return The comparer button.
     */
    public JButton getComparerButton() {
        return comparerButton;
    }

    /**
     * Gets the gallery button.
     *
     * @return The gallery button.
     */
    public JButton getGalleryButton() {
        return galleryButton;
    }

    /**
     * Gets the settings button.
     *
     * @return The settings button.
     */
    public JButton getSettingsButton() {
        return settingsButton;
    }

    /**
     * Gets the credit button.
     *
     * @return The credit button.
     */
    public JButton getCreditsButton() {
        return creditsButton;
    }

    /**
     * Gets the exit button.
     *
     * @return The exit button.
     */
    public JButton getExitButton() {
        return exitButton;
    }

    /**
     * A factory class for creating instances of {@code MenuView}.
     */
    public static class Factory {

        /**
         * Creates and configures a new instance of {@code MenuView}.
         *
         * @return A fully configured {@code MenuView} instance.
         */
        public static MenuView create() {
            JButton comparerButton = createButton("view.menu.button.comparer");
            JButton galleryButton = createButton("view.menu.button.gallery");
            JButton settingsButton = createButton("view.menu.button.settings");
            JButton creditsButton = createButton("view.menu.button.credits");
            JButton exitButton = createButton("view.menu.button.exit");

            return new MenuView(
                comparerButton,
                galleryButton,
                settingsButton,
                creditsButton,
                exitButton
            );
        }

        /**
         * Creates a {@code JButton} thenLoad the specified title and common properties.
         *
         * @param title The title key for the button.
         * @return A {@code JButton} thenLoad the specified title and properties.
         */
        private static JButton createButton(String title) {
            JButton button = ComponentUtils.buttonFactory(title, new Insets(10, 15, 10, 15));
            button.setPreferredSize(new Dimension(200, 50));
            return button;
        }
    }
}
