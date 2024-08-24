package pl.magzik.UiViews;

import pl.magzik.UiComponents.Utility;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class CreditsView extends AbstractView {

    public CreditsView() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.weighty = 0;
        c.gridy = 0;

        JLabel desc = new JLabel("view.credits.desc.1");
        desc.setFont(Utility.fontHelveticaBold);
        desc.setHorizontalAlignment(SwingConstants.CENTER);
        desc.setBorder(new EmptyBorder(10, 0, 0, 0));

        JLabel desc2 = new JLabel("view.credits.desc.2");
        desc2.setFont(Utility.fontHelveticaPlain);
        desc2.setHorizontalAlignment(SwingConstants.CENTER);
        desc2.setBorder(new CompoundBorder(
            new MatteBorder(0,0,1,0, Color.GRAY),
            new EmptyBorder(0, 0, 10, 0)
        ));

        JLabel authorLabel = new JLabel("view.credits.label.author");
        authorLabel.setBorder(new CompoundBorder(
            new MatteBorder(1, 0, 0, 0, Color.gray),
            new EmptyBorder(10, 0, 0, 0)
        ));
        authorLabel.setFont(Utility.fontHelveticaBold);
        authorLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel authorText = new JLabel("view.credits.author");
        authorText.setBorder(new CompoundBorder(
            new MatteBorder(0, 0, 1, 0, Color.gray),
            new EmptyBorder(0, 0, 10, 0)
        ));
        authorText.setFont(Utility.fontHelveticaPlain);
        authorText.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel githubLabel = new JLabel("view.credits.label.github");
        githubLabel.setBorder(new CompoundBorder(
            new MatteBorder(1, 0, 0, 0, Color.gray),
            new EmptyBorder(10, 0, 0, 0)
        ));
        githubLabel.setFont(Utility.fontHelveticaBold);
        githubLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel githubHyperlink = new JLabel("view.credits.github.link");
        githubHyperlink.setFont(Utility.fontHelveticaPlain);
        githubHyperlink.setBorder(new CompoundBorder(
            new MatteBorder(0, 0, 1, 0, Color.gray),
            new EmptyBorder(0, 0, 10, 0)
        ));
        githubHyperlink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        githubHyperlink.setHorizontalAlignment(SwingConstants.CENTER);
        githubHyperlink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI("https://github.com/maksik997"));
                } catch (IOException | URISyntaxException ex) {
                    throw new RuntimeException(ex); // todo for now
                }
            }
        });

        JLabel versionLabel = new JLabel("general.version");
        versionLabel.setBorder(new CompoundBorder(
            new MatteBorder(1, 0, 0, 0, Color.GRAY),
            new EmptyBorder(5, 0, 5, 0)
        ));
        versionLabel.setFont(Utility.fontSmallHelveticaBold);
        versionLabel.setHorizontalAlignment(SwingConstants.CENTER);

        mainPanel.add(desc, c);
        c.gridy++;
        mainPanel.add(desc2, c);
        c.gridy++;
        c.weighty = 1;
        mainPanel.add(Box.createVerticalBox(), c);
        c.gridy++;
        c.weighty = 0;
        mainPanel.add(authorLabel, c);
        c.gridy++;
        mainPanel.add(authorText, c);
        c.gridy++;
        mainPanel.add(githubLabel, c);
        c.gridy++;
        mainPanel.add(githubHyperlink, c);
        c.gridy++;
        c.weighty = 1;
        mainPanel.add(Box.createVerticalBox(), c);
        c.gridy++;
        c.weighty = 0;
        mainPanel.add(versionLabel, c);
        c.gridy++;
        mainPanel.add(Box.createVerticalGlue(), c);

        this.add(mainPanel, BorderLayout.CENTER);
    }
}
