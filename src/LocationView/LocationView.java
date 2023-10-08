package LocationView;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

public class LocationView extends JPanel implements TaskListener, PropertyChangeListener {
    private final JFileChooser fileChooser;
    private final JProgressBar progressBar;
    private final JTextArea outputLog;
    private List<ProcessingListener> listeners = new ArrayList<ProcessingListener>();
    private String path;

    private final SwingWorker<Boolean, Void> processAllData;

    public LocationView(SwingWorker<Boolean, Void> processAllData) {
        this.processAllData = processAllData;
        fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        this.progressBar = new JProgressBar(0, 100);
        this.progressBar.setString("0%");
        this.progressBar.setStringPainted(true);

        this.outputLog = new JTextArea();
        this.outputLog.setEditable(false);
        this.outputLog.setColumns(64);
        this.outputLog.setRows(13);

        this.setLayout(new BorderLayout());

        JLabel title = new JLabel("Thousand Pictures Redundancy"),
            pathLabel = new JLabel("Path: ");

        this.add(title, BorderLayout.NORTH);

        JPanel pathBox = new JPanel();
        pathBox.setLayout(new FlowLayout());
        pathBox.add(pathLabel);

        JTextField path = new JTextField(64);
        path.setEditable(false);

        pathBox.add(path);

        JButton pathButton = new JButton("Open a directory");

        pathButton.addActionListener(e->{
            int file = fileChooser.showOpenDialog(LocationView.this);
            if (file == JFileChooser.APPROVE_OPTION) {
                path.setText(fileChooser.getSelectedFile().getAbsolutePath());
                this.path = path.getText();
            }
        });

        pathBox.add(pathButton);

//        this.add(pathBox);

        JPanel progressPanel = new JPanel();

        progressPanel.setLayout(new FlowLayout());

        progressPanel.add(progressBar);
        progressPanel.add(outputLog);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayout(2, 1));

        centerPanel.add(pathBox);

        centerPanel.add(progressPanel);

        this.add(centerPanel);

        JButton startButton = new JButton("Extract duplicates");

        startButton.addActionListener(e -> {
            startButton.setEnabled(false);
            callProcessingListeners();
        });

        this.add(startButton, BorderLayout.SOUTH);
    }

    public void addProcessingListener(ProcessingListener l){
        listeners.add(l);
    }

    public void removeProcessingListener(ProcessingListener l){
        listeners.remove(l);
    }

    private void callProcessingListeners(){
        for (ProcessingListener l : listeners) {
            l.actionPerformed(new ProcessingEvent(this, path));
        }
    }

    @Override
    public void actionPerformed(EventObject e) {
        processAllData.addPropertyChangeListener(this);
        processAllData.execute();
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        int progress = processAllData.getProgress();
        progressBar.setValue(progress);
        progressBar.setString(progress + "%");
        outputLog.append("Completed in " + progress + "% \n");
        if (progress >= 100)
            setCursor(Cursor.getDefaultCursor());
    }
}
