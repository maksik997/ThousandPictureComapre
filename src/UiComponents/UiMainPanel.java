//package UiComponents;
//
//import javax.swing.*;
//import javax.swing.border.MatteBorder;
//import javax.swing.border.TitledBorder;
//import java.awt.*;
//
//public class UiMainPanel extends JPanel {
//
//    private final UiPath uiPath;
//
//    private final UiOutput uiOutput;
//
//    private final UiTray uiTray;
//
//    private final JButton resetButton, loadButton, moveButton;
//
//    private final JLabel stateLabel;
//
//    public UiMainPanel() {
//        this.setLayout(new BorderLayout());
//
//        this.uiPath = new UiPath();
//        this.add(uiPath, BorderLayout.PAGE_START);
//
//        JPanel mainPanel = new JPanel();
//        mainPanel.setLayout(new BorderLayout());
//
//        this.uiTray = new UiTray();
//        mainPanel.add(uiTray, BorderLayout.NORTH);
//
//        this.uiOutput = new UiOutput();
//        mainPanel.add(uiOutput);
//
//        JPanel rightPanel = new JPanel();
//        rightPanel.setLayout(new GridLayout(2, 1));
//        rightPanel.setBorder(new MatteBorder(0, 1, 0, 0, Color.GRAY));
//
//        JPanel buttonPanel = new JPanel();
//        buttonPanel.setLayout(new GridBagLayout());
//        GridBagConstraints gbc = new GridBagConstraints();
//        gbc.insets = new Insets(5, 0, 5, 0);
//        gbc.gridx = 0;
//        gbc.gridy = 0;
//        gbc.weighty = 0;
//        gbc.anchor = GridBagConstraints.NORTH;
//        gbc.fill = GridBagConstraints.HORIZONTAL;
//
//        Insets insets = new Insets(5, 15, 5, 15);
//        this.loadButton = Utility.buttonFactory("Load files & compare", insets);
//        this.moveButton = Utility.buttonFactory("Move files", insets);
//        this.resetButton = Utility.buttonFactory("Reset", insets);
//
//        buttonPanel.add(loadButton, gbc);
//        gbc.gridy++;
//        buttonPanel.add(moveButton, gbc);
//        gbc.gridy++;
//        buttonPanel.add(resetButton, gbc);
//        gbc.gridy++;
//        gbc.weighty = 1;
//        buttonPanel.add(Box.createVerticalGlue(), gbc);
//
//        rightPanel.add(buttonPanel);
//
//        JPanel statePanel = new JPanel();
//        statePanel.setLayout(new GridLayout());
//        statePanel.setBorder(new TitledBorder(
//            new MatteBorder(1, 0, 0, 0, Color.GRAY),
//            "State:"
//        ));
//
//        this.stateLabel = new JLabel("Ready.");
//        this.stateLabel.setFont(Utility.fontBigHelveticaBold);
//        this.stateLabel.setHorizontalAlignment(SwingConstants.CENTER);
//        this.stateLabel.setVerticalAlignment(SwingConstants.CENTER);
//
//        statePanel.add(stateLabel);
//
//        rightPanel.add(statePanel);
//
//        this.add(rightPanel, BorderLayout.LINE_END);
//
//        this.add(mainPanel);
//    }
//
//    public void clear() {
//        uiPath.clearPath();
////        uiOutput.clear();
//        uiTray.clear();
//    }
//
//    // Getters
//    public UiPath getUiPath() {
//        return uiPath;
//    }
//
//    public UiOutput getUiOutput() {
//        return uiOutput;
//    }
//
//    public UiTray getUiTray() {
//        return uiTray;
//    }
//
//    public JButton getResetButton() {
//        return resetButton;
//    }
//
//    public JButton getLoadButton() {
//        return loadButton;
//    }
//
//    public JButton getMoveButton() {
//        return moveButton;
//    }
//
//    public JLabel getStateLabel() {
//        return stateLabel;
//    }
//}
