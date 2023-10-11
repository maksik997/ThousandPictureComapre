package MinorViews;

import javax.swing.*;
import java.awt.*;

public class LocationView extends JPanel /*implements FileLoadingListener, PropertyChangeListener*/ {
    private final JFileChooser fileChooser;
    private final JTextArea outputLog;
    private JTextField pathTextField;
    private JButton fileTransferButton, loadFilesButton, pathButton;
//    private final List<ProcessingListener> listeners = new ArrayList<ProcessingListener>();
    private String path;

//    private int workersHandled;

    private SwingWorker<Void, Void> fileLoadingWorker, lookForDuplicatesWorker, moveFilesWorker;

    public LocationView() {
//        workersHandled = 0;
/*        this.fileLoadingWorker = fileLoadingWorker;
        this.lookForDuplicatesWorker = lookForDuplicatesWorker;
        this.moveFilesWorker = moveFilesWorker;*/
        fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        this.setLayout(new BorderLayout());

        JLabel title = new JLabel("Thousand Pictures Redundancy"),
            pathLabel = new JLabel("Path: ");

        title.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(title, BorderLayout.NORTH);

        // Central Panel
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayout(2, 1));

        // PathBox Panel
        JPanel pathBox = new JPanel();
        pathBox.setLayout(new FlowLayout());
        pathBox.add(pathLabel);

        pathTextField = new JTextField(64);
        pathTextField.setEditable(false);
        pathBox.add(pathTextField);

        pathButton = new JButton("Open a directory");
        pathBox.add(pathButton);

        // Progress Panel
        JPanel progressPanel = new JPanel();

        this.outputLog = new JTextArea();
        this.outputLog.setEditable(false);
        this.outputLog.setColumns(64);
        this.outputLog.setRows(13);
        progressPanel.add(outputLog);

        centerPanel.add(pathBox);
        centerPanel.add(progressPanel);

        this.add(centerPanel);

        // Bottom panel
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new GridLayout(1,2));

        loadFilesButton = new JButton("Load files");
        loadFilesButton.setEnabled(false);
        bottomPanel.add(loadFilesButton);

        fileTransferButton = new JButton("Move files");
        fileTransferButton.setEnabled(false);
        bottomPanel.add(fileTransferButton);

        // deprecated
        /*// Action listeners for buttons

        pathButton.addActionListener(e->{
            int file = fileChooser.showOpenDialog(LocationView.this);
            if (file == JFileChooser.APPROVE_OPTION) {
                pathTextField.setText(fileChooser.getSelectedFile().getAbsolutePath());
                this.path = pathTextField.getText();
                loadFilesButton.setEnabled(true);
            }
        });
        loadFilesButton.addActionListener(e -> {
            loadFilesButton.setEnabled(false);
            callProcessingListeners();
        });
        fileTransferButton.addActionListener(e->{
            fileTransferButton.setEnabled(false);
            outputLog.append("Checking collection of images for duplicates...\n");
            outputLog.append("It can take awhile...\n");
            lookForDuplicatesWorker.addPropertyChangeListener(this);
            lookForDuplicatesWorker.execute();
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        });*/

        this.add(bottomPanel, BorderLayout.SOUTH);
    }

    //deprecated
    /*@Override
    public void actionPerformed(EventObject e) {
        fileLoadingWorker.addPropertyChangeListener(this);
        fileLoadingWorker.execute();
        outputLog.append("Loading images... \n");
        outputLog.append("It can take awhile...\n");
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    }*/

    //deprecated
    /*@Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (fileLoadingWorker.isDone() && workersHandled == 0) {
            outputLog.append("Completed loading images. \n");
            workersHandled++;
            setCursor(Cursor.getDefaultCursor());
            fileTransferButton.setEnabled(true);
        }
        if (lookForDuplicatesWorker.isDone() && workersHandled == 1){
            outputLog.append("Completed checking for duplicates. \n");
            outputLog.append("Files transfer started. \n");
            outputLog.append("It will probably take a brief moment... \n");

            workersHandled++;
            // now third worker will start working to move files
            if(moveFilesWorker.getState() == SwingWorker.StateValue.PENDING) {
                moveFilesWorker.addPropertyChangeListener(this);
                moveFilesWorker.execute();
            }
        }
        if (moveFilesWorker.isDone() && workersHandled == 2){
            outputLog.append("Completed moving files. \n");
            setCursor(Cursor.getDefaultCursor());
            workersHandled++;
        }
    }*/


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

    public void setPath(String path) {
        this.path = path;
    }

    //deprecated
    /*public void setWorkersHandled(int workersHandled) {
        this.workersHandled = workersHandled;
    }*/

    //deprecated
    /*// tmp, next six
    public SwingWorker<Void, Void> getFileLoadingWorker() {
        return fileLoadingWorker;
    }

    public SwingWorker<Void, Void> getLookForDuplicatesWorker() {
        return lookForDuplicatesWorker;
    }

    public SwingWorker<Void, Void> getMoveFilesWorker() {
        return moveFilesWorker;
    }

    public void setFileLoadingWorker(SwingWorker<Void, Void> fileLoadingWorker) {
        this.fileLoadingWorker = fileLoadingWorker;
    }

    public void setLookForDuplicatesWorker(SwingWorker<Void, Void> lookForDuplicatesWorker) {
        this.lookForDuplicatesWorker = lookForDuplicatesWorker;
    }

    public void setMoveFilesWorker(SwingWorker<Void, Void> moveFilesWorker) {
        this.moveFilesWorker = moveFilesWorker;
    }*/
}
