package UiViews;

import UiComponents.Utility;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CreditsView extends AbstractView {

    public CreditsView() {

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.NORTH;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0;
        c.weighty = 0;
        c.gridy = 0;

        JLabel desc = new JLabel("Thank you for downloading app!!!");
        desc.setFont(Utility.fontSmallHelveticaBold);
        desc.setHorizontalAlignment(SwingConstants.CENTER);
        JLabel desc2 = new JLabel("Please leave your feedback on Github!");
        desc2.setFont(Utility.fontSmallHelveticaBold);
        desc2.setHorizontalAlignment(SwingConstants.CENTER);
        desc2.setBorder(new CompoundBorder(
            new MatteBorder(0,0,1,0, Color.GRAY),
            new EmptyBorder(0, 0, 10, 0)
        ));

        JLabel authorLabel = new JLabel("Author:");
        authorLabel.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, Color.gray),
                new EmptyBorder(5, 5, 5, 5)
        ));
        authorLabel.setFont(Utility.fontHelveticaBold);

        JTextField authorTextField = getTextField("~~maksik997~~");
        authorTextField.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel githubLabel = new JLabel("Github:");
        githubLabel.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, Color.gray),
                new EmptyBorder(5, 5, 5, 5)
        ));
        githubLabel.setFont(Utility.fontHelveticaBold);

        JTextField githubTextField = getTextField("https://github.com/maksik997");
        githubTextField.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel versionLabel = new JLabel("Version: 0.4.2");
        versionLabel.setBorder(new CompoundBorder(
            new MatteBorder(1, 0, 0, 0, Color.GRAY),
            new EmptyBorder(5, 0, 5, 0)
        ));
        versionLabel.setFont(Utility.fontSmallHelveticaBold);
        versionLabel.setHorizontalAlignment(SwingConstants.CENTER);

        c.gridy++;
        mainPanel.add(desc, c);
        c.gridy++;
        mainPanel.add(desc2, c);
        c.gridy++;
        mainPanel.add(authorLabel, c);
        c.gridy++;
        mainPanel.add(authorTextField, c);
        c.gridy++;

        mainPanel.add(githubLabel, c);
        c.gridy++;
        mainPanel.add(githubTextField, c);
        c.gridy++;

        mainPanel.add(versionLabel, c);
        c.gridy++;
        c.weighty = 1;

        mainPanel.add(Box.createVerticalGlue(), c);

        this.add(mainPanel, BorderLayout.CENTER);
    }

    private JTextField getTextField(String value) {
        JTextField textField = new JTextField(value);
        textField.setEditable(false);
        textField.setFont(Utility.fontHelveticaPlain);

        textField.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                textField.setFocusable(true);
            }
        });

        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                textField.selectAll();
            }
        });

        return textField;
    }
}
