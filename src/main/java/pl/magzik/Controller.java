package pl.magzik;

import pl.magzik.controllers.localization.TranslationInterface;
import pl.magzik.modules.ComparerInterface;
import pl.magzik.modules.ComparerModule;
import pl.magzik.modules.GalleryModule;
import pl.magzik.modules.SettingsModule;
import pl.magzik.ui.components.Utility;
import pl.magzik.ui.views.*;

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

public class Controller implements TranslationInterface {

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

    @Override
    public String translate(String key) {
        // Resource Bundle
        if (key != null && resourceBundle.containsKey(key)) {
            return resourceBundle.getString(key);
        }
        return key;
    }

    @Override
    public String reverseTranslate(String key) {
        // Resource Bundle
        return resourceBundle.keySet().stream()
            .filter(k -> resourceBundle.getString(k).equals(key))
            .findFirst()
            .orElse(key);
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

        // Load files & compareAndExtract button
        cView.getLoadButton().addActionListener(_ -> {
            // Assign a user picked path for Picture Comparer
            String path = cView.getUiPath().getPath();
            if (path == null) {
                view.showErrorMessage(
                    translate("error.general.title"),
                    translate("error.comparer.lack_of_images.desc")
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
                view.showErrorMessage(
                    translate("error.comparer.loading_needed.desc"),
                    translate("error.general.title")
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

            cView.getStateLabel().setText(translate("comparer.state.ready"));
        });

        // Workers
        comparerTasks();

    }

    private void initSettingsView() {
        SettingsView sView = view.getSettingsView();
        SettingsModule sModule = model.getSettingsModule();
        ComparerModule cModule = model.getComparerModule();
        GalleryModule gModule = model.getGalleryModule();

        // Save Button
        sView.getSaveButton().addActionListener(_ -> {
            boolean messageNeeded = false;

            // Check if the language has changed.
            String lang = translate("lang." + sModule.getSetting("language"));
            if (!lang.equals(sView.getLanguageEntry().getValue())) {
                messageNeeded = true;

                String[] splitKey = reverseTranslate(
                    (String) sView.getLanguageEntry().getValue()
                ).split("\\.");
                sModule.updateSetting("language", splitKey[splitKey.length-1]);
            }

            // Check if the theme has changed.
            String theme = translate("theme." + sModule.getSetting("theme"));
            if (!theme.equals(sView.getThemeEntry().getValue())) {
                messageNeeded = true;

                String[] splitKey = reverseTranslate(
                    (String) sView.getThemeEntry().getValue()
                ).split("\\.");
                sModule.updateSetting("theme", splitKey[splitKey.length-1]);
            }

            if (!sModule.getSetting("destination-for-pc").equals(sView.getDestinationEntry().getValue())) {
                sModule.updateSetting("destination-for-pc", sView.getDestinationEntry().getValue());
            }

            // Update mode
            sModule.updateSetting(
                "mode",
                sView.getRecursiveModeEntry().getValue() ? "recursive" : "not-recursive"
            );

            // Update phash
            sModule.updateSetting(
                "phash",
                    sView.getPHashModeEntry().getValue() ? "yes" : "no"
            );

            // Update pbp
            sModule.updateSetting(
                "pbp",
                sView.getPixelByPixelModeEntry().getValue() ? "yes" : "no"
            );

            updateComparerSettings(cModule);
            updateComparerSettings(gModule);


            // Check if the unify names prefix has changed.
            if (!sView.getNamesPrefixEntry().getValue().equals(sModule.getSetting("unify-names-prefix"))) {
                sModule.updateSetting(
                    "unify-names-prefix",
                    sView.getNamesPrefixEntry().getValue()
                );

                gModule.setNameTemplate(
                    sModule.getSetting("unify-names-prefix")
                );
            }

            // Update unify names lowercase.
            sModule.updateSetting(
                "unify-names-lowercase",
                sView.getNamesLowerCaseEntry().getValue() ? "yes" : "no"
            );

            gModule.setLowercaseExtension(
                sModule.getSetting("unify-names-lowercase").equals("yes")
            );

            // Save settings and show the message if needed.
            sModule.saveSettings();
            if (messageNeeded) {
                view.showInformationMessage(
                    translate("message.restart_required.desc"),
                    translate("message.restart_required.title")
                );
            }
        });

        // Language setting initialization
        String[] languages = sModule.getSetting("languages").split(",");

        sView.getLanguageEntry().initializeComboBox(
            Arrays.stream(languages)
                .map(l -> translate("lang." + l))
                .toArray(String[]::new)
        );

        String language = "lang." + sModule.getSetting("language");

        sView.getLanguageEntry().setValue(translate(language));

        // Theme setting initialization
        String[] themes = sModule.getSetting("themes").split(",");

        sView.getThemeEntry().initializeComboBox(
            Arrays.stream(themes)
                .map(l -> translate("theme." + l))
                .toArray(String[]::new)
        );

        String theme = "theme." + sModule.getSetting("theme");
        sView.getThemeEntry().setValue(
            translate(theme)
        );


        // Comparer's settings init
        sView.getDestinationEntry().setValue(
            sModule.getSetting("destination-for-pc")
        );

        sView.getRecursiveModeEntry().setValue(
            sModule.getSetting("mode").equals("recursive")
        );

        sView.getPHashModeEntry().setValue(
            sModule.getSetting("phash").equals("yes")
        );

        sView.getPixelByPixelModeEntry().setValue(
            sModule.getSetting("pbp").equals("yes")
        );

        // Gallery's settings init
        sView.getNamesPrefixEntry().setValue(
            sModule.getSetting("unify-names-prefix")
        );

        sView.getNamesLowerCaseEntry().setValue(
            sModule.getSetting("unify-names-lowercase").equals("yes")
        );

        // Comparer Module settings init
        updateComparerSettings(cModule);

        // Gallery Module settings init
        updateComparerSettings(gModule);

        gModule.setNameTemplate(
            sModule.getSetting("unify-names-prefix")
        );
        gModule.setLowercaseExtension(
            sModule.getSetting("unify-names-lowercase").equals("yes")
        );

        sView.getSaveButton().setEnabled(false);
    }

    private void updateComparerSettings(ComparerInterface ci) {
        SettingsModule sModule = model.getSettingsModule();

        ci.setDestination(new File(sModule.getSetting("destination-for-pc")));
        ci.setMode(
            sModule.getSetting("mode").equals("recursive") ? ComparerModule.Mode.RECURSIVE : ComparerModule.Mode.NON_RECURSIVE
        );
        ci.setPHash(
            sModule.getSetting("phash").equals("yes")
        );
        ci.setPixelByPixel(
            sModule.getSetting("pbp").equals("yes")
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
                view.showErrorMessage(
                    translate("error.delete.no_images.desc"),
                    translate("error.general.title")
                );

                return;
            }

            int a = JOptionPane.showConfirmDialog(
                view,
                translate("message.confirmation.delete_images.desc"),
                translate("message.confirmation.delete_images.title"),
                JOptionPane.YES_NO_OPTION
            );

            if (a == JOptionPane.YES_OPTION) {
                selected = Arrays.stream(selected)
                    .map(i -> gModule.getTableRowSorter().convertRowIndexToModel(i))
                    .toArray();

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
                    try {
                        gModule.deleteImage(idx);
                    } catch (IOException e) {
                        view.showErrorMessage(
                            translate("error.delete.ioexception.desc"),
                            translate("error.general.title")
                        );

                        return;
                    }
                }

                try {
                    gModule.saveToFile();
                } catch (IOException ex) {
                    view.showErrorMessage(
                        translate("error.save.ioexception.desc"),
                        translate("error.general.title")
                    );
                }
            }


        });

        // Distinct Button
        gView.getDistinctButton().addActionListener(_ -> {
            int[] selected = gView.getGalleryTable().getSelectedRows();
            if (selected == null || selected.length < 2) {
                view.showErrorMessage(
                    translate("error.distinct.lack_of_images.desc"),
                    translate("error.general.title")
                );
                return;
            }

            // Lock buttons
            gView.lockModule();
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
                view.showErrorMessage(
                    translate("error.open.lack_of_images.desc"),
                    translate("error.general.title")
                );

                return;
            }

            for (int idx : selected) {
                try {
                    gModule.openImage(idx);
                } catch (IOException e) {
                    view.showErrorMessage(
                        translate("error.open.ioexception.desc"),
                        translate("error.general.title")
                    );
                }
            }

        });

        // Add Tag Button
        gView.getAddTagButton().addActionListener(_ -> {
            int[] selected = gView.getGalleryTable().getSelectedRows();
            if (selected == null || selected.length == 0) {
                view.showErrorMessage(
                    translate("error.tag.lack_of_images.desc"),
                    translate("error.general.title")
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
                translate("message.add_tag.title"),
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );

            if (result == JOptionPane.OK_OPTION) {
                String tag = (String) comboBox.getSelectedItem();
                if (tag != null && !tag.matches("^[\\w\\-]+$")) {
                    view.showErrorMessage(
                        translate("error.tag.invalid_string.desc"),
                        translate("error.general.title")
                    );
                    return;
                }

                for (int idx : selected) {
                    try {
                        gModule.addTag(idx, tag);
                    } catch (IOException e) {
                        view.showErrorMessage(
                            translate("error.general.desc"),
                            translate("error.general.title"),
                            e
                        );

                        return;
                    }
                }

                try {
                    gModule.saveToFile();
                } catch (IOException e) {
                    view.showErrorMessage(
                        translate("error.general.desc"),
                        translate("error.general.title"),
                        e
                    );
                }
            }
        });

        // Remove Tag Button
        gView.getRemoveTagButton().addActionListener(_ -> {
            int selected = gView.getGalleryTable().getSelectedRow();
            if (selected == -1) {
                view.showErrorMessage(
                    translate("error.tag.lack_of_images.desc"),
                    translate("error.general.title")
                );

                return;
            }

            JComboBox<String> comboBox = new JComboBox<>();
            String[] tags = gModule.getTags(selected);

            if (tags.length == 0) {
                view.showErrorMessage(
                    translate("error.tag.lack_of_tags.desc"),
                    translate("error.general.title")
                );

                return;
            }

            for (String tag : tags) {
                comboBox.addItem(translate(tag));
            }


            int result = JOptionPane.showConfirmDialog(
                null,
                comboBox,
                translate("message.remove_tag.title"),
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
                view.showErrorMessage(
                    translate("error.general.desc"),
                    translate("error.general.title"),
                    e
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
                    view.showErrorMessage(
                        translate("error.save.ioexception.desc"),
                        translate("error.general.title")
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
                cView.getStateLabel().setText(translate("comparer.state.prepare"));
                view.setCursor(
                        Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR)
                );

                try {
                    cModule.load();
                } catch (IOException | InterruptedException | TimeoutException e) {
                    view.showErrorMessage(
                        translate("error.general.desc"),
                        translate("error.general.title"),
                        e
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

                cView.getStateLabel().setText(translate("comparer.state.map"));

                try {
                    cModule.compareAndExtract();
                } catch (IOException | ExecutionException e) {
                    view.showErrorMessage(
                        translate("error.general.desc"),
                        translate("error.general.title"),
                        e
                    );

                    return null;
                }

                return null;
            }

            @Override
            protected void done() {
                if (state() == State.CANCELLED)
                    return;

                cView.getStateLabel().setText(translate("comparer.state.update"));
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

                cView.getStateLabel().setText(translate("comparer.state.done"));
                view.setCursor(Cursor.getDefaultCursor());
            }
        });
        cModule.setTransferObjects(new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                cView.getStateLabel().setText(translate("comparer.state.prepare"));
                view.setCursor(
                        Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR)
                );

                cView.getStateLabel().setText(translate("comparer.state.move"));
                try {
                    cModule.fileTransfer();
                } catch (IOException e) {
                    view.showErrorMessage(
                        translate("error.general.desc"),
                        translate("error.general.title"),
                        e
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

                cView.getStateLabel().setText(translate("comparer.state.done"));
                view.setCursor(Cursor.getDefaultCursor());

                int option = JOptionPane.showConfirmDialog(
                        null,
                        translate("message.confirmation.comparer_restart.desc"),
                        translate("message.confirmation.title"),
                        JOptionPane.YES_NO_OPTION
                );

                if (option == JOptionPane.OK_OPTION) {
                    comparerTasks();
                    cView.clear();
                    cModule.reset();

                    cView.getLoadButton().setEnabled(true);
                    cView.getMoveButton().setEnabled(true);

                    cView.getStateLabel().setText(translate("comparer.state.ready"));
                }
            }
        });
    }

    private void resetGalleryDistinctTasks() {
        // Todo: EDT safety

        // Get references
        GalleryView gView = view.getGalleryView();
        GalleryModule gModule = model.getGalleryModule();

        // Reset Tasks
        gModule.setMapObjects(new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                view.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

                try {
                    gModule.prepareComparer(
                        Arrays.stream(gView.getGalleryTable().getSelectedRows()).boxed().toList()
                    );
                } catch (IOException | InterruptedException | TimeoutException e) {
                    view.showErrorMessage(
                        translate("error.general.desc"),
                        translate("error.general.title"),
                        e
                    );

                    return null;
                }

                try {
                    gModule.compareAndExtract();
                } catch (IOException | ExecutionException e) {
                    view.showErrorMessage(
                        translate("error.general.desc"),
                        translate("error.general.title"),
                        e
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
                    translate("message.confirmation.duplicates_removal.desc"),
                    translate("message.confirmation.title"),
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
                    gModule.fileDelete();
                } catch (IOException e) {
                    view.showErrorMessage(
                        translate("error.general.desc"),
                        translate("error.general.title"),
                        e
                    );
                }

                return null;
            }

            @Override
            protected void done() {
                gView.getElementCount().setText(String.valueOf(gModule.getGalleryTableModel().getRowCount()));
                view.setCursor(Cursor.getDefaultCursor());
                gView.unlockModule();

                view.showInformationMessage(
                    translate("message.redundant_images.deleted.desc"),
                    translate("message.general.title")
                );
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
                    gModule.fileTransfer();
                } catch (IOException e) {
                    view.showErrorMessage(
                        translate("error.general.desc"),
                        translate("error.general.title"),
                        e
                    );
                }
                return null;
            }

            @Override
            protected void done() {
                gView.getElementCount().setText(String.valueOf(gModule.getGalleryTableModel().getRowCount()));
                view.setCursor(Cursor.getDefaultCursor());
                gView.unlockModule();

                view.showInformationMessage(
                    translate("message.redundant_images.moved.desc"),
                    translate("message.general.title")
                );

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
                    view.showErrorMessage(
                        translate("error.general.desc"),
                        translate("error.general.title"),
                        e
                    );

                    return null;
                }

                try {
                    gModule.saveToFile();
                } catch (IOException e) {
                    view.showErrorMessage(
                        translate("error.save.ioexception.desc"),
                        translate("error.general.title")
                    );
                }

                return null;
            }

            @Override
            protected void done() {
                view.showInformationMessage(
                    translate("message.unify_names.desc"),
                    translate("message.general.title")
                );

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
                    view.showErrorMessage(
                        translate("error.add.ioexception.desc"),
                        translate("error.general.title")
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
                gView.getGalleryTable().clearSelection();

                if (selected == null || selected.length == 0) {
                    view.showErrorMessage(
                        translate("error.delete.no_images.desc"),
                        translate("error.general.title")
                    );

                    return null;
                }

                selected = Arrays.stream(selected)
                        .map(i -> gModule.getTableRowSorter().convertRowIndexToModel(i))
                        .toArray();

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
                    view.showErrorMessage(
                        translate("error.save.ioexception.desc"),
                        translate("error.general.title")
                    );
                }

                return null;
            }

            @Override
            protected void done() {
                gView.unlockModule();
                view.setCursor(Cursor.getDefaultCursor());
                resetGalleryRemoveImagesTask();
            }
        });
    }
}