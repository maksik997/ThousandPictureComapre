/* todo
    Check if pics are available,
*/

package Modules;

import Modules.Gallery.Entry;
import Modules.Gallery.GalleryTableModel;
import pl.magzik.PictureComparer;
import pl.magzik.Structures.Record;

import javax.swing.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

public class GalleryModule {

    private static final Path imageReferenceFilePath = Path.of(".", "resources", "gallery.tp");

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    //private final DefaultTableModel galleryModel;

    private final GalleryTableModel galleryTableModel;

    //private final List<Path> images;

    private final PictureComparer pc;

    private boolean isLocked;

    private SwingWorker<Void, Void> mapObjects, transferObjects, removeObjects, unifyNames;

    // Helpers for exception handling while loading.
    private boolean massAction, isFirstTime;

    public GalleryModule() throws IOException {
        isLocked = false;

        galleryTableModel = new GalleryTableModel();

        // File Name, Size, Modification Date
//        galleryModel = new DefaultTableModel() {
//            @Override
//            public boolean isCellEditable(int row, int column) {
//                return column == 0;
//            }
//        };
//
//        galleryModel.addColumn("File name");
//        galleryModel.addColumn("Size");
//        galleryModel.addColumn("Modification time");

        this.pc = new PictureComparer();
//        images = new ArrayList<>();

        if (Files.exists(imageReferenceFilePath)) {
            loadFromFile();
        }

//        galleryModel.addTableModelListener(e -> {
//            // This operation will lock a gallery.
//            if (e.getType() != TableModelEvent.UPDATE) return;
//
//            if (isLocked()) {
//                JOptionPane.showMessageDialog(
//                    null,
//                    String.format("You should wait until all names was updated.%nTry again after task is finished!"),
//                    "Information:",
//                    JOptionPane.INFORMATION_MESSAGE
//                );
//                return;
//            }
//
//            int r = e.getFirstRow();
//            int c = e.getColumn();
//
//            if (c != 0) return;
//
//            String newValue = (String) galleryModel.getValueAt(r, c);
//            Path file = images.get(r);
//
//            try {
//                modifyName(file.toString(), newValue);
//
//                repairModel(); // Eh... That's unfortunately necessary... It's costly...
//
//                saveToFile();
//            } catch (IOException ex) {
//                throw new RuntimeException(ex); // do something with this :)
//            }
//        });

        resetModuleTasks();
    }

    // Comparer interaction
    public void prepareComparer(String destPath, List<Integer> indexes) throws FileNotFoundException {
        // Prepares Picture Comparer with destination path and source path

        List<Path> toCheck = new ArrayList<>();
        for (int i = 0; i < galleryTableModel.getImages().size(); i++) {
            if (indexes.contains(i))
                toCheck.add(galleryTableModel.getImages().get(i).getPath());
        }

        pc._setUp(
            new File(destPath),
            toCheck.stream()
                .map(Path::toFile)
                .toList()
        );
    }

    public void compare() {
        // Finds all redundant images
        // Do not call before setUp

        pc.map();
        pc.compare();
    }

    public void removeRedundant() throws IOException {
        performReduction();
        // Removes all the redundant images
        AtomicBoolean deletedAll = new AtomicBoolean(true);

        pc.getDuplicates().stream()
                .map(Record::getFile)
                .map(File::toPath)
                .map(p -> {
                    try {
                        Files.deleteIfExists(p);
                    } catch (IOException e) {
                        return false;
                    }
                    return true;
                })
                .filter(b -> !b)
                .findAny()
                .ifPresent(_ -> deletedAll.set(false));
        if (!deletedAll.get())
            throw new IOException("Couldn't delete all files.");
    }

    public void moveRedundant() throws IOException {
        performReduction();
        // Moves all the redundant images
        pc.move();
    }

    // Basic set of operations

    public void addImage(String... paths) throws IOException {
        addImage(Arrays.asList(paths));
    }

    public void addImage(List<String> entries) throws IOException {
        // This method could take awhile,
        // I should probably move it to some worker.
        // But that's a todo for now.

        for (String e : entries) {
            String[] split = e.split("->");

            Path filePath = Path.of(split[0]);

            if (!Files.exists(filePath))
                continue;

            if (filePath.toFile().isDirectory()) {
                addImage(filePath.toFile().list());
            } else {
                if (!pc.filePredicate(filePath.toFile())) continue;

                Set<String> tags = split.length == 1 ? Set.of() : Set.of(split[1].split(","));

                Entry entry = new Entry(filePath, tags);

                galleryTableModel.addEntry(entry);
//                images.add(filePath);
//                galleryModel.addRow(new String[]{
//                        filePath.toFile().getName(),
//                        getKilobytes(Files.size(filePath)),
//                        getFormattedDate(Files.getLastModifiedTime(filePath))
//                });
            }
        }

        saveToFile(galleryTableModel.getImages());
    }

    public void removeImage(int idx) {
//        Path filePath = images.get(idx);
//
//        galleryModel.removeRow(idx);
//        images.remove(filePath);
        galleryTableModel.removeEntry(idx);
    }

    public void openImage(int idx) throws IOException {
//        Path filePath = images.get(idx);
//
//        Desktop.getDesktop().open(filePath.toFile());
        galleryTableModel.openEntry(idx);
    }

    public void modifyName(int idx, String newName) throws IOException {
//        Path filePath = Path.of(path);
//
//        int idx = images.indexOf(filePath);
//
//        File oldFile = new File(path);
//
//        Path parentPath = filePath.getParent();
//
//        File newFile = new File(parentPath.toString(), newName);
//
//        if (oldFile.equals(newFile)) return;
//
//        Files.move(oldFile.toPath(), newFile.toPath());
//
//        images.set(idx, newFile.toPath());


        galleryTableModel.modifyName(idx, newName);
    }

    // Special set of operations

    private void unifyNames() throws IOException {
//        String pattern = "tp_img_";
//        int i = 0;
//
//        for (Path filePath : images) {
//            String ext = filePath.toString().substring(filePath.toString().lastIndexOf("."));
//            modifyName(filePath.toString(), pattern + ++i + "_" + System.currentTimeMillis() + ext);
//        }
        galleryTableModel.unifyNames();
    }

//    private void repairModel() throws IOException {
//        // This method will clear all models, and rewrite it with an image list.
//        // This method should be called if many operations were invoked outside EDT.
//        // Or if there is a risk that you'll find yourself inside a call loop.
//
//        while (galleryModel.getRowCount() > 0) galleryModel.removeRow(0);
//
//        for (Path p : images) {
//            String name = p.toFile().getName();
//            String kb = getKilobytes(Files.size(p));
//            String date = getFormattedDate(Files.getLastModifiedTime(p));
//
//            galleryModel.addRow(new String[]{name, kb, date});
//        }
//    }

    // todo more of this...

    // Other important methods...

    private void performReduction() throws IOException {
        if (pc.getDuplicates().isEmpty())
            return;

//        List<Integer> indexes = new ArrayList<>();
        List<Path> files = pc.getDuplicates().stream().map(Record::getFile).map(File::toPath).toList();
//        for (Path f : files)
//            indexes.add(images.indexOf(f));
//
//        indexes.stream().map(images::get).forEach(images::remove);
//
//        indexes.forEach(galleryModel::removeRow);

        galleryTableModel.reduction(files);

        saveToFile();
    }

    private void resetModuleTasks() {
        // This worker will be used to safely update a gallery model, after a task is finished
        unifyNames = new SwingWorker<>() {
            @Override
            protected Void doInBackground()  {
                isLocked = true;
                try {
                    unifyNames();
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(
                        null,
                        String.format("Error: %s", e.getMessage()),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                }

                return null;
            }

            @Override
            protected void done() {
                try {
                    //repairModel();
                    saveToFile();
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(
                        null,
                        "Couldn't repair model or save, please restart the app!",
                        "Error:",
                        JOptionPane.ERROR_MESSAGE
                    );
                }

                JOptionPane.showMessageDialog(
                    null,
                    String.format("Names are unified now.%nYay!"),
                    "Information:",
                    JOptionPane.INFORMATION_MESSAGE
                );

                isLocked = false;
                resetModuleTasks();
            }
        };
    }

    private void loadFromFile() throws IOException {
        Function<String, Entry> separateLine = l -> {
            // Line comes in format: path->tag[,tag]*
            // Split the line.
            String[] split = l.split("->");

            // Create a path from the first part of split.
            Path p = Path.of(split[0]);

            // Add tags to a Set
            Set<String> tags = split.length == 1 ? Set.of() : Set.of(split[1].split(","));

            // Create entry
            try {
                return new Entry(p, tags);
            } catch (IOException e) {
                if (!massAction) {
                    JOptionPane.showMessageDialog(
                        null,
                        String.format("Couldn't find image: %s.%nSkipping this file. Consider adding it again when you find it.", p),
                        "Error:",
                        JOptionPane.ERROR_MESSAGE
                    );
                }

                if (isFirstTime) {
                    int i = JOptionPane.showConfirmDialog(
                        null,
                        String.format("Do you want to skip all images that app couldn't locate?%nOr you want to see it every single time?"),
                        "Question?",
                        JOptionPane.YES_NO_OPTION
                    );
                    if (i == JOptionPane.NO_OPTION)
                        setMassAction();
                    setFirstTime();
                }
                return null;
            }
        };

        try (BufferedReader reader = Files.newBufferedReader(imageReferenceFilePath)) {
            reader.lines()
                    .map(separateLine)
                    .filter(Objects::nonNull)
                    //.map(Path::of)
                    .filter(e -> Files.exists(e.getPath()))
                    //.map(Path::toFile)
                    .filter(e -> pc.filePredicate(e.getPath().toFile()))
                    //.map(File::toPath)
                    .forEach(galleryTableModel::addEntry);
        }

//        for (Path path : images) {
//            galleryModel.addRow(new String[]{
//                path.toFile().getName(),
//                getKilobytes(Files.size(path)),
//                getFormattedDate(Files.getLastModifiedTime(path))
//            });
//        }

        // To clear any unreachable images.
        // todo It should be for now solution.
        //  Probably app should ask user about that.
        saveToFile(galleryTableModel.getImages());
    }

    public void saveToFile() throws IOException {
        saveToFile(galleryTableModel.getImages());
    }

    private static void saveToFile(List<Entry> images) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(imageReferenceFilePath)) {
            List<String> toSave = images.stream()
                    .map(e -> String.format("%s->%s", e.getPath(), String.join(",", e.getTags())))
//                                        .map(Path::toAbsolutePath)
//                                        .map(Path::toString)
                                        .toList();

            for (String image : toSave) {
                writer.write(image);
                writer.newLine();
            }
        }
    }

    // Getters

//    public DefaultTableModel getGalleryModel() {
//        return galleryModel;
//    }


    public GalleryTableModel getGalleryTableModel() {
        return galleryTableModel;
    }

    private String getKilobytes(double bytes) {
        return ((int)(bytes/(1024))) == 0 ?
                    String.format("%.2f KB",(bytes/(1024))) :
                    String.format("%d KB",(int)(bytes/(1024)));
    }

    private String getFormattedDate(FileTime fileTime) {
        return dateFormat.format(fileTime.toMillis());
    }

    public SwingWorker<Void, Void> getMapObjects() {
        return mapObjects;
    }

    public SwingWorker<Void, Void> getTransferObjects() {
        return transferObjects;
    }

    public SwingWorker<Void, Void> getRemoveObjects() {
        return removeObjects;
    }

    public SwingWorker<Void, Void> getUnifyNames() {
        return unifyNames;
    }

    public boolean isLocked() {
        return isLocked;
    }

    private void setFirstTime() {
        isFirstTime = false;
    }

    private void setMassAction() {
        this.massAction = true;
    }

    // Resetting tasks...

    public void resetTasks(SwingWorker<Void, Void> mapObjects, SwingWorker<Void, Void> transferObjects, SwingWorker<Void, Void> removeObjects) {
        this.mapObjects = mapObjects;
        this.transferObjects = transferObjects;
        this.removeObjects = removeObjects;
    }
}
