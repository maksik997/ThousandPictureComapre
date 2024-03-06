import Modules.ComparerModule;
import UiViews.LocationView;
import UiViews.SettingsView;
import UiComponents.Utility;
import pl.magzik.Comparer;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

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

    private void initView(){
        // Initialize header listeners for each view
        view.getScenes().forEach( p -> {
            p.getUiHeader_().getComparerButton().addActionListener(e ->
                    view.toggleScene(Utility.Scene.COMPARER)
            );
            p.getUiHeader_().getSettingsButton().addActionListener(e ->
                    view.toggleScene(Utility.Scene.SETTINGS)
            );
            p.getUiHeader_().getGalleryButton().addActionListener(e ->
                    view.toggleScene(Utility.Scene.GALLERY)
            );
        });

        // This method initializes every interactive element of view
        SettingsView sView = view.getSettingsView();

        // destination button action listener
        sView.getPathButton().addActionListener(e->{
            if(sView.openFileChooser()) {
                model.getComparerModule().setDestDir(
                    new File(sView.getPath())
                );
            }
        });

        // Changing mode
        sView.getModeSelector().addActionListener(
            e -> model.getComparerModule().getPc()
                .setMode((Comparer.Modes) sView.getModeSelector().getSelectedItem())
        );

        LocationView lView = view.getLocationView();

        // path button action listener
        lView.getButton(Utility.Buttons.OPEN_SOURCE).addActionListener(e->{ // updated
            if(lView.openFileChooser()) {
                lView.getButton(Utility.Buttons.LOAD_FILES).setEnabled(true);
            }
        });

        // reset button action listener
        lView.getButton(Utility.Buttons.RESET).addActionListener(e->{
            lView.writeLine("Resetting...");

            workersFactory();
            lView.clear();
            model.getComparerModule()._reset();

            lView.writeLine("Reset done.\n");
        });

        // load files button action listener
        lView.getButton(Utility.Buttons.LOAD_FILES).addActionListener(e->{
            lView.writeLine("Loading images. It can take awhile...");

            lView.getButton(Utility.Buttons.OPEN_SOURCE).setEnabled(false);
            lView.getButton(Utility.Buttons.LOAD_FILES).setEnabled(false);
            lView.getButton(Utility.Buttons.RESET).setEnabled(false);
            sView.getModeSelector().setEnabled(false);
            model.getComparerModule().setSourceDir(new File(lView.getPath()));

            loadFilesWorker.execute();

            view.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        });

        // move files button action listener
        lView.getButton(Utility.Buttons.MOVE_FILES).addActionListener(e->{
            lView.writeLine("File transfer started. It can take awhile...");

            lView.getButton(Utility.Buttons.OPEN_SOURCE).setEnabled(false);
            lView.getButton(Utility.Buttons.MOVE_FILES).setEnabled(false);
            lView.getButton(Utility.Buttons.RESET).setEnabled(false);
            sView.getModeSelector().setEnabled(false);

            fileTransferWorker.execute();

            view.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        });
    }

    private void workersFactory(){
        // This method will create workers on-demand, which is handy in case of program restart without restart

        ComparerModule compareLayer = model.getComparerModule();
        LocationView lView = view.getLocationView();
        SettingsView sView = view.getSettingsView();

        loadFilesWorker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                lView.writeLine("Setting up picture comparer.");
                try {
                    compareLayer.setUp();
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(view, String.format("Error have occurred:%n%s", e.getMessage()));

                    return null;
                }
                lView.writeLine("Picture comparer ready.\nMapping files. It can take awhile...");

                lView.updateTray(
                    compareLayer.getPc().getTotalObjectCount(),
                    compareLayer.getPc().getProcessedObjectCount(),
                    compareLayer.getPc().getDuplicatesObjectCount()
                );

                // v2
                // Tray updating
                executeProcessedObjects();

                compareLayer.compareAndExtract();

                lView.writeLine("Completed checking for duplicates.");
                lView.setDuplicatesFieldValue(compareLayer.getPc().getDuplicatesObjectCount());

                view.setCursor(Cursor.getDefaultCursor());

                if(compareLayer.getPc().getDuplicatesObjectCount() > 0)
                    lView.getButton(Utility.Buttons.MOVE_FILES).setEnabled(true);

                lView.getButton(Utility.Buttons.OPEN_SOURCE).setEnabled(true);
                lView.getButton(Utility.Buttons.RESET).setEnabled(true);
                sView.getModeSelector().setEnabled(true);


                return null;
            }
        };

        fileTransferWorker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                lView.writeLine(
                    String.format("Moving duplicates from %s to %s",
                        compareLayer.getSourceDir(), compareLayer.getDestDir()
                    )
                );

                compareLayer.fileTransfer();
                lView.writeLine("Completed moving files.");

                view.setCursor(Cursor.getDefaultCursor());

                lView.getButton(Utility.Buttons.OPEN_SOURCE).setEnabled(true);
                lView.getButton(Utility.Buttons.RESET).setEnabled(true);
                sView.getModeSelector().setEnabled(true);

                return null;
            }
        };
    }

    private void executeProcessedObjects() {
        // This method will create a new SwingWorker that will observe for any change in ProcessedObjectCount property,
        // and will update it when needed.
        ComparerModule compareLayer = model.getComparerModule();
        LocationView lView = view.getLocationView();

        SwingWorker<Void,Long> task = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                long lastProcessedCount = compareLayer.getPc().getProcessedObjectCount();
                while (!loadFilesWorker.isDone()) {
                    long thisProcessedCount = compareLayer.getPc().getProcessedObjectCount();
                    if (lastProcessedCount < thisProcessedCount){
                        lastProcessedCount = thisProcessedCount;

                        publish(thisProcessedCount);
                    }
                }
                return null;
            }

            @Override
            protected void process(List<Long> chunks) {
                super.process(chunks);
                lView.getUiMainPanel_().getUiTray_().getProcessedField_().setText(
                        String.valueOf(compareLayer.getPc().getProcessedObjectCount())
                );
            }
        };

        task.execute();
    }
}
