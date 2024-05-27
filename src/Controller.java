import Exceptions.InvalidTypeException;
import Modules.ComparerModule;
import Modules.GalleryModule;
import Modules.SettingsModule;
import UiComponents.Utility;
import UiViews.*;
import pl.magzik.Comparer;
import pl.magzik.Structures.ImageRecord;
import pl.magzik.Structures.Record;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Controller {

    private final View view;
    private final Model model;
//    private SwingWorker<Void, Void> loadFilesWorker, fileTransferWorker;

    public Controller(View view, Model model) {
        this.view = view;
        this.model = model;

        // Initialize view controllable elements
        initView();
        initMenuView();
        initComparerView();
        initSettingsView();
        initGalleryView();

        // Initialize workers
//        workersFactory();
    }

    private void initView(){
        // Initialize header listeners for each view
        view.getScenes().forEach( p -> {
            if (p instanceof AbstractView) {
                ((AbstractView) p)
                    .getUiHeader_()
                    .getBackButton()
                    .addActionListener(e -> view.toggleScene(Utility.Scene.MENU));
            }

            /*((AbstractView)p).getUiHeader_().getComparerButton().addActionListener(e ->
                    view.toggleScene(Utility.Scene.COMPARER)
            );
            ((AbstractView)p).getUiHeader_().getSettingsButton().addActionListener(e ->
                    view.toggleScene(Utility.Scene.SETTINGS)
            );
            ((AbstractView)p).getUiHeader_().getGalleryButton().addActionListener(e ->
                    view.toggleScene(Utility.Scene.GALLERY)
            );*/
        });

        // Initialize gallery view
        /*GalleryView gView = view.getGalleryView();

        gView.updateTableModel(model.getGalleryModule().getSerializedData());

        // add image button
        gView.getUiGalleryButtonPanel().getAddImageButton().addActionListener(a -> {
            File[] objs = gView.openFileChooser();
            if(objs != null) {
                try {
                    model.getGalleryModule().addImages(Arrays.asList(objs));
                } catch (IOException e) {
                    throw new RuntimeException(e); // todo ...
                }

                gView.updateTableModel(model.getGalleryModule().getSerializedData());
            }
        });

        // delete image button
        gView.getUiGalleryButtonPanel().getDeleteImageButton().addActionListener(a -> {
            List<File> files = new ArrayList<>();

            Arrays.stream(gView.getFilesTable().getSelectedRows()).forEach(i -> {
                files.add((File) gView.getFilesTable().getValueAt(i, 0));
            });

            try {
                model.getGalleryModule().deleteImages(files);
            } catch (IOException e) {
                throw new RuntimeException(e); // todo ...
            }

            gView.updateTableModel(model.getGalleryModule().getSerializedData());
        });

        // redundancy check button
        gView.getUiGalleryButtonPanel().getRedundancyButton().addActionListener(a -> {
            // todo

            gView.getUiGalleryButtonPanel().setLocked(true);

            gView.getUiGalleryButtonPanel().getRedundancyButton().setEnabled(false);

//            gView.getUiHeader_().getGalleryButton().setEnabled(false);
//            gView.getUiHeader_().getSettingsButton().setEnabled(false);
//            gView.getUiHeader_().getComparerButton().setEnabled(false);

            gView.getUiGalleryButtonPanel().getAddImageButton().setEnabled(false);


            executeRedundancyCheck();

        });

        // attach tag button
        gView.getUiGalleryButtonPanel().getAttachTagButton().addActionListener(a -> {
            String tag = gView.openInputDialog(
                "Which tag would you like to add?",
                "Attach tag", model.getGalleryModule().getTags(), "Unordered"
            );

            if(tag != null && !tag.isEmpty()) {
                //...
                try {
                    int[] selectedRows = gView.getFilesTable().getSelectedRows();
                    for (int row : selectedRows) {
                        model.getGalleryModule().attachTag(
                            (File) gView.getFilesTable().getValueAt(row, 0),
                            tag
                        );
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e); // todo ...
                }

                gView.updateTableModel(model.getGalleryModule().getSerializedData());
            }
        });

        // delete tag button
        gView.getUiGalleryButtonPanel().getDeleteTagButton().addActionListener(a -> {
            // todo ...
            String tag = gView.openInputDialog(
                "Which tag would you like to remove?", "Delete tag",
                model.getGalleryModule().getTags(), "Unordered"
            );

            if(tag != null && !tag.isEmpty()) {
                try {
                    int[] selectedRows = gView.getFilesTable().getSelectedRows();
                    for (int row : selectedRows) {
                            model.getGalleryModule().deleteTag(
                                (File) gView.getFilesTable().getValueAt(row, 0),
                                tag
                            );

                    }
                } catch (IOException e) {
                    throw new RuntimeException(e); // todo ...
                }
            }

            gView.updateTableModel(model.getGalleryModule().getSerializedData());
        });

        // open image preview on table
        gView.getFilesTable().addMouseListener(new MouseAdapter() {
            // todo
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() < 2 || e.getButton() != MouseEvent.BUTTON1) return;

                int row = gView.getFilesTable().rowAtPoint(e.getPoint());
                if(row >= 0 && row < gView.getFilesTable().getRowCount()) {
                    File selectedFile = (File) gView.getFilesTable().getValueAt(row, 0);
                    JOptionPane.showMessageDialog(null, "W.I.P", String.format("Image preview for: %s", selectedFile.getName()), JOptionPane.PLAIN_MESSAGE);
                }
            }
        });

        // selection settings
        gView.getFilesTable().getSelectionModel().addListSelectionListener(a -> {
            if (gView.getUiGalleryButtonPanel().isLocked()) return;

            boolean isAnyRowSelected = gView.getFilesTable().getSelectedRows().length != 0;

            gView.getUiGalleryButtonPanel().getDeleteImageButton().setEnabled(isAnyRowSelected);
            gView.getUiGalleryButtonPanel().getRedundancyButton().setEnabled(gView.getFilesTable().getSelectedRows().length > 1);
            gView.getUiGalleryButtonPanel().getAttachTagButton().setEnabled(isAnyRowSelected);
            gView.getUiGalleryButtonPanel().getDeleteTagButton().setEnabled(isAnyRowSelected);
        });*/

        // This method initializes every interactive element of view
        //SettingsView sView = view.getSettingsView();

        /*// destination button action listener
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
        );*/

        //ComparerView lView = view.getComparerView();

        // path button action listener
//        lView.getButton(Utility.Buttons.OPEN_SOURCE).addActionListener(e->{ // updated
//            if(lView.openFileChooser()) {
//                lView.getButton(Utility.Buttons.LOAD_FILES).setEnabled(true);
//            }
//        });

        // reset button action listener
//        lView.getButton(Utility.Buttons.RESET).addActionListener(e->{
//            //lView.writeLine("Resetting...");
//
//            workersFactory();
//            lView.clear();
//            model.getComparerModule().reset();
//
//            //lView.writeLine("Reset done.\n");
//        });
//
//        // load files button action listener
//        lView.getButton(Utility.Buttons.LOAD_FILES).addActionListener(e->{
//            //lView.writeLine("Loading images. It can take awhile...");
//
//            lView.getButton(Utility.Buttons.OPEN_SOURCE).setEnabled(false);
//            lView.getButton(Utility.Buttons.LOAD_FILES).setEnabled(false);
//            lView.getButton(Utility.Buttons.RESET).setEnabled(false);
//            sView.getModeSelector().setEnabled(false);
//            //model.getComparerModule().setSources(new File(lView.getPath()));
//
//            loadFilesWorker.execute();
//
//            view.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
//        });
//
//        // move files button action listener
//        lView.getButton(Utility.Buttons.MOVE_FILES).addActionListener(e->{
//            //lView.writeLine("File transfer started. It can take awhile...");
//
//            lView.getButton(Utility.Buttons.OPEN_SOURCE).setEnabled(false);
//            lView.getButton(Utility.Buttons.MOVE_FILES).setEnabled(false);
//            lView.getButton(Utility.Buttons.RESET).setEnabled(false);
//            sView.getModeSelector().setEnabled(false);
//
//            fileTransferWorker.execute();
//
//            view.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
//        });
    }

    private void initMenuView() {
        MenuView mv = view.getMenuView();

        mv.getComparerButton().addActionListener(e -> view.toggleScene(Utility.Scene.COMPARER));
        mv.getGalleryButton().addActionListener(e -> view.toggleScene(Utility.Scene.GALLERY));
        mv.getSettingsButton().addActionListener(e -> view.toggleScene(Utility.Scene.SETTINGS));
        mv.getCreditsButton().addActionListener(e -> {
            JOptionPane.showMessageDialog(
                    null, "Work in progress."
            );
        });
        mv.getExitButton().addActionListener(e -> System.exit(0));
    }

    private void initComparerView() {
        ComparerView cView = view.getComparerView();
        ComparerModule cModule = model.getComparerModule();

        // Path button action listener
        cView.getUiPath().getPathButton().addActionListener(e -> {
            if (cView.getUiPath().openFileChooser()) {
                model.getComparerModule().setSources(
                    new File(cView.getUiPath().getPath())
                );
            }
        });

        // List model initialization
        cView.getUiOutput().getDuplicateList().setModel(
            cModule.getDuplicateListModel()
        );
        cView.getUiOutput().getMappedObjectList().setModel(
            cModule.getMappedListModel()
        );

        // Load files & compare button
        cView.getLoadButton().addActionListener(e -> {
            // Assign a user picked path for Picture Comparer
            String path = cView.getUiPath().getPath();
            if (path == null) {
                JOptionPane.showMessageDialog(
                    null,
                    String.format("You didn't pick a directory with images to compare%nPick your path and try again."),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );

                return;
            }

            // Block destructive buttons :)
            cView.getUiPath().getPathButton().setEnabled(false);
            cView.getLoadButton().setEnabled(false);
            cView.getMoveButton().setEnabled(false);
            cView.getResetButton().setEnabled(false);

            // Execute map task
            cModule.getMapObjects().execute();
        });

        // Move files button
        cView.getMoveButton().addActionListener(e -> {
            // Check if moving files is valid
            if (cModule.getPc().getDuplicatesObjectCount() <= 0) {
                JOptionPane.showMessageDialog(
                        null,
                        String.format("You didn't load your files.%n Pick them, then load and try again."),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );

                return;
            }

            // Block destructive buttons :)
            cView.getUiPath().getPathButton().setEnabled(false);
            cView.getLoadButton().setEnabled(false);
            cView.getMoveButton().setEnabled(false);
            cView.getResetButton().setEnabled(false);

            // Execute transferObject task
            cModule.getTransferObjects().execute();
        });

        // Reset Button
        cView.getResetButton().addActionListener(e -> {
            comparerTasks();
            cView.clear();
            cModule.reset();

            cView.getLoadButton().setEnabled(true);
            cView.getMoveButton().setEnabled(true);

            cView.getStateLabel().setText("Ready.");
        });

        // Workers
        comparerTasks();

    }

    private void initSettingsView() {
        SettingsView sView = view.getSettingsView();
        SettingsModule sModule = model.getSettingsModule();
        ComparerModule cModule = model.getComparerModule();

        // Destination Path button
        sView.getDestinationForComparer().getPathButton().addActionListener(e -> {
            if (sView.getDestinationForComparer().openFileChooser()) {
                Path path;
                try {
                    path = Paths.get(sView.getDestinationForComparer().getPath());
                } catch (InvalidPathException ex) {
                    JOptionPane.showMessageDialog(
                        null,
                        String.format("You've picked invalid path.%nTry again!"),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }

                sModule.updateSetting("destination-for-pc", path.toString());
                cModule.setSources(path.toFile());
            }
        });

        // Save Button
        sView.getSaveButton().addActionListener(e -> {
            sModule.updateSetting(
                "mode",
                sView.getRecursiveModeToggle().isSelected() ? "recursive" : "not-recursive"
            );

            sModule.saveSettings();

            model.getComparerModule().getPc().setMode(
                sModule.getSetting("mode").equals("not-recursive") ?
                Comparer.Modes.NOT_RECURSIVE : Comparer.Modes.RECURSIVE
            );

            // In general, there is no need to change anything else.
        });

        if (sModule.getSetting("mode").equals("recursive")) {
            sView.getRecursiveModeToggle().setSelected(true);
        }

        sView.getDestinationForComparer().setPath(
            sModule.getSetting("destination-for-pc")
        );
    }

    private void initGalleryView() {
        GalleryView gView = view.getGalleryView();
        GalleryModule gModule = model.getGalleryModule();

        // Initialize gallery table
        gView.getGalleryTable().setModel(gModule.getGalleryModel());

        // Add Button
        gView.getAddImageButton().addActionListener(e -> {
            String path = gView.openFileChooser();

            if (path == null)
                return;

            try {
                gModule.addImage(path);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(
                    null,
                    String.format("Error encountered while adding the file.%n Try again!"),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
            } catch (InvalidTypeException ex) {
                JOptionPane.showMessageDialog(
                    null,
                    String.format(ex.getMessage()),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        });

        // Remove Button
        gView.getRemoveImageButton().addActionListener(e -> {
            int[] selected = gView.getGalleryTable().getSelectedRows();
            if (selected == null || selected.length == 0) {
                JOptionPane.showMessageDialog(
                    null,
                    String.format("You didn't pick any image to delete.%nTry again! You can do it!"),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );

                return;
            }

            for (int idx : selected) {
                gModule.removeImage(idx);
            }

            try {
                gModule.saveToFile();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(
                    null,
                    String.format("Error encountered while saving file.%nIt's possible that image wasn't deleted.%nRestart the app and try again"),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        });

        // Distinct Button
        gView.getDistinctButton().addActionListener(e -> {
            // This operation will lock gallery.

            int[] selected = gView.getGalleryTable().getSelectedRows();
            if (selected == null || selected.length < 2) {
                JOptionPane.showMessageDialog(
                        null,
                        String.format("You either didn't select any image or selected one image.%nTry again and this time select at least two images!"),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }
        });
    }

    private void comparerTasks() {
        // todo
        //  I should probably add some more EDT safety,
        //  like for example don't edit GUI elements inside a doInBackground(),

        ComparerView cView = view.getComparerView();
        ComparerModule cModule = model.getComparerModule();

        cModule.resetTasks(
                new SwingWorker<>() {
                    @Override
                    protected Void doInBackground() {
                        // If a task stays in this state, that means that Picture Comparer failed the task.
                        // Probably cuz of FileVisitor
                        cView.getStateLabel().setText("Preparing...");
                        view.setCursor(
                            Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR)
                        );

                        try {
                            cModule.setUp();
                        } catch (IOException ex) {
                            JOptionPane.showMessageDialog(
                                    null,
                                    String.format("Error message:%n%s%nPlease restart the app!", ex.getMessage()),
                                    "Error encountered!",
                                    JOptionPane.ERROR_MESSAGE
                            );

                            return null;
                        }

                        cView.getUiTray().update(
                                cModule.getPc().getTotalObjectCount(),
                                0
                        );

                        cModule.getMappedListModel().addAll(
                                cModule.getPc().getSourceFiles().stream()
                                        .map(File::getName)
                                        .toList()
                        );

                        cView.getStateLabel().setText("Mapping...");
                        cModule.compareAndExtract();

                        return null;
                    }

                    @Override
                    protected void done() {
                        if (state() == State.CANCELLED)
                            return;

                        cView.getStateLabel().setText("Updating...");
                        cView.getUiTray().update(
                                cModule.getPc().getTotalObjectCount(),
                                cModule.getPc().getDuplicatesObjectCount()
                        );

                        cModule.getDuplicateListModel().addAll(
                                cModule.getPc().getDuplicates().stream()
                                        .map(Record::getFile)
                                        .map(File::getName)
                                        .collect(Collectors.toList())
                        );

                        if (cModule.getPc().getDuplicatesObjectCount() > 0)
                            cView.getMoveButton().setEnabled(true);
                        cView.getResetButton().setEnabled(true);
                        cView.getUiPath().getPathButton().setEnabled(true);

                        cView.getStateLabel().setText("Done.");
                        view.setCursor(Cursor.getDefaultCursor());
                    }
                },
                new SwingWorker<>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        cView.getStateLabel().setText("Preparing...");
                        view.setCursor(
                                Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR)
                        );

                        cView.getStateLabel().setText("Moving...");
                        cModule.fileTransfer();
                        return null;
                    }

                    @Override
                    protected void done() {
                        if (state() == State.CANCELLED)
                            return;

                        cView.getResetButton().setEnabled(true);
                        cView.getUiPath().getPathButton().setEnabled(true);

                        cView.getStateLabel().setText("Done.");
                        view.setCursor(Cursor.getDefaultCursor());

                        int option = JOptionPane.showConfirmDialog(
                            null,
                            "Do you want to reset comparer?",
                            "Choose an option:",
                            JOptionPane.YES_NO_OPTION
                        );

                        if (option == JOptionPane.OK_OPTION) {
                            comparerTasks();
                            cView.clear();
                            cModule.reset();

                            cView.getLoadButton().setEnabled(true);
                            cView.getMoveButton().setEnabled(true);

                            cView.getStateLabel().setText("Ready.");
                        }
                    }
                }
        );
    }

    private void galleryTasks() {
        GalleryView gView = view.getGalleryView();
        GalleryModule gModule = model.getGalleryModule();

        gModule.resetTasks(
            new SwingWorker<>() {
                @Override
                protected Void doInBackground() throws Exception {
                    view.setCursor(
                        Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR)
                    );

                    return null;
                }

                @Override
                protected void done() {
                    view.setCursor(Cursor.getDefaultCursor());
                }
            },
            new SwingWorker<>() {
                @Override
                protected Void doInBackground() throws Exception {
                    return null;
                }
            },
            new SwingWorker<>() {
                @Override
                protected Void doInBackground() throws Exception {
                    return null;
                }
            }
        );
    }

//    private void workersFactory(){
//        // This method will create workers on-demand, which is handy in case of program restart without restart
//
//        ComparerModule compareLayer = model.getComparerModule();
//        ComparerView lView = view.getComparerView();
//        SettingsView sView = view.getSettingsView();
//
//        loadFilesWorker = new SwingWorker<>() {
//            @Override
//            protected Void doInBackground() {
//                //lView.writeLine("Setting up picture comparer.");
//                try {
//                    compareLayer.setUp();
//                } catch (IOException e) {
//                    JOptionPane.showMessageDialog(view, String.format("Error have occurred:%n%s", e.getMessage()));
//
//                    return null;
//                }
//                //lView.writeLine("Picture comparer ready.\nMapping files. It can take awhile...");
//
//                System.out.println(compareLayer.getPc().getDuplicatesObjectCount());
//
//                lView.updateTray(
//                    compareLayer.getPc().getTotalObjectCount(),
//                    0, // for now
//                    compareLayer.getPc().getDuplicatesObjectCount()
//                );
//
//                // v2
//                // Tray updating
//                executeProcessedObjects();
//
//                compareLayer.compareAndExtract();
//
//                //lView.writeLine("Completed checking for duplicates.");
//                lView.setDuplicatesFieldValue(compareLayer.getPc().getDuplicatesObjectCount());
//
//                view.setCursor(Cursor.getDefaultCursor());
//
//                if(compareLayer.getPc().getDuplicatesObjectCount() > 0)
//                    lView.getButton(Utility.Buttons.MOVE_FILES).setEnabled(true);
//
//                lView.getButton(Utility.Buttons.OPEN_SOURCE).setEnabled(true);
//                lView.getButton(Utility.Buttons.RESET).setEnabled(true);
//                sView.getModeSelector().setEnabled(true);
//
//
//                return null;
//            }
//        };
//
//        fileTransferWorker = new SwingWorker<>() {
//            @Override
//            protected Void doInBackground() {
//                //lView.writeLine(
////                    String.format("Moving duplicates from %s to %s",
////                        compareLayer.getSources(), compareLayer.getDestDir()
////                    )
////                );
//
//                compareLayer.fileTransfer();
//                //lView.writeLine("Completed moving files.");
//
//                view.setCursor(Cursor.getDefaultCursor());
//
//                lView.getButton(Utility.Buttons.OPEN_SOURCE).setEnabled(true);
//                lView.getButton(Utility.Buttons.RESET).setEnabled(true);
//                sView.getModeSelector().setEnabled(true);
//
//                return null;
//            }
//        };
//    }

    /*private void executeProcessedObjects() {
        // This method will create a new SwingWorker that will observe for any change in ProcessedObjectCount property,
        // and will update it when needed.
        ComparerModule compareLayer = model.getComparerModule();
        ComparerView lView = view.getComparerView();
        GalleryView gView = view.getGalleryView();

        SwingWorker<Void,Long> task = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                */
    /*long lastProcessedCount = compareLayer.getPc().getMappedObjects().size(); // for now
                while (!loadFilesWorker.isDone()) {
                    long thisProcessedCount = compareLayer.getPc().getMappedObjects().size(); // for now
                    if (lastProcessedCount < thisProcessedCount){
                        lastProcessedCount = thisProcessedCount;

                        publish(thisProcessedCount);
                    }
                }*/
    /*
                return null;
            }

            @Override
            protected void process(List<Long> chunks) {
                super.process(chunks);
                long processed = compareLayer.getPc().getMappedObjects().size(); // for now

//                lView.getUiMainPanel_().getUiTray().getProcessedField().setText(
//                        String.valueOf(processed)
//                );

                int progress = (int) ((double)processed / (double) compareLayer.getPc().getTotalObjectCount() * 100);

                gView.getUiGalleryButtonPanel()
                        .getRedundancyCheckProgressBar().setValue(progress);
                gView.getUiGalleryButtonPanel().getRedundancyCheckProgressBar().setString(progress + "%");
            }
        };

        task.execute();
    }*/

    /*private void executeRedundancyCheck() {
        // todo Start here next time
        //  Add tray :)
        //  Add locking,
        GalleryView gView = view.getGalleryView();
        ComparerModule comparerModule = model.getComparerModule();

        new SwingWorker<Void, Integer>() {

            @Override
            protected Void doInBackground() {
                publish(0);

                List<File> filesToCheck = new ArrayList<>();
                int[] rows = gView.getFilesTable().getSelectedRows();

                Arrays.stream(rows).forEach(
                    i -> filesToCheck.add((File) gView.getFilesTable().getValueAt(i, 0))
                );

                comparerModule.setSources(filesToCheck);
                try {
                    comparerModule.setUp();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                executeProcessedObjects();
                comparerModule.compareAndExtract();

                try {
                    model.getGalleryModule().deleteImages(
                        comparerModule.getPc().getDuplicates().stream()
                                .map(ImageRecord::getFile).toList()
                    );
                    comparerModule.removeRedundant();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                gView.updateTableModel(model.getGalleryModule().getSerializedData());

//                gView.getUiHeader_().getComparerButton().setEnabled(true);
//                gView.getUiHeader_().getSettingsButton().setEnabled(true);

                gView.getUiGalleryButtonPanel().getAddImageButton().setEnabled(true);

                gView.getUiGalleryButtonPanel().setLocked(false);

                JOptionPane.showMessageDialog(
                    view,
                    String.format("Redundancy check completed, removed: %d images",
                        model.getComparerModule().getPc().getDuplicates().size()
                    ),
                    "Success", JOptionPane.INFORMATION_MESSAGE
                );

                return null;
            }

            @Override
            protected void done() {
                super.done();
                gView.getUiGalleryButtonPanel().getRedundancyCheckProgressBar().setString("Done");
            }
        }.execute();
    }*/
}
