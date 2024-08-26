package pl.magzik.ui.views;

import pl.magzik.ui.components.OutputPanel;
import pl.magzik.ui.components.PathPanel;
import pl.magzik.ui.components.TrayPanel;
import pl.magzik.ui.components.Utility;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class ComparerView extends AbstractView {

    // TODO START HERE NEXT
    // TODO UPDATE, CLEAN THIS...

    private final PathPanel pathPanel;

    private final OutputPanel outputPanel;

    private final TrayPanel trayPanel;

    private final JButton resetButton, loadButton, moveButton;

    private final JLabel stateLabel;
    
    public ComparerView() {
        super();

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        this.pathPanel = new PathPanel();
        mainPanel.add(pathPanel, BorderLayout.PAGE_START);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());

        this.trayPanel = new TrayPanel();
        contentPanel.add(trayPanel, BorderLayout.NORTH);

        this.outputPanel = new OutputPanel();
        contentPanel.add(outputPanel);

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
        this.loadButton = Utility.buttonFactory("view.comparer.button.load", insets);
        this.moveButton = Utility.buttonFactory("view.comparer.button.move", insets);
        this.resetButton = Utility.buttonFactory("view.comparer.button.reset", insets);

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
                "view.comparer.state.border.title"
        ));

        this.stateLabel = new JLabel("comparer.state.ready");
        this.stateLabel.setFont(Utility.fontBigHelveticaBold);
        this.stateLabel.setHorizontalAlignment(SwingConstants.CENTER);
        this.stateLabel.setVerticalAlignment(SwingConstants.CENTER);

        statePanel.add(stateLabel);

        rightPanel.add(statePanel);

        mainPanel.add(rightPanel, BorderLayout.LINE_END);

        mainPanel.add(contentPanel);

        this.add(mainPanel);

        resetButton.setEnabled(false);
        moveButton.setEnabled(false);
    }

    public void clear() {
        pathPanel.clearPath();
        trayPanel.clear();
    }

    // Getters
    public PathPanel getUiPath() {
        return pathPanel;
    }

    public OutputPanel getUiOutput() {
        return outputPanel;
    }

    public TrayPanel getUiTray() {
        return trayPanel;
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

    /**
     * Disables all buttons associated with destructive actions in the user interface.
     * This includes:
     * <ul>
     *     <li>The path button in the path panel</li>
     *     <li>The load button</li>
     *     <li>The move button</li>
     *     <li>The reset button</li>
     * </ul>
     * This method is typically used to prevent user interactions with these buttons
     * during critical operations where such actions could interfere with the process.
     */
    public void blockDestructiveButtons() {
        pathPanel.getPathButton().setEnabled(false);
        loadButton.setEnabled(false);
        moveButton.setEnabled(false);
        resetButton.setEnabled(false);
    }
}
