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

public class LoadingFrame extends JFrame implements PropertyChangeListener {

    private final JProgressBar progressBar;
    private final JLabel loadingLabel;

    private LoadingFrame(JProgressBar progressBar, JLabel loadingLabel) throws HeadlessException {
        this.progressBar = progressBar;
        this.loadingLabel = loadingLabel;

        createBackgroundPanel();

        initialize();
    }

    private void initialize() {
        ImageIcon icon = new ImageIcon("data/thumbnail_64x64.png");
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

    private void createBackgroundPanel() {
        JPanel backgroundPanel = new ImagePanel(new File("data/loadingImage.jpg"));
        backgroundPanel.setLayout(new BorderLayout());

        addPanel(backgroundPanel);
        add(backgroundPanel);
    }

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

    private void addProgressBar(JPanel panel) {
        progressBar.setStringPainted(true);
        progressBar.setFont(Utility.fontHelveticaBold);
        panel.add(progressBar);
    }

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

    private void updateLabel(String label) {
        String[] split = label.split("\\.");
        loadingLabel.setText(loadingLabel.getText().split(" ")[0] + " " + split[split.length - 1]);
    }

    private void updateProgress(double progress) {
        int newVal = (int) Math.round(progress * 100);

        progressBar.setValue(newVal);
        progressBar.setString(newVal + "%");
    }

    public static LoadingFrame createAndShow(ComponentTranslationStrategy translationStrategy) {
        LoadingFrame frame = new LoadingFrame(createProgressBar(), createLabel());
        translationStrategy.translateComponents(frame.getContentPane());

        SwingUtilities.invokeLater(() -> frame.setVisible(true));
        return frame;
    }

    private static JProgressBar createProgressBar() {
        return new JProgressBar(0, 100);
    }

    private static JLabel createLabel() {
        return new JLabel("loading_frame.top.label");
    }
}
