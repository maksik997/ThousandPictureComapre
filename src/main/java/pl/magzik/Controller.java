package pl.magzik;

import pl.magzik.controllers.ComparerController;
import pl.magzik.controllers.MenuController;
import pl.magzik.controllers.SettingsController;
import pl.magzik.ui.interfaces.TranslationInterface;
import pl.magzik.modules.GalleryModule;
import pl.magzik.ui.components.Utility;
import pl.magzik.ui.views.*;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TableModelEvent;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
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
        initGalleryView();

        // TODO SOMETHING WITH THIS
        MenuController menuController = new MenuController(view.getMenuView(), view);
        ComparerController comparerController = new ComparerController(view.getComparerView(), model.getComparerModule(), this, view, view);
        SettingsController settingsController = new SettingsController(view.getSettingsView(), model.getSettingsModule(), model.getGalleryModule(), this, view, model.getComparerModule(), model.getGalleryModule());
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
                .getBackButton()
                .addActionListener(_ -> view.toggleScene(Utility.Scene.MENU));
            }
        });
    }

    private void initGalleryView() {
        GalleryView gView = view.getGalleryView();
        GalleryModule gModule = model.getGalleryModule();

        // Initialize FileChooser in Gallery View
        gView.setFileChooser(this);

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

        gView.getElementCountLabel().setText(String.valueOf(gModule.getGalleryTableModel().getRowCount()));

        // Tasks
        resetGalleryDistinctTasks();
        resetGalleryUnifyNamesTask();
        resetGalleryAddImagesTask();
        resetGalleryRemoveImagesTask();
    }

    // WILL LAND IN GALLERY CONTROLLER
    public void addImages(List<String> list) {
        try {
            model.getGalleryModule().addImage(list);
        } catch (IOException e) {
            view.showErrorMessage(
                translate("error.add.ioexception.desc"),
                translate("error.general.title")
            );
        }
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
                view.useCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

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
                view.useCursor(Cursor.getDefaultCursor());

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
                view.useCursor(
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
                gView.getElementCountLabel().setText(String.valueOf(gModule.getGalleryTableModel().getRowCount()));
                view.useCursor(Cursor.getDefaultCursor());
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
                view.useCursor(
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
                gView.getElementCountLabel().setText(String.valueOf(gModule.getGalleryTableModel().getRowCount()));
                view.useCursor(Cursor.getDefaultCursor());
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
                view.useCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

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
                view.useCursor(Cursor.getDefaultCursor());
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
                view.useCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

                if (!gView.getFileChooser().perform()) return null;
                /*List<String> paths = GalleryView.getPaths();

                try {
                    gModule.addImage(paths);
                } catch (IOException ex) {
                    view.showErrorMessage(
                        translate("error.add.ioexception.desc"),
                        translate("error.general.title")
                    );
                }*/

                return null;
            }

            @Override
            protected void done() {
                gView.getElementCountLabel().setText(String.valueOf(gModule.getGalleryTableModel().getRowCount()));
                gView.unlockModule();
                view.useCursor(Cursor.getDefaultCursor());
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
                view.useCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

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
                view.useCursor(Cursor.getDefaultCursor());
                resetGalleryRemoveImagesTask();
            }
        });
    }
}
