package UiViews;

import UiComponents.*;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class ComparerView extends AbstractView {

    private final UiPath uiPath;

    private final UiOutput uiOutput;

    private final UiTray uiTray;

    private final JButton resetButton, loadButton, moveButton;

    private final JLabel stateLabel;
    
    public ComparerView() {
        super();

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        this.uiPath = new UiPath();
        mainPanel.add(uiPath, BorderLayout.PAGE_START);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());

        this.uiTray = new UiTray();
        contentPanel.add(uiTray, BorderLayout.NORTH);

        this.uiOutput = new UiOutput();
        contentPanel.add(uiOutput);

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel,BoxLayout.Y_AXIS));
        rightPanel.setBorder(new MatteBorder(0, 1, 0, 0, Color.GRAY));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 0, 5, 0);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weighty = 0;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Insets insets = new Insets(5, 15, 5, 15);
        this.loadButton = Utility.buttonFactory("LOC_COMPARER_VIEW_LOAD_BUTTON", insets);
        this.moveButton = Utility.buttonFactory("LOC_COMPARER_VIEW_MOVE_BUTTON", insets);
        this.resetButton = Utility.buttonFactory("LOC_COMPARER_VIEW_RESET_BUTTON", insets);

        buttonPanel.add(loadButton, gbc);
        gbc.gridy++;
        buttonPanel.add(moveButton, gbc);
        gbc.gridy++;
        buttonPanel.add(resetButton, gbc);
        gbc.gridy++;
        gbc.weighty = 1;
        buttonPanel.add(Box.createVerticalGlue(), gbc);

        rightPanel.add(buttonPanel);

        JPanel statePanel = new JPanel();
        statePanel.setLayout(new GridLayout());
        statePanel.setBorder(new TitledBorder(
            new MatteBorder(1, 0, 0, 0, Color.GRAY),
            "LOC_COMPARER_VIEW_STATE_BORDER_TITLE"
        ));

        this.stateLabel = new JLabel("LOC_COMPARER_VIEW_STATE_READY");
        this.stateLabel.setFont(Utility.fontBigHelveticaBold);
        this.stateLabel.setHorizontalAlignment(SwingConstants.CENTER);
        this.stateLabel.setVerticalAlignment(SwingConstants.CENTER);

        statePanel.add(stateLabel);

        rightPanel.add(statePanel);

        mainPanel.add(rightPanel, BorderLayout.LINE_END);

        mainPanel.add(contentPanel);

        this.add(mainPanel);
    }

    public void clear() {
        uiPath.clearPath();
        uiTray.clear();
    }

    // Getters
    public UiPath getUiPath() {
        return uiPath;
    }

    public UiOutput getUiOutput() {
        return uiOutput;
    }

    public UiTray getUiTray() {
        return uiTray;
    }

    public JButton getResetButton() {
        return resetButton;
    }

    public JButton getLoadButton() {
        return loadButton;
    }

    public JButton getMoveButton() {
        return moveButton;
    }

    public JLabel getStateLabel() {
        return stateLabel;
    }
}
