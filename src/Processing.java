import DataActions.ImageRecord;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

public class Processing{
    private ArrayList<ImageRecord> allImages, duplicates;

    private File dir, destDir;

    public Processing() {
        this.allImages = new ArrayList<>();
        this.duplicates = new ArrayList<>();
    }

    public void _reset(){
        allImages = new ArrayList<>();
        duplicates = new ArrayList<>();
        dir = null;
    }

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
                    destDir == null ?
                    Paths.get("data" + separator + "duplicates" + separator + file.getName()):
                    Paths.get(destDir + file.getName()),
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

    public File getDestDir() {
        return destDir;
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

    public void setDestDir(File destDir) {
        this.destDir = destDir;
    }
}
