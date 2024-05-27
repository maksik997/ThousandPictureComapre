/* todo
    Check if pics are available,
*/

package Modules;

import Exceptions.InvalidTypeException;
import pl.magzik.PictureComparer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class GalleryModule {

    private static final Path imageReferenceFilePath = Path.of(".", "resources", "gallery.tp");

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    private final DefaultTableModel galleryModel;

    private final List<Path> images;

    private final PictureComparer pc;

    private SwingWorker<Void, Void> mapObjects, transferObjects, removeObjects;

    public GalleryModule() throws IOException {
        // File Name, Size, Modification Date
        galleryModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        galleryModel.addColumn("File name");
        galleryModel.addColumn("Size");
        galleryModel.addColumn("Modification time");

        this.pc = new PictureComparer();

        images = new ArrayList<>();
        if (Files.exists(imageReferenceFilePath))
            loadFromFile();
    }

    // Basic set of operations

    public void addImage(String path) throws IOException, InvalidTypeException {
        Path filePath = Path.of(path);

        if (!Files.exists(filePath) || !pc.filePredicate(filePath.toFile()))
            throw new InvalidTypeException("Given file isn't a image.");

        images.add(filePath);
        galleryModel.addRow(new String[]{
            filePath.toFile().getName(),
            getKilobytes(Files.size(filePath)),
            getFormattedDate(Files.getLastModifiedTime(filePath))
        });

        saveToFile(images);
    }

    public void removeImage(int idx) {
        Path filePath = images.get(idx);

        galleryModel.removeRow(idx);
        images.remove(filePath);
    }

    public void openImage(String path) throws IOException {
        Path filePath = Path.of(path);

        Desktop.getDesktop().open(filePath.toFile());
    }

    public void modifyName(String path, String newName) throws IOException {
        Path filePath = Path.of(path);

        int idx = images.indexOf(filePath);

        File oldFile = new File(path);

        Path parentPath = filePath.getParent();
        String extension = path.substring(path.lastIndexOf('.'));

        File newFile = new File(parentPath.toString(), newName + "." + extension);

        if (!oldFile.renameTo(newFile))
            throw new IOException("Could not rename " + oldFile + " to " + newFile);

        images.set(idx, newFile.toPath());
        galleryModel.setValueAt(newFile.getName(), idx, 0);
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
            reader.lines()
                    .map(Path::of)
                    .filter(Files::exists)
                    .map(Path::toFile)
                    .filter(pc::filePredicate)
                    .map(File::toPath)
                    .forEach(images::add);
        }

        for (Path path : images) {
            galleryModel.addRow(new String[]{
                path.toFile().getName(),
                getKilobytes(Files.size(path)),
                getFormattedDate(Files.getLastModifiedTime(path))
            });
        }

        // To clear any unreachable images.
        // todo It should be for now solution.
        //  Probably app should ask user about that.
        saveToFile(images);
    }

    public void saveToFile() throws IOException {
        saveToFile(images);
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

    public DefaultTableModel getGalleryModel() {
        return galleryModel;
    }

    private String getKilobytes(double bytes) {
        return String.format("%d KB",(int)(bytes/(1024)));
    }

    private String getFormattedDate(FileTime fileTime) {
        return dateFormat.format(fileTime.toMillis());
    }

    public void resetTasks(SwingWorker<Void, Void> mapObjects, SwingWorker<Void, Void> transferObjects, SwingWorker<Void, Void> removeObjects) {
        this.mapObjects = mapObjects;
        this.transferObjects = transferObjects;
        this.removeObjects = removeObjects;
    }
}
