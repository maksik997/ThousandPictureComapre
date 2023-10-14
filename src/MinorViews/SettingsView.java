package MinorViews;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;

public class SettingsView extends JPanel {
    private JButton backButton, destDirButton;

    private JTextField destDirTextField;
    private final JFileChooser fileChooser;


    public SettingsView() {
        this.setLayout(new BorderLayout());

        fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        _init();
    }

    private void _init(){
        JPanel header = new JPanel();
        header.setBorder(new MatteBorder(0,0,1,0, Color.GRAY));

        JLabel settingsLabel = new JLabel("Settings");
        settingsLabel.setFont(new Font("Helvetica", Font.BOLD, 32));

        header.add(settingsLabel);

        this.add(header, BorderLayout.NORTH);

        JPanel main = new JPanel();

        JLabel destDirLabel = new JLabel("Destination directory");
        main.add(destDirLabel);
        destDirTextField = new JTextField(50);
        main.add(destDirTextField);
        destDirButton = new JButton("Open");
        main.add(destDirButton);

        this.add(main);

        backButton = new JButton("Back");
        this.add(backButton, BorderLayout.SOUTH);
    }

    public JButton getBackButton() {
        return backButton;
    }

    public JButton getDestDirButton() {
        return destDirButton;
    }

    public JTextField getDestDirTextField() {
        return destDirTextField;
    }

    public JFileChooser getFileChooser() {
        return fileChooser;
    }
}
