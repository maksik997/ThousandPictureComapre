package pl.magzik.ui;

import pl.magzik.ui.components.Utility;
import pl.magzik.ui.components.panels.ImagePanel;
import pl.magzik.ui.localization.ComponentTranslationStrategy;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

/**
 * The {@code LoadingFrame} class represents a custom JFrame that displays a loading screen
 * with a progress bar and a label. This frame is used to show loading progress in a visually
 * appealing manner. The frame is non-resizable, has rounded corners, and displays a background
 * image along with a progress bar and text.
 *
 * <p>The class implements {@link PropertyChangeListener} to update the progress bar and
 * the displayed label based on property changes.
 */
public class LoadingFrame extends JFrame implements PropertyChangeListener {

    private static final String iconPath = "data/thumbnail_64x64.png",
                                backgroundImagePath = "data/loadingImage.jpg";

    private final JProgressBar progressBar;
    private final JLabel loadingLabel;

    /**
     * Constructs a new {@code LoadingFrame} with the specified progress bar and loading label.
     * Initializes the frame and adds components to it.
     *
     * @param progressBar the {@link JProgressBar} to display the loading progress
     * @param loadingLabel the {@link JLabel} to display the loading text
     * @throws HeadlessException if GraphicsEnvironment.isHeadless() returns true
     */
    private LoadingFrame(JProgressBar progressBar, JLabel loadingLabel) throws HeadlessException {
        this.progressBar = progressBar;
        this.loadingLabel = loadingLabel;

        createBackgroundPanel();

        initialize();
    }

    /**
     * Initializes the frame with basic properties such as size, shape, and location.
     */
    private void initialize() {
        ImageIcon icon = new ImageIcon(iconPath);
        setType(Type.UTILITY);
        setAlwaysOnTop(true);
        setIconImage(icon.getImage());
        setUndecorated(true);
        setSize(new Dimension(800, 650));
        setResizable(false);
        setShape(new RoundRectangle2D.Double(0,0, getWidth(), getHeight(), 20, 20));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    /**
     * Creates and adds the background panel to the frame.
     */
    private void createBackgroundPanel() {
        JPanel backgroundPanel = new ImagePanel(new File(backgroundImagePath));
        backgroundPanel.setLayout(new BorderLayout());

        addPanel(backgroundPanel);
        add(backgroundPanel);
    }

    /**
     * Adds the progress bar and labels to the specified panel.
     *
     * @param mainPanel the main panel to which the components are added
     */
    private void addPanel(JPanel mainPanel) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        addProgressBar(panel);
        addLabel(
            panel,
            loadingLabel,
            new EmptyBorder(20, 0, 0, 10),
            Utility.fontHelveticaBold
        );
        addLabel(
            panel,
            new JLabel("loading_frame.bottom.label"),
            new EmptyBorder(0, 0, 20, 10),
            Utility.fontHelveticaPlain
        );

        mainPanel.add(panel, BorderLayout.SOUTH);
    }

    /**
     * Adds the progress bar to the specified panel.
     *
     * @param panel the panel to which the progress bar is added
     */
    private void addProgressBar(JPanel panel) {
        progressBar.setStringPainted(true);
        progressBar.setFont(Utility.fontHelveticaBold);
        panel.add(progressBar);
    }

    /**
     * Adds a label to the specified panel with the given border and font.
     *
     * @param panel the panel to which the label is added
     * @param label the label to be added
     * @param border the border to be set on the label
     * @param font the font to be set on the label
     */
    private void addLabel(JPanel panel, JLabel label, Border border, Font font) {
        label.setAlignmentX(Component.RIGHT_ALIGNMENT);
        label.setFont(font);
        label.setBorder(border);
        panel.add(label);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("module")) {
            SwingUtilities.invokeLater(() -> updateLabel((String) evt.getNewValue()));
        } else if (evt.getPropertyName().equals("progress")) {
            SwingUtilities.invokeLater(() -> updateProgress((double) evt.getNewValue()));
        }
    }

    /**
     * Updates the loading label with the new text.
     *
     * @param label the new text to be displayed on the loading label
     */
    private void updateLabel(String label) {
        String[] split = label.split("\\.");
        loadingLabel.setText(loadingLabel.getText().split(" ")[0] + " " + split[split.length - 1]);
    }

    /**
     * Updates the progress bar with the new progress value.
     *
     * @param progress the new progress value as a double between 0 and 1
     */
    private void updateProgress(double progress) {
        int newVal = (int) Math.round(progress * 100);

        progressBar.setValue(newVal);
        progressBar.setString(newVal + "%");
    }

    /**
     * Creates and shows a new {@code LoadingFrame}, translating its components using the specified translation strategy.
     *
     * @param translationStrategy the {@link ComponentTranslationStrategy} used to translate components in the frame
     * @return the created {@code LoadingFrame} instance
     */
    public static LoadingFrame createAndShow(ComponentTranslationStrategy translationStrategy) {
        LoadingFrame frame = new LoadingFrame(createProgressBar(), createLabel());
        translationStrategy.translateComponents(frame.getContentPane());

        SwingUtilities.invokeLater(() -> frame.setVisible(true));
        return frame;
    }

    /**
     * Creates and returns a new progress bar with a range from 0 to 100.
     *
     * @return the created {@link JProgressBar} instance
     */
    private static JProgressBar createProgressBar() {
        return new JProgressBar(0, 100);
    }

    /**
     * Creates and returns a new label with the default text "loading_frame.top.label".
     *
     * @return the created {@link JLabel} instance
     */
    private static JLabel createLabel() {
        return new JLabel("loading_frame.top.label");
    }
}
