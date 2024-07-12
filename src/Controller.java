import Modules.ComparerModule;
import Modules.GalleryModule;
import Modules.SettingsModule;
import UiComponents.Utility;
import UiViews.*;
import pl.magzik.Comparer;
import pl.magzik.Structures.Record;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.List;

public class Controller {

    private final View view;
    private final Model model;

    public Controller(View view, Model model) {
        this.view = view;
        this.model = model;

        // Initialize view controllable elements
        initView();
        initMenuView();
        initComparerView();
        initSettingsView();
        initGalleryView();
    }

    private void initView(){
        // Back buttons for each scene
        view.getScenes().forEach( p -> {
            if (p instanceof AbstractView) {
                ((AbstractView) p)
                    .getUiHeader_()
                    .getBackButton()
                    .addActionListener(_ -> view.toggleScene(Utility.Scene.MENU));
            }
        });
    }

    private void initMenuView() {
        MenuView mv = view.getMenuView();

        mv.getComparerButton().addActionListener(_ -> view.toggleScene(Utility.Scene.COMPARER));
        mv.getGalleryButton().addActionListener(_ -> view.toggleScene(Utility.Scene.GALLERY));
        mv.getSettingsButton().addActionListener(_ -> view.toggleScene(Utility.Scene.SETTINGS));
        mv.getCreditsButton().addActionListener(_ -> view.toggleScene(Utility.Scene.CREDITS));
        mv.getExitButton().addActionListener(_ -> System.exit(0));
    }

    private void initComparerView() {
        ComparerView cView = view.getComparerView();
        ComparerModule cModule = model.getComparerModule();

        // Path button action listener
        cView.getUiPath().getPathButton().addActionListener(_ -> {
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
        cView.getLoadButton().addActionListener(_ -> {
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
        cView.getMoveButton().addActionListener(_ -> {
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
        cView.getResetButton().addActionListener(_ -> {
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
        sView.getDestinationForComparer().getPathButton().addActionListener(_ -> {
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
                cModule.setDestDir(path.toFile());
            }
        });

        // Save Button
        sView.getSaveButton().addActionListener(_ -> {
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
        gView.getGalleryTable().setModel(gModule.getGalleryTableModel());

        // Add Button
        gView.getAddImageButton().addActionListener(_ -> {
            // This operation will lock a gallery.
            /*if (gModule.isLocked()) {
                view.moduleLocked();
                return;
            }*/

            List<String> paths = gView.openFileChooser();

            if (paths == null) return;

            try {
                view.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                gModule.addImage(paths);
                view.setCursor(Cursor.getDefaultCursor());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(
                        null,
                        String.format("Error encountered while adding the file.%n Try again!"),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        // Remove Button
        gView.getRemoveImageButton().addActionListener(_ -> {
            // This operation will lock a gallery.
            /*if (gModule.isLocked()) {
                view.moduleLocked();
                return;
            }*/

            // Important note!
            // We must sort indexes and then reverse them
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

            Arrays.sort(selected);
            int l = 0, r = selected.length - 1;
            while (l < r) {
                int t = selected[l];
                selected[l] = selected[r];
                selected[r] = t;
                l++;
                r--;
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

        // Delete Button
        gView.getDeleteImageButton().addActionListener(_ -> {
            /*if (gModule.isLocked()) {
                view.moduleLocked();
                return;
            }*/

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

            int a = JOptionPane.showConfirmDialog(
                view,
                "Are you sure you want to delete these images?",
                "Are you sure???",
                JOptionPane.YES_NO_OPTION
            );

            if (a == JOptionPane.YES_OPTION) {
                for (int idx : selected) {
                    try {
                        gModule.deleteImage(idx);
                    } catch (IOException e) {
                        JOptionPane.showMessageDialog(
                            null,
                            String.format("Couldn't delete image!%nPlease restart the app!"),
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                        );
                    }
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
            }


        });

        // Distinct Button
        gView.getDistinctButton().addActionListener(_ -> {
            // This operation will lock a gallery.
            /*if (gModule.isLocked()) {
                view.moduleLocked();
                return;
            }*/

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

            // Lock buttons
            gView.lockModule();
            /*gView.getAddImageButton().setEnabled(false);
            gView.getRemoveImageButton().setEnabled(false);
            gView.getDistinctButton().setEnabled(false);
            gView.getUnifyNamesButton().setEnabled(false);
            gView.getOpenButton().setEnabled(false);*/

            galleryTasks();

            gModule.getMapObjects().execute();
        });

        // Unify Button
        gView.getUnifyNamesButton().addActionListener(_ -> {
            // This operation will lock a gallery.
            /*if (gModule.isLocked()) {
                view.moduleLocked();
                return;
            }*/
            gView.lockModule();

            gModule.getUnifyNames().execute();
        });

        // Open Button
        gView.getOpenButton().addActionListener(_ -> {
            int[] selected = gView.getGalleryTable().getSelectedRows();
            if (selected == null || selected.length == 0) {
                JOptionPane.showMessageDialog(
                    null,
                    String.format("You didn't pick any image to open.%nTry again! You can do it!"),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            for (int idx : selected) {
                try {
                    gModule.openImage(idx);
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(
                        null,
                        String.format("Couldn't open image%nTry again!"),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            }

        });

        // Table click on cell
        gView.getGalleryTable().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JTable table = gView.getGalleryTable();

                int r = table.rowAtPoint(e.getPoint());
                int c = table.columnAtPoint(e.getPoint());

                if (table.isCellEditable(r, c)) {
                    table.editCellAt(r, c);
                    Component editor = table.getEditorComponent();
                    editor.requestFocus();
                }
            }
        });

        gModule.getGalleryTableModel().addTableModelListener(e -> {
            int c = e.getColumn();

            if (c == 0 && e.getType() == TableModelEvent.UPDATE) {
                try {
                    gModule.saveToFile();
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(
                        null,
                        String.format("Couldn't save changes%nRestart the app!"),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        });

        // Workers
        galleryTasks();
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
                    protected Void doInBackground() {
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
        SettingsView sView = view.getSettingsView();
        GalleryModule gModule = model.getGalleryModule();

        gModule.resetTasks(
            new SwingWorker<>() {
                @Override
                protected Void doInBackground() {
                    view.setCursor(
                        Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR)
                    );

                    try {
                        gModule.prepareComparer(
                            sView.getDestinationForComparer().getPath(),
                            Arrays.stream(gView.getGalleryTable().getSelectedRows()).boxed().toList()
                        );
                    } catch (FileNotFoundException e) {
                        JOptionPane.showMessageDialog(
                            null,
                            String.format("Error message:%n%s%nPlease restart the app!", e.getMessage()),
                            "Error encountered!",
                            JOptionPane.ERROR_MESSAGE
                        );

                        return null;
                    }

                    gModule.compare();
                    return null;
                }

                @Override
                protected void done() {
                    view.setCursor(Cursor.getDefaultCursor());

                    int ans = JOptionPane.showConfirmDialog(
                        view,
                    "Do you want to delete all duplicates?",
                        "What to do?",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                    );

                    if (ans == JOptionPane.YES_OPTION)
                        gModule.getRemoveObjects().execute();
                    else
                        gModule.getTransferObjects().execute();

                }
            },
            new SwingWorker<>() {
                @Override
                protected Void doInBackground() {
                    view.setCursor(
                        Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR)
                    );

                    try {
                        gModule.moveRedundant();
                    } catch (IOException e) {
                        JOptionPane.showMessageDialog(
                            null,
                            String.format("Error message:%n%s%nPlease restart the app!", e.getMessage()),
                            "Error encountered!",
                            JOptionPane.ERROR_MESSAGE
                        );
                    }
                    return null;
                }

                @Override
                protected void done() {
                    view.setCursor(Cursor.getDefaultCursor());
                    gView.unlockModule();

                    JOptionPane.showMessageDialog(view, "Redundant images transfer completed.");
                }
            },
            new SwingWorker<>() {
                @Override
                protected Void doInBackground() {
                    view.setCursor(
                        Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR)
                    );

                    try {
                        gModule.removeRedundant();
                    } catch (IOException e) {
                        JOptionPane.showMessageDialog(
                            null,
                            String.format("Error message:%n%s%nPlease restart the app!", e.getMessage()),
                            "Error encountered!",
                            JOptionPane.ERROR_MESSAGE
                        );
                    }

                    return null;
                }

                @Override
                protected void done() {
                    view.setCursor(Cursor.getDefaultCursor());
                    gView.unlockModule();

                    JOptionPane.showMessageDialog(view, "Redundant images deletion completed.");
                }
            }
        );

        gModule.setUnifyNames(new SwingWorker<>() {
            @Override
            protected Void doInBackground()  {
                try {
                    gModule.unifyNames();
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(
                            null,
                            String.format("Error: %s", e.getMessage()),
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }

                return null;
            }

            @Override
            protected void done() {
                try {
                    //repairModel();
                    gModule.saveToFile();
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(
                            null,
                            "Couldn't repair model or save, please restart the app!",
                            "Error:",
                            JOptionPane.ERROR_MESSAGE
                    );
                }

                JOptionPane.showMessageDialog(
                        null,
                        String.format("Names are unified now.%nYay!"),
                        "Information:",
                        JOptionPane.INFORMATION_MESSAGE
                );


                gView.unlockModule();

                galleryTasks();
            }
        });
    }
}
