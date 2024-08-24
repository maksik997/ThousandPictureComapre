package pl.magzik.UiViews;

import pl.magzik.UiComponents.Utility;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.io.IOException;

public class LoadingFrame extends JFrame {
    public LoadingFrame() {
        this.add(new BackgroundPanel());

        ImageIcon icon = new ImageIcon("data/thumbnail_64x64.png");
        this.setIconImage(icon.getImage());
        this.setUndecorated(true);
        this.setSize(new Dimension(800, 650));
        this.setResizable(false);
        this.setShape(new RoundRectangle2D.Double(0,0, getWidth(), getHeight(), 20, 20));
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }


}

class BackgroundPanel extends JPanel {
    private final Image background;

    public BackgroundPanel() {
        try {
            background = ImageIO.read(new File("data/loadingImage.jpg"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        setLayout(new BorderLayout());

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(Color.DARK_GRAY);
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));

        JLabel loadingLabel = new JLabel("loading_frame.top.label");
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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (background != null) {
            g.drawImage(background, 0, 0, this);
        }
    }
}
