import Modules.ComparerModule;
import Modules.GalleryModule;
import Modules.SettingsModule;
import UiComponents.Utility;
import UiViews.*;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TableModelEvent;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
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
        // TODO LESS REDUNDANCY

        SettingsView sView = view.getSettingsView();
        SettingsModule sModule = model.getSettingsModule();
        ComparerModule cModule = model.getComparerModule();
        GalleryModule gModule = model.getGalleryModule();

        // Destination Path button
        sView.getDestinationOpenButton().addActionListener(_ -> {
            sView.openDestinationFileChooser();
        });

        // Save Button
        sView.getSaveButton().addActionListener(_ -> {
            boolean messageNeeded = false;

            String lang = resourceBundle.getString("LOC_SETTINGS_LANG_" + sModule.getSetting("language"));
            if (!lang.equals(sView.getLanguageComboBox().getSelectedItem())) {
                messageNeeded = true;

                String newLang = (String) sView.getLanguageComboBox().getSelectedItem();

                resourceBundle.keySet().stream()
                    .filter(k -> resourceBundle.getString(k).equals(newLang))
                    .findFirst()
                    .ifPresentOrElse(k -> {
                        String[] ks = k.split("_");
                        sModule.updateSetting("language", ks[ks.length-1]);
                    }, () -> {
                        throw new RuntimeException(); // TODO
                });
            }

            String theme = resourceBundle.getString("LOC_SETTINGS_THEME_" + sModule.getSetting("theme"));
            if (!theme.equals(sView.getThemeComboBox().getSelectedItem())) {
                messageNeeded = true;

                String newTheme = (String) sView.getThemeComboBox().getSelectedItem();
                resourceBundle.keySet().stream()
                    .filter(k -> resourceBundle.getString(k).equals(newTheme))
                    .findFirst()
                    .ifPresentOrElse(k -> {
                        String[] ks = k.split("_");
                        sModule.updateSetting("theme", ks[ks.length-1]);
                    }, () -> {
                        throw new RuntimeException(); // TODO
                    });
            }

            if (!sModule.getSetting("destination-for-pc").equals(sView.getDestinationTextField().getText())) {
                sModule.updateSetting("destination-for-pc", sView.getDestinationTextField().getText());
                cModule.setDestination(new File(sView.getDestinationTextField().getText()));
            }

            sModule.updateSetting(
                "mode",
                sView.getRecursiveModeToggle().isSelected() ? "recursive" : "not-recursive"
            );

            cModule.setMode(
                sModule.getSetting("mode").equals("not-recursive") ?
                ComparerModule.Mode.NON_RECURSIVE : ComparerModule.Mode.RECURSIVE
            );

            gModule.setMode(
                sModule.getSetting("mode").equals("not-recursive") ?
                ComparerModule.Mode.NON_RECURSIVE : ComparerModule.Mode.RECURSIVE
            );

            sModule.updateSetting(
                "phash",
                    sView.getPHashModeToggle().isSelected() ? "yes" : "no"
            );

            cModule.setPHash(
                sModule.getSetting("phash").equals("yes")
            );

            gModule.setPHash(
                    sModule.getSetting("phash").equals("yes")
            );

            sModule.updateSetting(
                "pbp",
                sView.getPHashModeToggle().isSelected() ? "yes" : "no"
            );

            cModule.setPixelByPixel(
                sModule.getSetting("pbp").equals("yes")
            );

            gModule.setPixelByPixel(
                sModule.getSetting("pbp").equals("yes")
            );

            if (!sView.getUnifyNamesPrefixTextField().getText().equals(sModule.getSetting("unify-names-prefix"))) {
                sModule.updateSetting(
                    "unify-names-prefix",
                    sView.getUnifyNamesPrefixTextField().getText()
                );

                gModule.setNameTemplate(
                    sModule.getSetting("unify-names-prefix")
                );
            }

            sModule.updateSetting(
                "unify-names-lowercase",
                sView.getUnifyNamesLowerCaseToggle().isSelected() ? "yes" : "no"
            );

            gModule.setLowercaseExtension(
                sModule.getSetting("unify-names-lowercase").equals("yes")
            );

            sModule.saveSettings();

            if (messageNeeded) {
                JOptionPane.showMessageDialog(
                    view,
                    resourceBundle.getString("LOC_MESSAGE_RESTART_REQUIRED_DESC"),
                    resourceBundle.getString("LOC_MESSAGE_RESTART_REQUIRED_TITLE"),
                    JOptionPane.INFORMATION_MESSAGE
                );
            }
        });

        // Language settings init
        String[] langs = sModule.getSetting("languages").split(",");
        for (String lang : langs) {
            String key = "LOC_SETTINGS_LANG_" + lang;
            if (resourceBundle.containsKey(key))
                sView.getLanguageComboBox().addItem(resourceBundle.getString(key));
        }

        String language = "LOC_SETTINGS_LANG_" + sModule.getSetting("language");
        resourceBundle.keySet().stream()
            .filter(k -> k.equals(language))
            .findFirst()
            .ifPresentOrElse(k -> sView.getLanguageComboBox().setSelectedItem(resourceBundle.getString(k)), () -> {
                throw new RuntimeException(); // TODO
        });

        // Theme settings init
        String[] themes = sModule.getSetting("themes").split(",");
        for (String theme : themes) {
            String key = "LOC_SETTINGS_THEME_" + theme;
            if (resourceBundle.containsKey(key))
                sView.getThemeComboBox().addItem(resourceBundle.getString(key));
        }

        String theme = "LOC_SETTINGS_THEME_" + sModule.getSetting("theme");
        resourceBundle.keySet().stream()
            .filter(k -> k.equals(theme))
            .findFirst()
            .ifPresentOrElse(k -> sView.getThemeComboBox().setSelectedItem(resourceBundle.getString(k)), () -> {
                throw new RuntimeException(); // TODO
        });



        // Comparer's settings init
        sView.getDestinationTextField().setText(
            sModule.getSetting("destination-for-pc")
        );

        sView.getRecursiveModeToggle().setSelected(
            sModule.getSetting("mode").equals("recursive")
        );

        sView.getPHashModeToggle().setSelected(
            sModule.getSetting("phash").equals("yes")
        );

        sView.getPixelByPixelModeToggle().setSelected(
            sModule.getSetting("pbp").equals("yes")
        );

        // Gallery's settings init
        sView.getUnifyNamesPrefixTextField().setText(
            sModule.getSetting("unify-names-prefix")
        );

        sView.getUnifyNamesLowerCaseToggle().setSelected(
            sModule.getSetting("unify-names-lowercase").equals("yes")
        );

        // Comparer Module settings init
        cModule.setDestination(new File(sModule.getSetting("destination-for-pc")));
        cModule.setMode(
            sModule.getSetting("mode").equals("recursive") ? ComparerModule.Mode.RECURSIVE : ComparerModule.Mode.NON_RECURSIVE
        );
        cModule.setPHash(
            sModule.getSetting("phash").equals("yes")
        );
        cModule.setPixelByPixel(
            sModule.getSetting("pbp").equals("yes")
        );

        // Gallery Module settings init
        gModule.setDestination(new File(sModule.getSetting("destination-for-pc")));
        gModule.setMode(
            sModule.getSetting("mode").equals("recursive") ? ComparerModule.Mode.RECURSIVE : ComparerModule.Mode.NON_RECURSIVE
        );
        gModule.setPHash(
            sModule.getSetting("phash").equals("yes")
        );
        gModule.setPixelByPixel(
            sModule.getSetting("pbp").equals("yes")
        );
        gModule.setNameTemplate(
            sModule.getSetting("unify-names-prefix")
        );
        gModule.setLowercaseExtension(
            sModule.getSetting("unify-names-lowercase").equals("yes")
        );
    }

    private void initGalleryView() {
        GalleryView gView = view.getGalleryView();
        GalleryModule gModule = model.getGalleryModule();

        // Initialize gallery table
        gView.getGalleryTable().setModel(gModule.getGalleryTableModel());
        gView.getGalleryTable().setRowSorter(gModule.getTableRowSorter());

        // Filter by Name Text Field
        gView.getNameFilterTextField().getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                gView.getGalleryTable().clearSelection();
                gModule.filterTable(gView.getNameFilterTextField().getText().trim());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                gView.getGalleryTable().clearSelection();
                gModule.filterTable(gView.getNameFilterTextField().getText().trim());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {}
        });

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

        // Add Tag Button
        gView.getAddTagButton().addActionListener(_ -> {
            int[] selected = gView.getGalleryTable().getSelectedRows();
            if (selected == null || selected.length == 0) {
                JOptionPane.showMessageDialog(
                    view,
                    String.format(translate("LOC_ERROR_DESC_14")),
                    translate("LOC_ERROR_TITLE_14"),
                    JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            JComboBox<String> comboBox = new JComboBox<>();
            for (String tag : gModule.getExistingTags()) {
                comboBox.addItem(translate(tag));
            }
            comboBox.setEditable(true);

            int result = JOptionPane.showConfirmDialog(
                null,
                comboBox,
                translate("LOC_ADD_TAG_OPTION_PANE"),
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );

            if (result == JOptionPane.OK_OPTION) {
                String tag = (String) comboBox.getSelectedItem();
                if (tag != null && !tag.matches("^[\\w\\-]+$")) {
                    JOptionPane.showMessageDialog(
                        view,
                        String.format(translate("LOC_ERROR_DESC_15")),
                        translate("LOC_ERROR_TITLE_15"),
                        JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }

                for (int idx : selected) {
                    try {
                        gModule.addTag(idx, tag);
                    } catch (IOException e) {
                        JOptionPane.showMessageDialog(
                            view,
                            String.format(translate("LOC_ERROR_DESC_10"), e.getMessage()),
                            translate("LOC_ERROR_TITLE_10"),
                            JOptionPane.ERROR_MESSAGE
                        );
                        return;
                    }
                }

                try {
                    gModule.saveToFile();
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(
                        view,
                        String.format(translate("LOC_ERROR_DESC_10"), e.getMessage()),
                        translate("LOC_ERROR_TITLE_10"),
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        });

        // Remove Tag Button
        gView.getRemoveTagButton().addActionListener(_ -> {
            int selected = gView.getGalleryTable().getSelectedRow();
            if (selected == -1) {
                JOptionPane.showMessageDialog(
                        view,
                        String.format(translate("LOC_ERROR_DESC_14")),
                        translate("LOC_ERROR_TITLE_14"),
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            JComboBox<String> comboBox = new JComboBox<>();
            String[] tags = gModule.getTags(selected);

            if (tags.length == 0) {
                JOptionPane.showMessageDialog(
                    view,
                    String.format(translate("LOC_ERROR_DESC_16")),
                    translate("LOC_ERROR_TITLE_16"),
                    JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            for (String tag : tags) {
                comboBox.addItem(translate(tag));
            }


            int result = JOptionPane.showConfirmDialog(
                null,
                comboBox,
                translate("LOC_REMOVE_TAG_OPTION_PANE"),
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );

            if (result == JOptionPane.OK_OPTION) {
                String tag = (String) comboBox.getSelectedItem();
                if (tag != null) gModule.removeTag(selected, tag);
            }

            try {
                gModule.saveToFile();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(
                    view,
                    String.format(translate("LOC_ERROR_DESC_10"), e.getMessage()),
                    translate("LOC_ERROR_TITLE_10"),
                    JOptionPane.ERROR_MESSAGE
                );
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

        gView.getElementCount().setText(String.valueOf(gModule.getGalleryTableModel().getRowCount()));

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
                        sView.getDestinationTextField().getText(),
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
                gView.getElementCount().setText(String.valueOf(gModule.getGalleryTableModel().getRowCount()));
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
                gView.getElementCount().setText(String.valueOf(gModule.getGalleryTableModel().getRowCount()));
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
                gView.getElementCount().setText(String.valueOf(gModule.getGalleryTableModel().getRowCount()));
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
