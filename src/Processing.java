import DataActions.ImageRecord;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;

public class Processing{
    private ArrayList<ImageRecord> duplicates;

    private HashMap<Long, ArrayList<ImageRecord>> mappedImages;

    private int imagesCount;

    private File dir, destDir;

    public Processing() {
        _reset();
    }

    public void _reset(){
        imagesCount = 0;
        mappedImages = new HashMap<>();
        duplicates = new ArrayList<>();
        dir = null;
    }

    // This method compare all images checksum
    public ArrayList<ImageRecord> compareAllImages(){
        // update v3-0
        ArrayList<ImageRecord> duplicates = new ArrayList<>();

        mappedImages.forEach(
            (key, val) -> {
                for (int i = 0; i < val.size(); i++) {
                    if(i > 0) duplicates.add(val.get(i));
                }
            }
        );
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
                    Paths.get(destDir + separator + file.getName()),
                    StandardCopyOption.REPLACE_EXISTING
                );
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
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

    public void setDuplicates(ArrayList<ImageRecord> duplicates) {
        this.duplicates = duplicates;
    }

    public void setDir(File dir) {
        this.dir = dir;
    }

    public void setDestDir(File destDir) {
        this.destDir = destDir;
    }

    public HashMap<Long, ArrayList<ImageRecord>> getMappedImages() {
        return mappedImages;
    }

    public void setMappedImages(HashMap<Long, ArrayList<ImageRecord>> mappedImages) {
        this.mappedImages = mappedImages;
        mappedImages.forEach(
            (k, v) -> imagesCount += v.size()
        );
    }

    public int getImagesCount() {
        return imagesCount;
    }

    public void setImagesCount(int imagesCount) {
        this.imagesCount = imagesCount;
    }
}
