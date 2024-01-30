package MinorViews;

import UiComponents.UiFooter;
import UiComponents.UiHeader;
import UiComponents.UiPath;
import UiComponents.Utility;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.image.BufferedImage;

public class LocationView extends JPanel {

    private JTextArea outputLog;
    // update v0.3
    private final UiPath uiPath;

    private final UiHeader uiHeader;

    private final UiFooter uiFooter;
    
    public LocationView() {
        this.setLayout(new BorderLayout());

        // update v0.3
        this.uiPath = new UiPath();
        this.uiHeader = new UiHeader();
        this.uiFooter = new UiFooter();

        _init();
    }

    private void _init(){
        this.add(uiHeader, BorderLayout.NORTH); // update v0.3
        this.add(_initMain());
        this.add(uiFooter, BorderLayout.SOUTH); // update v0.3
    }

    private JPanel _initMain(){
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        mainPanel.add(uiPath, BorderLayout.PAGE_START);

        outputLog = new JTextArea();
        outputLog.setEditable(false);
        outputLog.setColumns(64);
        outputLog.setRows(13);

        JScrollPane outputPanel = new JScrollPane(outputLog);

        mainPanel.add(outputPanel);

        return mainPanel;
    }

    public void _reset(){
        uiFooter.clear();
        uiPath.clear();
    }

    public JTextArea getOutputLog() {
        return outputLog;
    }

    // update v0.3
    public UiPath getUiPath() {
        return uiPath;
    }

    public UiHeader getUiHeader() {
        return uiHeader;
    }

    public UiFooter getUiFooter() {
        return uiFooter;
    }
}
