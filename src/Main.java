import UiComponents.Utility;
import UiViews.LoadingFrame;
import com.formdev.flatlaf.FlatDarculaLaf;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Main {
    public static void main(String[] args) {
        FlatDarculaLaf.setup();
        setUIManagerProperties();

        LoadingFrame frame = new LoadingFrame();
        SwingUtilities.invokeLater(() -> frame.setVisible(true));

        try {
            View view = new View();
            Model model = initModelAsync();
            Controller controller = new Controller(view, model);

            frame.dispose();
            SwingUtilities.invokeLater(() -> view.setVisible(true));

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    private static void setUIManagerProperties(){
        UIManager.put( "TextComponent.arc", 10 );
        UIManager.put( "Component.focusWidth", 1 );
        UIManager.put( "Button.innerFocusWidth", 0 );
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

    /*static class LoadingFrame extends JFrame {
        public LoadingFrame() {
            JLabel label = new JLabel("Loading... Please wait...");
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setFont(Utility.fontBigHelveticaBold);
            this.add(label);

            this.setUndecorated(true);
            this.setMinimumSize(new Dimension(800, 650));
            this.pack();
            this.setLocationRelativeTo(null);
            this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        }
    }*/
}


