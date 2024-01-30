package Comparer;

import Comparer.Structures.ImageRecord;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

public class PictureComparer extends Comparer<ImageRecord> {
    public PictureComparer(List<File> sourceFiles, File sourceDirectory, File destDirectory) throws IOException {
        super(sourceFiles, sourceDirectory, destDirectory);
        super.acceptedTypes = ImageRecord.acceptedTypes;
        _setUp(sourceFiles, sourceDirectory, destDirectory);

        log("Picture Comparer initialized");
    }

    public PictureComparer() throws IOException {
        super();
        super.acceptedTypes = ImageRecord.acceptedTypes;

        log("Picture Comparer initialized");
    }

    public PictureComparer(File sourceDirectory, File destDirectory) throws IOException {
        this(null, sourceDirectory, destDirectory);
    }

    public PictureComparer(List<File> sourceFiles, File destDirectory) throws IOException {
        this(sourceFiles, null, destDirectory);
    }

    @Override
    public void map() {
        // Method

        log("Mapping files.");

        String pattern = super.generateTypePattern();

        HashMap<Long, ArrayList<ImageRecord>> map = new HashMap<>();

        sourceFiles.stream()
        .filter(file -> file.getName().matches(pattern))
        .forEach(
            file -> {
                try {
                    ImageRecord ir = new ImageRecord(file);
                    if(!map.containsKey(ir.getChecksum())) // calculating checksum
                        map.put(ir.getChecksum(), new ArrayList<>());
                    map.get(ir.getChecksum()).add(ir);
                    super.processedObjectCount++;
                    log(String.format("Processed %d images from %d total", processedObjectCount, totalObjectCount));
                } catch (IOException e) {
                    // skip that file
                    log(String.format("Skipping file: %s", file.getName()));
                }
            }
        );


        super.mappedObjects = map;
    }

    @Override
    public void compare() {
        log("Extracting duplicates");

        ArrayList<ImageRecord> duplicates = new ArrayList<>();

        mappedObjects.forEach(
            (k, v) -> {
                for (int i = 0; i < v.size(); i++)
                    if( i > 0 ) duplicates.add(v.get(i));
            }
        );

        super.duplicates = duplicates;
        log(String.format("Found %d duplicates from %d all images", duplicates.size(), totalObjectCount));
    }

    @Override
    public void move() {
        log("Moving duplicated images");
        String separator = File.separator;
        super.duplicates.forEach(
            ir -> {
                File f = ir.getFile();
                try {
                    Files.move(
                        f.toPath(),
                        Paths.get(destDirectory + separator + f.getName()),
                        StandardCopyOption.REPLACE_EXISTING
                    );
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        );
    }
}