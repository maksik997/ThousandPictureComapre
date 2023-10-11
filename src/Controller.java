import DataActions.ImageRecord;
import MinorViews.LocationView;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class Controller {

    private final View view;
    private final Model model;

    private SwingWorker<Void, Void> loadFilesWorker, fileTransferWorker;

    public Controller(View view, Model model) {
        this.view = view;
        this.model = model;

        // Initialize view controllable elements
        initView();

        // Initialize workers
        workersFactory();
    }

    public void initView(){
        // This method initialize every interactive element of view :P

        LocationView lView = view.getLocationView();

        //deprecated
        /*// next three are tmp
        lView.setFileLoadingWorker(model.getProcessing().getFileLoadingWorker());
        lView.setLookForDuplicatesWorker(model.getProcessing().getLookForDuplicatesWorker());
        lView.setMoveFilesWorker(model.getProcessing().getMoveFilesWorker());*/

        // path button action listener
        lView.getPathButton().addActionListener(e->{
            int file = lView.getFileChooser().showOpenDialog(lView);
            if (file == JFileChooser.APPROVE_OPTION){
                lView.getPathTextField().setText(
                    lView.getFileChooser().getSelectedFile().getAbsolutePath()
                );
                lView.setPath(lView.getPathTextField().getText());
                lView.getLoadFilesButton().setEnabled(true);
            }
        });

        // load files button action listener

        lView.getLoadFilesButton().addActionListener(e->{
            lView.getLoadFilesButton().setEnabled(false);
            model.getProcessing().setDir(new File(lView.getPath()));

            //lView.getFileLoadingWorker().addPropertyChangeListener(lView);
            //lView.getFileLoadingWorker().execute();
            loadFilesWorker.execute();

            lView.getOutputLog().append("Loading images... \n");
            lView.getOutputLog().append("It can take awhile...\n");

            view.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        });

        // check for duplicates button action listener

        lView.getFileTransferButton().addActionListener(e->{
            lView.getFileTransferButton().setEnabled(false);

            //lView.getLookForDuplicatesWorker().addPropertyChangeListener(lView);
            //lView.getLookForDuplicatesWorker().execute();
            fileTransferWorker.execute();

            lView.getOutputLog().append("Files transfer started. \n");
            lView.getOutputLog().append("It will probably take a brief moment... \n");

            view.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        });
    }

    public void workersFactory(){
        // This method will create workers on-demand, which is handy in case of program restart without restart

        Processing processing = model.getProcessing();
        LocationView lView = view.getLocationView();

        loadFilesWorker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                try {
                    processing.setAllImages(
                        ImageRecord.getAllImages(processing.getDir())
                    );
                } catch (IOException e) {
                    lView.getOutputLog().append("\n\n[E]Problem just occurred...\n");
                    lView.getOutputLog().append("[E]Application couldn't load files.\n");
                    lView.getOutputLog().append("Please restart app and try again.\n");
                    throw new RuntimeException(e);
                }
                lView.getOutputLog().append("Completed loading images. \n");
                lView.getOutputLog().append("Found "+ processing.getAllImages().size() +" images. \n\n");
                lView.getOutputLog().append("Checking collection of images for duplicates...\n");
                lView.getOutputLog().append("It can take awhile...\n");

                processing.setDuplicates(processing.compareAllImages());

                lView.getOutputLog().append("Completed checking for duplicates. \n");
                lView.getOutputLog().append("Found " + processing.getDuplicates().size()+ " redundant images. \n\n");

                view.setCursor(Cursor.getDefaultCursor());
                lView.getFileTransferButton().setEnabled(true);

                return null;
            }
        };

        fileTransferWorker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                processing.fileTransfer();

                lView.getOutputLog().append("Completed moving files. \n");

                view.setCursor(Cursor.getDefaultCursor());
                return null;
            }
        };
    }

}
