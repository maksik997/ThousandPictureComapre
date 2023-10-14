package MinorViews;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;

public class LocationView extends JPanel {
    private final JFileChooser fileChooser;
    private JTextArea outputLog;
    private JTextField pathTextField;
    private JButton fileTransferButton, loadFilesButton, pathButton, settingsButton, resetButton;
    private String path;
    
    public LocationView() {
        this.setLayout(new BorderLayout());

        fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        _init();
    }

    private void _init(){
        this.add(_initHeader(), BorderLayout.NORTH);
        this.add(_initMain());
        this.add(_initFooter(), BorderLayout.SOUTH);
    }

    private JPanel _initHeader(){
        JPanel header = new JPanel();
        header.setLayout(new GridLayout(2,1));

        ImageIcon icon = new ImageIcon("data/thumbnail.png");
        JLabel mainTitle = new JLabel("Thousand Picture Redundancy", icon, JLabel.LEFT);
        mainTitle.setFont(new Font("Helvetica", Font.BOLD, 32));

        header.add(mainTitle);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBorder(new MatteBorder(0,0,1,0, Color.GRAY));

        settingsButton = new JButton("Settings");

        bottomPanel.add(settingsButton);

        header.add(bottomPanel);

        return header;
    }

    private JPanel _initMain(){
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        JPanel pathBox = new JPanel();
        pathBox.setLayout(new FlowLayout());

        JLabel pathLabel = new JLabel("Path:");

        pathBox.add(pathLabel);

        pathTextField = new JTextField();
        pathTextField.setColumns(58);
        pathTextField.setEditable(false);
        pathTextField.setFocusable(false);

        pathBox.add(pathTextField);

        pathButton = new JButton("Open");

        pathBox.add(pathButton);

        mainPanel.add(pathBox, BorderLayout.PAGE_START);

        outputLog = new JTextArea();
        outputLog.setEditable(false);
        outputLog.setColumns(64);
        outputLog.setRows(13);

        JScrollPane outputPanel = new JScrollPane(outputLog);

        mainPanel.add(outputPanel);

        return mainPanel;
    }

    private JPanel _initFooter(){
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new GridLayout(1,3));

        resetButton = new JButton("Reset");
        resetButton.setEnabled(false);
        bottomPanel.add(resetButton);

        loadFilesButton = new JButton("Load files");
        loadFilesButton.setEnabled(false);
        bottomPanel.add(loadFilesButton);

        fileTransferButton = new JButton("Move files");
        fileTransferButton.setEnabled(false);
        bottomPanel.add(fileTransferButton);

        return bottomPanel;
    }

    public void _reset(){
        fileTransferButton.setEnabled(false);
        loadFilesButton.setEnabled(false);
        resetButton.setEnabled(false);

        pathTextField.setText("");

        path = "";
    }

    // Couple of getters
    public JButton getFileTransferButton() {
        return fileTransferButton;
    }

    public JButton getLoadFilesButton() {
        return loadFilesButton;
    }

    public JButton getPathButton() {
        return pathButton;
    }

    public JFileChooser getFileChooser() {
        return fileChooser;
    }

    public JButton getSettingsButton() {
        return settingsButton;
    }

    public JButton getResetButton() {
        return resetButton;
    }

    public String getPath() {
        return path;
    }

    public JTextField getPathTextField() {
        return pathTextField;
    }

    public JTextArea getOutputLog() {
        return outputLog;
    }

    public void setPathTextField(JTextField pathTextField) {
        this.pathTextField = pathTextField;
    }

    public void setFileTransferButton(JButton fileTransferButton) {
        this.fileTransferButton = fileTransferButton;
    }

    public void setLoadFilesButton(JButton loadFilesButton) {
        this.loadFilesButton = loadFilesButton;
    }

    public void setPathButton(JButton pathButton) {
        this.pathButton = pathButton;
    }

    public void setSettingsButton(JButton settingsButton) {
        this.settingsButton = settingsButton;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setResetButton(JButton resetButton) {
        this.resetButton = resetButton;
    }
}
