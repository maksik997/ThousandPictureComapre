import Modules.ComparerModule;
import Modules.GalleryModule;
import Modules.SettingsModule;
import UiComponents.Utility;
import UiViews.*;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
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
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import java.util.List;

public class Controller {

    private final View view;
    private final Model model;
    private final ResourceBundle resourceBundle;

    public Controller(View view, Model model, ResourceBundle resourceBundle) {
        this.view = view;
        this.model = model;
        this.resourceBundle = resourceBundle;

        // Initialize view controllable elements
        initView();
        initMenuView();
        initComparerView();
        initSettingsView();
        initGalleryView();
    }

    private String translate(String key) {
        // FOR RESOURCE BUNDLES USAGE
        if (key != null && resourceBundle.containsKey(key)) {
            return resourceBundle.getString(key);
        }
        return key;
    }

    private void initView(){
        // Back buttons for each scene
        view.getScenes().forEach( p -> {
            if (p instanceof AbstractView) {
                ((AbstractView) p)
//                    .getUiHeader_()
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
                    view,
                    String.format(translate("LOC_ERROR_DESC_0")),
                    translate("LOC_ERROR_TITLE_0"),
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
            if (cModule.getComparerOutputSize() <= 0) {
                JOptionPane.showMessageDialog(
                        null,
                        String.format(translate("LOC_ERROR_DESC_1")),
                        translate("LOC_ERROR_TITLE_1"),
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

            cView.getStateLabel().setText(translate("LOC_COMPARER_VIEW_STATE_READY"));
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
                        String.format(translate("LOC_ERROR_DESC_2")),
                        translate("LOC_ERROR_TITLE_2"),
                        JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }

                sModule.updateSetting("destination-for-pc", path.toString());
                cModule.setDestination(path.toFile());
            }
        });

        // Save Button
        sView.getSaveButton().addActionListener(_ -> {
            sModule.updateSetting(
                "mode",
                sView.getRecursiveModeToggle().isSelected() ? "recursive" : "not-recursive"
            );

            sModule.saveSettings();

            model.getComparerModule().setMode(
                sModule.getSetting("mode").equals("not-recursive") ?
                ComparerModule.Mode.NON_RECURSIVE : ComparerModule.Mode.RECURSIVE
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
            gView.lockModule();
            gModule.getAddImages().execute();
        });

        // Remove Button
        gView.getRemoveImageButton().addActionListener(_ -> {
            gView.lockModule();
            gModule.getRemoveImages().execute();
        });

        // Delete Button
        gView.getDeleteImageButton().addActionListener(_ -> {
            int[] selected = gView.getGalleryTable().getSelectedRows();

            if (selected == null || selected.length == 0) {
                JOptionPane.showMessageDialog(
                    null,
                    String.format(translate("LOC_ERROR_DESC_3")),
                    translate("LOC_ERROR_TITLE_3"),
                    JOptionPane.ERROR_MESSAGE
                );

                return;
            }

            int a = JOptionPane.showConfirmDialog(
                view,
                translate("LOC_CONFIRMATION_DESC_0"),
                translate("LOC_CONFIRMATION_TITLE_0"),
                JOptionPane.YES_NO_OPTION
            );

            if (a == JOptionPane.YES_OPTION) {
                for (int idx : selected) {
                    try {
                        gModule.deleteImage(idx);
                    } catch (IOException e) {
                        JOptionPane.showMessageDialog(
                            null,
                            String.format(translate("LOC_ERROR_DESC_4")),
                            translate("LOC_ERROR_TITLE_4"),
                            JOptionPane.ERROR_MESSAGE
                        );
                    }
                }

                try {
                    gModule.saveToFile();
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(
                        null,
                        String.format(translate("LOC_ERROR_DESC_5")),
                        translate("LOC_ERROR_TITLE_5"),
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            }


        });

        // Distinct Button
        gView.getDistinctButton().addActionListener(_ -> {
            int[] selected = gView.getGalleryTable().getSelectedRows();
            if (selected == null || selected.length < 2) {
                JOptionPane.showMessageDialog(
                    null,
                    String.format(translate("LOC_ERROR_DESC_6")),
                    translate("LOC_ERROR_TITLE_6"),
                    JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            // Lock buttons
            gView.lockModule();

//            galleryTasks();

            gModule.getMapObjects().execute();
        });

        // Unify Button
        gView.getUnifyNamesButton().addActionListener(_ -> {
            gView.lockModule();

            gModule.getUnifyNames().execute();
        });

        // Open Button
        gView.getOpenButton().addActionListener(_ -> {
            int[] selected = gView.getGalleryTable().getSelectedRows();
            if (selected == null || selected.length == 0) {
                JOptionPane.showMessageDialog(
                    null,
                    String.format(translate("LOC_ERROR_DESC_7")),
                    translate("LOC_ERROR_TITLE_7"),
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
                        String.format(translate("LOC_ERROR_DESC_8")),
                        translate("LOC_ERROR_TITLE_8"),
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
                        String.format(translate("LOC_ERROR_DESC_9")),
                        translate("LOC_ERROR_TITLE_9"),
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        });

        // Tasks
        resetGalleryDistinctTasks();
        resetGalleryUnifyNamesTask();
        resetGalleryAddImagesTask();
        resetGalleryRemoveImagesTask();
    }

    private void comparerTasks() {
        // todo
        //  I should probably add some more EDT safety,
        //  like for example don't edit GUI elements inside a doInBackground(),

        ComparerView cView = view.getComparerView();
        ComparerModule cModule = model.getComparerModule();

        cModule.setMapObjects(new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                // If a task stays in this state, that means that Picture Comparer failed the task.
                // Probably cuz of FileVisitor
                cView.getStateLabel().setText(translate("LOC_COMPARER_VIEW_STATE_PREPARE"));
                view.setCursor(
                        Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR)
                );

                try {
                    cModule.load();
                } catch (IOException | InterruptedException | TimeoutException e) {
                    JOptionPane.showMessageDialog(
                        view,
                        String.format(translate("LOC_ERROR_DESC_10"), e.getMessage()),
                        translate("LOC_ERROR_TITLE_10"),
                        JOptionPane.ERROR_MESSAGE
                    );

                    return null;
                }

                cView.getUiTray().update(
                        cModule.getSourcesSize(),
                        0
                );

                cModule.getMappedListModel().addAll(
                        cModule.getSources().stream()
                                .map(File::getName)
                                .toList()
                );

                cView.getStateLabel().setText(translate("LOC_COMPARER_VIEW_STATE_MAP"));

                try {
                    cModule.compareAndExtract();
                } catch (IOException | ExecutionException e) {
                    JOptionPane.showMessageDialog(
                            view,
                            String.format(translate("LOC_ERROR_DESC_10"), e.getMessage()),
                            translate("LOC_ERROR_TITLE_10"),
                            JOptionPane.ERROR_MESSAGE
                    );

                    return null;
                }

                return null;
            }

            @Override
            protected void done() {
                if (state() == State.CANCELLED)
                    return;

                cView.getStateLabel().setText(translate("LOC_COMPARER_VIEW_STATE_UPDATE"));
                cView.getUiTray().update(
                        cModule.getSourcesSize(),
                        cModule.getComparerOutputSize()
                );

                cModule.getDuplicateListModel().addAll(
                        cModule.getComparerOutput().stream()
//                                        .map(Record::getFile)
                                .map(File::getName)
                                .collect(Collectors.toList())
                );

                if (cModule.getComparerOutputSize() > 0)
                    cView.getMoveButton().setEnabled(true);
                cView.getResetButton().setEnabled(true);
                cView.getUiPath().getPathButton().setEnabled(true);

                cView.getStateLabel().setText(translate("LOC_COMPARER_VIEW_STATE_DONE"));
                view.setCursor(Cursor.getDefaultCursor());
            }
        });
        cModule.setTransferObjects(new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                cView.getStateLabel().setText(translate("LOC_COMPARER_VIEW_STATE_PREPARE"));
                view.setCursor(
                        Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR)
                );

                cView.getStateLabel().setText(translate("LOC_COMPARER_VIEW_STATE_MOVE"));
                try {
                    cModule.fileTransfer();
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(
                            view,
                            String.format(translate("LOC_ERROR_DESC_10"), e.getMessage()),
                            translate("LOC_ERROR_TITLE_10"),
                            JOptionPane.ERROR_MESSAGE
                    );

                    return null;
                }
                return null;
            }

            @Override
            protected void done() {
                if (state() == State.CANCELLED)
                    return;

                cView.getResetButton().setEnabled(true);
                cView.getUiPath().getPathButton().setEnabled(true);

                cView.getStateLabel().setText(translate("LOC_COMPARER_VIEW_STATE_DONE"));
                view.setCursor(Cursor.getDefaultCursor());

                int option = JOptionPane.showConfirmDialog(
                        null,
                        translate("LOC_CONFIRMATION_DESC_1"),
                        translate("LOC_CONFIRMATION_TITLE_1"),
                        JOptionPane.YES_NO_OPTION
                );

                if (option == JOptionPane.OK_OPTION) {
                    comparerTasks();
                    cView.clear();
                    cModule.reset();

                    cView.getLoadButton().setEnabled(true);
                    cView.getMoveButton().setEnabled(true);

                    cView.getStateLabel().setText(translate("LOC_COMPARER_VIEW_STATE_READY"));
                }
            }
        });
    }

    private void resetGalleryDistinctTasks() {
        // Todo: EDT safety

        // Get references
        GalleryView gView = view.getGalleryView();
        SettingsView sView = view.getSettingsView();
        GalleryModule gModule = model.getGalleryModule();

        // Reset Tasks
        gModule.setMapObjects(new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                view.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

                try {
                    gModule.prepareComparer(
                        sView.getDestinationForComparer().getPath(),
                        Arrays.stream(gView.getGalleryTable().getSelectedRows()).boxed().toList()
                    );
                } catch (IOException | InterruptedException | TimeoutException e) {
                    JOptionPane.showMessageDialog(
                        view,
                        String.format(translate("LOC_ERROR_DESC_10"), e.getMessage()),
                        translate("LOC_ERROR_TITLE_10"),
                        JOptionPane.ERROR_MESSAGE
                    );

                    return null;
                }

                try {
                    gModule.compare();
                } catch (IOException | ExecutionException e) {
                    JOptionPane.showMessageDialog(
                            view,
                            String.format(translate("LOC_ERROR_DESC_10"), e.getMessage()),
                            translate("LOC_ERROR_TITLE_10"),
                            JOptionPane.ERROR_MESSAGE
                    );

                    return null;
                }
                return null;
            }

            @Override
            protected void done() {
                view.setCursor(Cursor.getDefaultCursor());

                int ans = JOptionPane.showConfirmDialog(
                        view,
                        translate("LOC_CONFIRMATION_DESC_2"),
                        translate("LOC_CONFIRMATION_TITLE_2"),
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );

                if (ans == JOptionPane.YES_OPTION)
                    gModule.getRemoveObjects().execute();
                else
                    gModule.getTransferObjects().execute();

            }
        });
        gModule.setRemoveObjects(new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                view.setCursor(
                        Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR)
                );

                try {
                    gModule.removeRedundant();
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(
                            view,
                            String.format(translate("LOC_ERROR_DESC_10"), e.getMessage()),
                            translate("LOC_ERROR_TITLE_10"),
                            JOptionPane.ERROR_MESSAGE
                    );
                }

                return null;
            }

            @Override
            protected void done() {
                view.setCursor(Cursor.getDefaultCursor());
                gView.unlockModule();

                JOptionPane.showMessageDialog(view, translate("LOC_MESSAGE_0"));
                resetGalleryDistinctTasks();
            }
        });
        gModule.setTransferObjects(new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                view.setCursor(
                        Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR)
                );

                try {
                    gModule.moveRedundant();
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(
                            view,
                            String.format(translate("LOC_ERROR_DESC_10"), e.getMessage()),
                            translate("LOC_ERROR_TITLE_10"),
                            JOptionPane.ERROR_MESSAGE
                    );
                }
                return null;
            }

            @Override
            protected void done() {
                view.setCursor(Cursor.getDefaultCursor());
                gView.unlockModule();

                JOptionPane.showMessageDialog(view, translate("LOC_MESSAGE_1"));
                resetGalleryDistinctTasks();
            }
        });
    }

    private void resetGalleryUnifyNamesTask() {
        // Todo: EDT safety

        GalleryView gView = view.getGalleryView();
        GalleryModule gModule = model.getGalleryModule();

        gModule.setUnifyNames(new SwingWorker<>() {
            @Override
            protected Void doInBackground()  {
                view.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

                try {
                    gModule.unifyNames();
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(
                            view,
                            String.format(translate("LOC_ERROR_DESC_10"), e.getMessage()),
                            translate("LOC_ERROR_TITLE_10"),
                            JOptionPane.ERROR_MESSAGE
                    );
                }

                try {
                    gModule.saveToFile();
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(
                        view,
                        translate("LOC_ERROR_DESC_11"),
                        translate("LOC_ERROR_TITLE_11"),
                        JOptionPane.ERROR_MESSAGE
                    );
                }

                return null;
            }

            @Override
            protected void done() {
                JOptionPane.showMessageDialog(view, String.format(translate("LOC_MESSAGE_2")));

                gView.unlockModule();
                view.setCursor(Cursor.getDefaultCursor());
                resetGalleryUnifyNamesTask();
            }
        });
    }

    private void resetGalleryAddImagesTask() {
        // Todo: EDT Safety

        GalleryView gView = view.getGalleryView();
        GalleryModule gModule = model.getGalleryModule();

        gModule.setAddImages(new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                view.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

                List<String> paths = gView.openFileChooser();
                if (paths == null) return null;

                try {
                    gModule.addImage(paths);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(
                        view,
                        String.format(translate("LOC_ERROR_DESC_12")),
                        translate("LOC_ERROR_TITLE_12"),
                        JOptionPane.ERROR_MESSAGE
                    );
                }

                return null;
            }

            @Override
            protected void done() {
                gView.unlockModule();
                view.setCursor(Cursor.getDefaultCursor());
                resetGalleryAddImagesTask();
            }
        });
    }

    private void resetGalleryRemoveImagesTask() {
        // Todo: EDT Safety

        GalleryView gView = view.getGalleryView();
        GalleryModule gModule = model.getGalleryModule();

        gModule.setRemoveImages(new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                view.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

                // Important note!
                // We must sort indexes and then reverse them
                int[] selected = gView.getGalleryTable().getSelectedRows();

                if (selected == null || selected.length == 0) {
                    JOptionPane.showMessageDialog(
                            null,
                            String.format(translate("LOC_ERROR_DESC_3")),
                            translate("LOC_ERROR_TITLE_3"),
                            JOptionPane.ERROR_MESSAGE
                    );
                    return null;
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
                        String.format(translate("LOC_ERROR_DESC_13")),
                        translate("LOC_ERROR_TITLE_13"),
                        JOptionPane.ERROR_MESSAGE
                    );
                }

                return null;
            }

            @Override
            protected void done() {
                gView.unlockModule();
                view.setCursor(Cursor.getDefaultCursor());
                gView.getGalleryTable().clearSelection();
                resetGalleryRemoveImagesTask();
            }
        });
    }
}
