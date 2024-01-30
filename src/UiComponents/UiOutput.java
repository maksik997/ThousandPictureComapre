package UiComponents;

import javax.swing.*;
import java.awt.*;

public class UiOutput extends JPanel {

    private final JTextArea outputArea_;

    public UiOutput() {
        this.setLayout(new GridLayout());

        this.outputArea_ = new JTextArea();

        this.outputArea_.setEditable(false);
        this.outputArea_.setFocusable(false);
        this.outputArea_.setFont(Utility.fontHelveticaPlain);

        JScrollPane outputPanel = new JScrollPane(outputArea_);
        this.add(outputPanel);
    }

    public void write(String msg) {
        outputArea_.append(msg);
    }

    public void writeLine(String msg) {
        write(String.format("%s%n", msg));
    }

    public void clear() {
        outputArea_.setText("");
    }
}
