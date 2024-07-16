import Modules.Gallery.GalleryTableModel;
import UiViews.LoadingFrame;
import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Main {
    public static void main(String[] args) {
        FlatDarculaLaf.setup();
        setUIManagerProperties();

        Locale locale = Locale.getDefault(); // Connect with settings.
        ResourceBundle resources = ResourceBundle.getBundle("localization", locale);

        LoadingFrame loadingFrame = new LoadingFrame();
        updateComponents(loadingFrame.getContentPane(), resources);

        SwingUtilities.invokeLater(() -> loadingFrame.setVisible(true));

        try {
            Model model = new Model();

            // Take valid locale and update resource bundle
            locale = Locale.forLanguageTag(model.getSettingsModule().getSetting("language"));
            resources = ResourceBundle.getBundle("localization", locale);

            // Setup theme
            if (model.getSettingsModule().getSetting("theme").equals("dark")) {
                FlatDarculaLaf.setup();
            } else {
                FlatLightLaf.setup();
            }

            View view = initView(resources);
            Controller controller = new Controller(view, model, resources);

            updateAfterwardsComponents(model, resources);

            loadingFrame.dispose();
            SwingUtilities.invokeLater(() -> view.setVisible(true));

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    private static void setUIManagerProperties(){
        UIManager.put("TextComponent.arc", 10);
        UIManager.put("Component.focusWidth", 1);
        UIManager.put("Button.innerFocusWidth", 0);
    }

    private static Model initModelAsync() {
        AtomicReference<Model> model = new AtomicReference<>();
        Lock lock = new ReentrantLock();
        Condition cond = lock.newCondition();

        new Thread(() -> {
            try {
                model.set(new Model());
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }

            if (!model.get().isLoaded()) {
                System.out.println("Error: model is not loaded");
                System.exit(3);
            }

            lock.lock();
            try{
                cond.signalAll();
            } finally {
                lock.unlock();
            }
        }).start();

        lock.lock();
        try {
            cond.await();
        } catch (InterruptedException e) {
            System.out.println("Interrupted while loading...");
            System.exit(2);
        } finally {
            lock.unlock();
        }

        return model.get();
    }

    private static View initView(ResourceBundle resources) throws IOException {
        View view = new View();
        view.setTitle(resources.getString(view.getTitle()));

        // Translate file choosers:
        JFileChooser[] fcs = {
            view.getComparerView().getUiPath().getFileChooser(),
            view.getSettingsView().getDestinationFileChooser(),
            view.getGalleryView().getFileChooser()
        };

        for (JFileChooser fc : fcs) {
              String titleKey = fc.getDialogTitle();
              if (titleKey != null && resources.containsKey(titleKey)) {
                  fc.setDialogTitle(resources.getString(titleKey));
              }
              String approveButtonKey = fc.getApproveButtonText();
              if (approveButtonKey != null && resources.containsKey(approveButtonKey)) {
                  fc.setApproveButtonText(resources.getString(approveButtonKey));
              }
        }

        for(JPanel p : view.getScenes()) updateComponents(p, resources);
        return view;
    }

    private static void updateComponents(Container container, ResourceBundle resources) {
        String key;
        for (Component component : container.getComponents()) {
            if (component instanceof JComponent c) {
                if (c.getBorder() instanceof TitledBorder border) {
                    key = border.getTitle();
                    if (key != null && resources.containsKey(key))
                        border.setTitle(resources.getString(key));
                }
            }

            if (component instanceof JLabel label) {
                key = label.getText();
                if (key != null && resources.containsKey(key))
                    label.setText(resources.getString(key));
            } else if (component instanceof JButton button) {
                key = button.getText();
                if (key != null && resources.containsKey(key))
                    button.setText(resources.getString(key));
            } else if (component instanceof JCheckBox checkBox) {
                key = checkBox.getText();
                if (key != null && resources.containsKey(key))
                    checkBox.setText(resources.getString(key));
            } else if (component instanceof Container) {
                if (component instanceof JTabbedPane tabbedPane) {
                    for (int i = 0; i < tabbedPane.getTabCount(); i++) {
                        key = tabbedPane.getTitleAt(i);
                        if (key != null && resources.containsKey(key))
                            tabbedPane.setTitleAt(i, resources.getString(key));
                    }
                }

                updateComponents((Container) component, resources);
            }
        }
    }

    private static void updateAfterwardsComponents(Model model, ResourceBundle resources) {
        GalleryTableModel gtm = model.getGalleryModule().getGalleryTableModel();
        String key;
        for (int i = 0; i < gtm.getColumnCount(); i++) {
            key = gtm.getColumnName(i);
            if (key != null && resources.containsKey(key)) gtm.setColumnName(i, resources.getString(key));
        }
        gtm.refresh();
    }
}


