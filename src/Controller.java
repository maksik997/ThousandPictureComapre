import MinorViews.LocationView;
import MinorViews.SettingsView;

import javax.swing.*;
import java.awt.*;
import java.io.File;
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
        SettingsView sView = view.getSettingsView();

        // change view to settings
        lView.getUiHeader().getSettingsButton().addActionListener(e->{
            view.remove(lView);
            view.add(sView);
            view.repaint();
            view.revalidate();
        });

        // go back to the main view
        sView.getBackButton().addActionListener(e->{
            view.remove(sView);
            view.add(lView);
            view.repaint();
            view.revalidate();
        });

        // destDir button action listener
        sView.getDestDirButton().addActionListener(e->{
            int file = sView.getFileChooser().showOpenDialog(sView);
            if (file == JFileChooser.APPROVE_OPTION){
                sView.getDestDirTextField().setText(
                    sView.getFileChooser().getSelectedFile().getAbsolutePath()
                );
                model.getComparerLayer().setDestDir(
                    new File(sView.getDestDirTextField().getText())
                );
            }
        });

        // path button action listener
        lView.getUiPath().getPathButton().addActionListener(e->{ // updated
            if(lView.getUiPath().openFileChooser()) {
                lView.getUiFooter().getLoadFilesButton().setEnabled(true);
            }

/*//            int file = lView.getUiPath().showOpenDialog(lView);
//            if (file == JFileChooser.APPROVE_OPTION){
//                lView.getPathTextField().setText(
//                    lView.getFileChooser().getSelectedFile().getAbsolutePath()
//                );
//                lView.setPath(lView.getPathTextField().getText());
//                lView.getLoadFilesButton().setEnabled(true);
//            }*/
        });


        // reset button action listener
        lView.getUiFooter().getResetButton().addActionListener(e->{
            workersFactory();

            lView.getOutputLog().append("\n ===RESET=== \n");
            lView._reset();
            model.getComparerLayer()._reset();
        });

        // load files button action listener
        lView.getUiFooter().getLoadFilesButton().addActionListener(e->{
            lView.getUiFooter().getLoadFilesButton().setEnabled(false);
            lView.getUiFooter().getResetButton().setEnabled(false);
            model.getComparerLayer().setSourceDir(new File(lView.getUiPath().getPath()));

            loadFilesWorker.execute();

            lView.getOutputLog().append("Loading images... \n");
            lView.getOutputLog().append("It can take awhile...\n");

            view.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

//            lView.getResetButton().setEnabled(true);
        });

        // check for duplicate button action listener
        lView.getUiFooter().getFileTransferButton().addActionListener(e->{
            lView.getUiFooter().getFileTransferButton().setEnabled(false);
            lView.getUiFooter().getResetButton().setEnabled(false);

            fileTransferWorker.execute();

            lView.getOutputLog().append("Files transfer started. \n");
            lView.getOutputLog().append("It will probably take a brief moment... \n");

            view.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        });
    }

    public void workersFactory(){
        // This method will create workers on-demand, which is handy in case of program restart without restart

        ComparerLayer compareLayer = model.getComparerLayer();
        LocationView lView = view.getLocationView();


        loadFilesWorker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                lView.getOutputLog().append("Preparing picture comparer.\n");
                compareLayer.setUp();
                lView.getOutputLog().append("Mapping files... This will take a while...\n");

                // todo It should call loading indicator in future
                compareLayer.compareAndExtract();


                lView.getOutputLog().append("Completed checking for duplicates. \n");
//                lView.getOutputLog().append("Found " + compareLayer.getPc().getTotalObjectCount() + " images. \n\n");
                lView.getOutputLog().append(String.format(
                    "Found %d images%n", compareLayer.getPc().getTotalObjectCount()
                ));

                view.setCursor(Cursor.getDefaultCursor());
                lView.getUiFooter().getFileTransferButton().setEnabled(true);
                lView.getUiFooter().getResetButton().setEnabled(true);

                return null;
            }
        };

        fileTransferWorker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                compareLayer.fileTransfer();

                lView.getOutputLog().append("Completed moving files. \n");

                view.setCursor(Cursor.getDefaultCursor());

                lView.getUiFooter().getResetButton().setEnabled(true);
                return null;
            }
        };
    }
}
