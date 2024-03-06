package Modules;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class GalleryModule {

    private final static String[] tagNames = {
        "Unordered", "Nature", "Architecture", "City", "People", "Animals", "Landscape", "Every Day Life",
        "Travel", "Food", "Sports", "Technology", "Music", "Family", "Art", "Universe"
    };

    // This field holds a metaFilePath for all saved images in a gallery
    private static final Path metaFilePath = Path.of(
        System.getProperty("user.dir") + File.separator + "meta" + File.separator + "gallery.tcp"
    );
    private static final Path metaTagPath = Path.of(
        System.getProperty("user.dir") + File.separator + "meta" + File.separator + "tag.tcp"
    );

    private final List<String> tags;

    private final Map<String, List<File>> loadedImages;

    private int imageCount;

    public GalleryModule() throws IOException {
        this.imageCount = 0;
        tags = new ArrayList<>(); // Create plain tag list
        loadedImages = new HashMap<>();

        if (!Files.exists(metaTagPath)) {
            createTagCollection();
        } else {
            try (ObjectInputStream is = new ObjectInputStream(
                new FileInputStream(String.valueOf(metaTagPath))
            )) {
                Object obj = is.readObject();
                if (obj instanceof ArrayList) {
                    @SuppressWarnings("unchecked") // Not good, but working solution :P
                    ArrayList<String> tmpTagList = (ArrayList<String>) obj;

                    tags.addAll(tmpTagList);
                }
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("ERR2..."); // todo ...
            }

            if (tags.isEmpty())
                createTagCollection();
        }

        if (Files.exists(metaFilePath)) {
            try (ObjectInputStream is = new ObjectInputStream(
                new FileInputStream(String.valueOf(metaFilePath))
            )) {
                Object obj = is.readObject();
                if (obj instanceof HashMap<?,?>) {
                    @SuppressWarnings("unchecked")
                    HashMap<String, ArrayList<File>> tmpFileList =
                            (HashMap<String, ArrayList<File>>) obj;

                    loadedImages.putAll(tmpFileList);
                }
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("ERR..."); // todo ...
            }
        }
        if(loadedImages.isEmpty()) {
            for (String tag : tags) {
                loadedImages.put(tag, new ArrayList<>());
            }
        }
    }

    private void createTagCollection() throws IOException {
        if (Files.exists(metaTagPath))
            Files.delete(metaTagPath);

        Files.createFile(metaTagPath);
        tags.addAll(List.of(tagNames));

        try (ObjectOutputStream os = new ObjectOutputStream(
            new FileOutputStream(String.valueOf(metaTagPath))
        )) {
            os.writeObject(tags);
        }
    }

    private void createFileDump() throws IOException {
        if (Files.exists(metaFilePath))
            Files.delete(metaFilePath);

        Files.createFile(metaFilePath);

        try(ObjectOutputStream os = new ObjectOutputStream(
            new FileOutputStream(String.valueOf(metaFilePath))
        )) {
            os.writeObject(loadedImages);
        }
    }

    public void addImages(List<File> images) throws IOException {
        // add 'n save
        loadedImages.get("Unordered").addAll(images);

        createFileDump();
    }

    public void attachTag(File image, String... tags) {
        for (String tag : tags) {
            if (loadedImages.get(tag).contains(image)) continue;

            loadedImages.get(tag).add(image);
        }

        loadedImages.get("Unordered").remove(image);
    }

}
