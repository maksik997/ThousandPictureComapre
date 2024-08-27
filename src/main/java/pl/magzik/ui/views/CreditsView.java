package pl.magzik.ui.views;

import pl.magzik.ui.components.Utility;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * The {@code CreditsView} class represents a UI component that displays credits information within the application.
 * This view includes descriptions, author information, and a hyperlink to the author's GitHub profile.
 * The layout is managed using a {@link GridBagLayout} to ensure flexible placement of labels and other components.
 */
public class CreditsView extends AbstractView {

    /**
     * Constructs a new {@code CreditsView} and initializes the UI components.
     */
    public CreditsView() {
        initialize();
    }

    /**
     * Initializes the components and layout for the credits view.
     * This method sets up the main panel with various labels and a hyperlink, using a {@link GridBagLayout} for positioning.
     */
    private void initialize() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.weighty = 0;
        c.gridy = 0;

        addLabels(
            mainPanel,
            c,
            createLabel(
                "view.credits.desc.1",
                Utility.fontHelveticaBold,
                new EmptyBorder(10, 0, 0, 0)
            ),
            createLabel(
                "view.credits.desc.2",
                Utility.fontHelveticaPlain,
                new CompoundBorder(
                        new MatteBorder(0,0,1,0, Color.GRAY),
                        new EmptyBorder(0, 0, 10, 0)
                )
            ),
            null,
            createLabel(
                "view.credits.label.author",
                Utility.fontHelveticaBold,
                new CompoundBorder(
                        new MatteBorder(1, 0, 0, 0, Color.gray),
                        new EmptyBorder(10, 0, 0, 0)
                )
            ),
            createLabel(
                "view.credits.author",
                Utility.fontHelveticaPlain,
                new CompoundBorder(
                        new MatteBorder(0, 0, 1, 0, Color.gray),
                        new EmptyBorder(0, 0, 10, 0)
                )
            ),
            createLabel(
                "view.credits.label.github",
                Utility.fontHelveticaBold,
                new CompoundBorder(
                        new MatteBorder(1, 0, 0, 0, Color.gray),
                        new EmptyBorder(10, 0, 0, 0)
                )
            ),
            createHyperlink(
                "view.credits.github.link",
                Utility.fontHelveticaPlain,
                new CompoundBorder(
                        new MatteBorder(0, 0, 1, 0, Color.gray),
                        new EmptyBorder(0, 0, 10, 0)
                ),
                "https://github.com/maksik997"
            ),
            null,
            createLabel(
                "general.version",
                Utility.fontSmallHelveticaBold,
                new CompoundBorder(
                        new MatteBorder(1, 0, 0, 0, Color.GRAY),
                        new EmptyBorder(5, 0, 5, 0)
                )
            )
        );

        add(mainPanel, BorderLayout.CENTER);
    }

    /**
     * Adds a series of labels to the specified panel using the provided {@link GridBagConstraints}.
     * Labels can be {@code null} to create vertical spacing between sections.
     *
     * @param mainPanel The panel to which the labels will be added.
     * @param c         The {@link GridBagConstraints} used to lay out the labels.
     * @param labels    The labels to be added to the panel.
     */
    private void addLabels(JPanel mainPanel, GridBagConstraints c, JLabel... labels) {
        for (JLabel label : labels) {
            if (label == null) {
                c.gridy++;
                c.weighty = 1;
                mainPanel.add(Box.createVerticalBox(), c);
                c.weighty = 0;
                continue;
            }
            c.gridy++;
            mainPanel.add(label, c);
        }

        c.gridy++;
        mainPanel.add(Box.createVerticalGlue(), c);
    }

    /**
     * Creates a {@link JLabel} with the specified text, font, and border.
     *
     * @param text   The text to display in the label.
     * @param font   The font to use for the label's text.
     * @param border The border to apply to the label.
     * @return A configured {@link JLabel}.
     */
    private JLabel createLabel(String text, Font font, Border border) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setBorder(border);
        label.setHorizontalAlignment(SwingConstants.CENTER);

        return label;
    }

    /**
     * Creates a {@link JLabel} that acts as a hyperlink. When clicked, it opens the specified URL in the default web browser.
     *
     * @param text   The text to display in the label.
     * @param font   The font to use for the label's text.
     * @param border The border to apply to the label.
     * @param link   The URL to open when the label is clicked.
     * @return A configured {@link JLabel} that acts as a hyperlink.
     */
    private JLabel createHyperlink(String text, Font font, Border border, String link) {
        JLabel label = createLabel(text, font, border);
        label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI(link));
                } catch (IOException | URISyntaxException ignored) { }
            }
        });

        return label;
    }
}
