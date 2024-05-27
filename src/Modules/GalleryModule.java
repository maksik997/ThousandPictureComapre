package Modules;

import javax.imageio.ImageIO;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class GalleryModule {

    private static final Path imageReferenceFilePath = Path.of(".", "resources", "gallery.tp");

    private final DefaultTableModel galleryModel;

    private final List<Path> images;


    public GalleryModule() throws IOException {
        // File Name, Size, Modification Date
        galleryModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        images = new ArrayList<>();
        if (Files.exists(imageReferenceFilePath))
            loadFromFile();
    }

    // Basic set of operations

    public void addImage(String path) throws IOException {
        Path filePath = Path.of(path);

        images.add(filePath);
        galleryModel.addRow(new String[]{
            filePath.toFile().getName(),
            String.valueOf(Files.size(filePath)),
            String.valueOf(Files.getLastModifiedTime(filePath))
        });

        saveToFile(images);
    }

    public void removeImage(String path) {
        Path filePath = Path.of(path);

        galleryModel.removeRow(images.indexOf(filePath));

        images.remove(filePath);
    }

    public void openImage(String path) throws IOException {
        Path filePath = Path.of(path);

        Desktop.getDesktop().open(filePath.toFile());
    }

    public void modifyName(String path, String newName) throws IOException {
        Path filePath = Path.of(path);

        int idx = images.indexOf(filePath);


        Path pointerPath = filePath.subpath(0, filePath.toString().lastIndexOf("/"));
        String extension = filePath.getFileName().toString().substring(filePath.toString().lastIndexOf("."));

        Path newPath = Paths.get(pointerPath.toString(), newName + "." + extension);
        images.set(idx, newPath);
        Files.move(filePath, newPath);

        galleryModel.setValueAt(newName + "." + extension, idx, 0);
    }

    // Special set of operations

    public void unifyNames() throws IOException {
        String pattern = "img";
        int i = 0;

        for (Path filePath : images) {
            modifyName(filePath.toString(), pattern + i);
        }
    }

    // todo more of this...

    private void loadFromFile() throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(imageReferenceFilePath)) {
            reader.lines().map(Path::of).forEach(images::add);
        }

        for (Path path : images) {
            galleryModel.addRow(new String[]{
                path.toFile().getName(),
                String.valueOf(Files.size(path)),
                String.valueOf(Files.getLastModifiedTime(path))
            });
        }
    }

    private static void saveToFile(List<Path> images) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(imageReferenceFilePath)) {
            List<String> toSave = images.stream()
                                        .map(Path::toAbsolutePath)
                                        .map(Path::toString)
                                        .toList();
            for (String image : toSave) {
                writer.write(image);
                writer.newLine();
            }
        }
    }
}
