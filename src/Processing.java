import DataActions.ImageRecord;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

public class Processing{
    private ArrayList<ImageRecord> allImages, duplicates;

    private File dir;

    public Processing() {
        this.allImages = new ArrayList<>();
        this.duplicates = new ArrayList<>();
    }

    //deprecated
    /*// This worker is supposed to load all file
    private final SwingWorker<Void, Void> fileLoadingWorker = new SwingWorker<>() {
        @Override
        protected Void doInBackground() {
            setProgress(0);
            try {
                allImages = ImageRecord.getAllImages(dir);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return null;
        }

        @Override
        protected void done() {
            super.done();
        }
    };

    public SwingWorker<Void, Void> getFileLoadingWorker() {
        return fileLoadingWorker;
    }

    // This worker is supposed to find all ImageReaders duplicates using checksums
    private final SwingWorker<Void, Void> lookForDuplicatesWorker = new SwingWorker<Void, Void>() {
        @Override
        protected Void doInBackground() {
            setProgress(0);
            duplicates = compareAllImages();
            return null;
        }

        @Override
        protected void done() {
            super.done();
        }
    };

    // This worker is supposed to move all files to duplicates folder in project folder
    public SwingWorker<Void, Void> getLookForDuplicatesWorker() {
        return lookForDuplicatesWorker;
    }

    private final SwingWorker<Void, Void> moveFilesWorker = new SwingWorker<>() {
        @Override
        protected Void doInBackground() throws Exception {
            fileTransfer();
            return null;
        }

        @Override
        protected void done() {
            super.done();
        }
    };

    public SwingWorker<Void, Void> getMoveFilesWorker() {
        return moveFilesWorker;
    }
*/

    // This method compare all images checksum
    public ArrayList<ImageRecord> compareAllImages(){
        ImageRecord n,m;
        ArrayList<ImageRecord> duplicates = new ArrayList<>();

        for (int i = 0, j = 1; i < allImages.size(); i++) {

            n = allImages.get(i);
            for (; j < allImages.size(); j++) {
                m = allImages.get(j);

                if(n.checksumEquals(m)){
                    if (!duplicates.contains(m))
                        duplicates.add(m);
                }
            }
            j = 1;
            j += i + 1;
        }

        return duplicates;
    }

    public void fileTransfer(){
        String separator = File.separator;
        duplicates.forEach(imageRecord -> {
            File file = imageRecord.getFile();
            try {
                Files.move(
                    file.toPath(),
                    Paths.get("data" + separator + "duplicates" + separator + file.getName()),
                    StandardCopyOption.ATOMIC_MOVE
                );
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }


    public ArrayList<ImageRecord> getAllImages() {
        return allImages;
    }

    public ArrayList<ImageRecord> getDuplicates() {
        return duplicates;
    }

    public File getDir() {
        return dir;
    }

    public void setAllImages(ArrayList<ImageRecord> allImages) {
        this.allImages = allImages;
    }

    public void setDuplicates(ArrayList<ImageRecord> duplicates) {
        this.duplicates = duplicates;
    }

    public void setDir(File dir) {
        this.dir = dir;
    }
}
