package pl.magzik.ui.views;

import pl.magzik.ui.components.Utility;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.Observer;

public class LoadingFrame extends JFrame implements PropertyChangeListener {

    private final BackgroundPanel backgroundPanel;

    public LoadingFrame() {
        backgroundPanel = new BackgroundPanel();
        this.add(backgroundPanel);

        ImageIcon icon = new ImageIcon("data/thumbnail_64x64.png");
        setType(Type.UTILITY);
        setAlwaysOnTop(true);
        this.setIconImage(icon.getImage());
        this.setUndecorated(true);
        this.setSize(new Dimension(800, 650));
        this.setResizable(false);
        this.setShape(new RoundRectangle2D.Double(0,0, getWidth(), getHeight(), 20, 20));
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("module")) {
            SwingUtilities.invokeLater(() -> backgroundPanel.setLabel((String) evt.getNewValue()));
        } else if (evt.getPropertyName().equals("progress")) {
            SwingUtilities.invokeLater(() -> {
                int newVal = (int) Math.round((double) evt.getNewValue() * 100);

                backgroundPanel.setValue(newVal);
                backgroundPanel.getProgressBar().setString(newVal + "%");
            });
        }
    }
}

class BackgroundPanel extends JPanel {
    private final Image background;

    private final JProgressBar progressBar;
    private final JLabel loadingLabel;

    public BackgroundPanel() {
        try {
            background = ImageIO.read(new File("data/loadingImage.jpg"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        setLayout(new BorderLayout());

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));

        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setFont(Utility.fontHelveticaBold);
        bottomPanel.add(progressBar);

        loadingLabel = new JLabel("loading_frame.top.label");
        loadingLabel.setAlignmentX(RIGHT_ALIGNMENT);
        loadingLabel.setFont(Utility.fontBigHelveticaBold);
        loadingLabel.setBorder(new EmptyBorder(20, 0, 0, 10));
        bottomPanel.add(loadingLabel);

        JLabel loadingText = new JLabel("loading_frame.bottom.label");
        loadingText.setAlignmentX(Component.RIGHT_ALIGNMENT);
        loadingText.setFont(Utility.fontHelveticaPlain);
        loadingText.setBorder(new EmptyBorder(0, 0, 20, 10));
        bottomPanel.add(loadingText);

        this.add(bottomPanel, BorderLayout.SOUTH);
    }

    public JProgressBar getProgressBar() {
        return progressBar;
    }

    public void setLabel(String label) {
        String[] split = label.split("\\.");
        loadingLabel.setText(loadingLabel.getText().split(" ")[0] + " " + split[split.length - 1]);
    }

    public void setValue(Integer value) {
        progressBar.setValue(value);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (background != null) {
            g.drawImage(background, 0, 0, this);
        }
    }
}
