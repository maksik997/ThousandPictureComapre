import LocationView.ProcessingEvent;
import LocationView.ProcessingListener;
import LocationView.TaskListener;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EventObject;

public class Processing implements ProcessingListener {

    private ArrayList<File> files,
            duplicates;

    public Processing() {
        this.files = new ArrayList<>();
        this.duplicates = new ArrayList<>();
    }

    @Override
    public void actionPerformed(ProcessingEvent e) {
        File[] files = new File(e.getPath()).listFiles();

        if(files == null)
            return;

        Arrays.stream(files).filter(File::isFile).forEach(
            f -> {
                if (f.getName().matches(".*\\.jpg$|.*\\.png$"))
                    this.files.add(f);
            }
        );
        fireTaskListener();
    }


    // Started externally by View
    private final SwingWorker<Boolean, Void> processAllData = new SwingWorker<>() {
        @Override
        protected Boolean doInBackground() {
            setProgress(0);
            checkForDuplicates(files);
            setProgress(25);
            duplicates.forEach(
                f -> {
                    try {
                        Files.move( // Moving files takes a while tho
                            f.toPath(),
                            Paths.get("data/duplicates/" + f.getName()),
                            StandardCopyOption.ATOMIC_MOVE
                        );
                        setProgress(Math.min(getProgress()+1, 90));
                    } catch (IOException e) {
                        e.printStackTrace();
                        throw new RuntimeException(e);
                    }
                }
            );

            return true;
        }

        @Override
        protected void done() {
            super.done();
            setProgress(100);
        }
    };

    public SwingWorker<Boolean, Void> getProcessAllData() {
        return processAllData;
    }

    private void checkForDuplicates(ArrayList<File> data){
        File n, m;

        for (int i = 0, j = 1; i < data.size(); i++) {

            n = data.get(i);
            // Checked focus
            for (; j < data.size(); j++) {
                m = data.get(j);
                // Comparing
                try {
                    if(compareImg(
                            ImageIO.read(n),
                            ImageIO.read(m)
                    )){

                        if(!duplicates.contains(m))
                            duplicates.add(m);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            j = 1;
            j += i + 1;
        }
    }

    private boolean compareImg(BufferedImage img1, BufferedImage img2){
        // Check dimensions
        if (img1.getTileWidth() != img2.getTileWidth() || img1.getTileHeight() != img2.getTileHeight())
            return false;

        int height = img1.getTileHeight(), width = img1.getTileWidth();
        Color pixel1, pixel2;

        // Check every pixel
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                // Get RGB for both images (where blue with >> 0, green >> 8, red >> 16) and all & 0xff
                int rgb1 = img1.getRGB(j, i),
                        rgb2 = img2.getRGB(j, i);
                pixel1 = new Color((rgb1 >> 16) & 0xff, (rgb1 >> 8) & 0xff, (rgb1) & 0xff);
                pixel2 = new Color((rgb2 >> 16) & 0xff, (rgb2 >> 8) & 0xff, (rgb2) & 0xff);

                if(!pixel1.equals(pixel2))
                    return false;
            }
        }
        return true;
    }


    private final ArrayList<TaskListener> listeners = new ArrayList<>();

    public void addTaskListeners(TaskListener l){
        listeners.add(l);
    }

    public void fireTaskListener(){
        for (TaskListener l : listeners)
            l.actionPerformed(new EventObject(this));
    }

}
