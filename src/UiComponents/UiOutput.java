package UiComponents;

import javax.swing.*;
import java.awt.*;

public class UiOutput extends JPanel {

//    private final JTextArea outputArea_;
    private final JList<String> mappedObjectList, duplicateList;

    public UiOutput() {
        this.setLayout(new GridLayout());

//        this.outputArea_ = new JTextArea();

//        this.outputArea_.setEditable(false);
//        this.outputArea_.setFocusable(false);
//        this.outputArea_.setFont(Utility.fontHelveticaPlain);

//        JScrollPane outputPanel = new JScrollPane(outputArea_);
//        this.add(outputPanel);

        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);

        JPanel mappedObjectPanel = new JPanel(new BorderLayout());

        this.mappedObjectList = new JList<>();
        this.mappedObjectList.setFocusable(false);

        mappedObjectPanel.add(new JScrollPane(mappedObjectList));

        tabbedPane.addTab("Loaded originals", mappedObjectPanel);

        JPanel duplicatePanel = new JPanel(new BorderLayout());

        this.duplicateList = new JList<>();
        this.duplicateList.setFocusable(false);

        duplicatePanel.add(new JScrollPane(duplicateList));

        tabbedPane.addTab("Duplicates found", duplicatePanel);

        this.add(tabbedPane);
    }

    public JList<String> getMappedObjectList() {
        return mappedObjectList;
    }

    public JList<String> getDuplicateList() {
        return duplicateList;
    }

//        public void write(String msg) {
//        outputArea_.append(msg);
//    }

//    public void writeLine(String msg) {
//        write(String.format("%s%n", msg));
//    }

//    public void clear() {
//        outputArea_.setText("");
//    }
}
