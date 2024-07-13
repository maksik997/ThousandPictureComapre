package Modules;

import Modules.Gallery.Entry;
import Modules.Gallery.GalleryTableModel;
import pl.magzik.PictureComparer;
import pl.magzik.Structures.Record;

import javax.swing.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Stream;

public class GalleryModule {

    private static final Path imageReferenceFilePath = Path.of(".", "resources", "gallery.tp");

    private final GalleryTableModel galleryTableModel;

    private final PictureComparer pc;

    private SwingWorker<Void, Void> mapObjects, transferObjects, removeObjects, unifyNames, addImages, removeImages;

    // Helpers for exception handling while loading.
    private boolean massAction, isFirstTime;

    public GalleryModule() throws IOException {
        galleryTableModel = new GalleryTableModel();

        this.pc = new PictureComparer();
        if (Files.exists(imageReferenceFilePath)) {
            loadFromFile();
        }
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
        if (entries.isEmpty()) return;
        else if (entries.size() == 1) {
            Path p = Path.of(entries.getFirst());

            if (!Files.exists(p)) return;

            if (Files.isDirectory(p)) {
                addImage(
                    Arrays.stream(
                        Objects.requireNonNull(p.toFile().list())
                    )
                    .map(s -> Path.of(p.toString(), s))
                    .map(Path::toString)
                    .toList()
                );
            }
            else {
                if (!pc.filePredicate(p.toFile())) return;

                galleryTableModel.addEntry(new Entry(p));
            }
        } else {
            // Directories
            addImage(
                entries.stream()
                .map(Path::of)
                .filter(Files::exists)
                .filter(Files::isDirectory)
                .map(Path::toFile)
                .map(File::list)
                .flatMap(Stream::of)
                .toList()
            );

            // Files
            try {
                galleryTableModel.addAllEntries(
                    entries.stream()
                    .map(Path::of)
                    .filter(Files::exists)
                    .filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .filter(pc::filePredicate)
                    .map(File::toPath)
                    .map(p -> {
                        try {
                            return new Entry(p);
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    })
                    .toList()
                );
            } catch (UncheckedIOException e) {
                throw e.getCause();
            }
        }

        saveToFile(galleryTableModel.getImages());
    }

    public void removeImage(int idx) {
        galleryTableModel.removeEntry(idx);
    }

    public void deleteImage(int idx) throws IOException {
        galleryTableModel.deleteImage(idx);
    }

    public void openImage(int idx) throws IOException {
        galleryTableModel.openEntry(idx);
    }

    // Special set of operations

    public void unifyNames() throws IOException {
        galleryTableModel.unifyNames();
    }

    // todo more of this...

    // Other important methods...

    private void performReduction() throws IOException {
        if (pc.getDuplicates().isEmpty())
            return;

        List<Path> files = pc.getDuplicates().stream().map(Record::getFile).map(File::toPath).toList();
        galleryTableModel.reduction(files);

        saveToFile();
    }

    private void loadFromFile() throws IOException {
        Function<String, Entry> separateLine = l -> {
            // Create a path
            Path p = Path.of(l);

            // Create entry
            try {
                return new Entry(p);
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
                    .filter(e -> Files.exists(e.getPath()))
                    .filter(e -> pc.filePredicate(e.getPath().toFile()))
                    .forEach(galleryTableModel::addEntry);
        }

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
                    .map(Entry::getPath)
                    .map(Path::toString)
                    .toList();

            for (String image : toSave) {
                writer.write(image);
                writer.newLine();
            }
        }
    }

    public GalleryTableModel getGalleryTableModel() {
        return galleryTableModel;
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

    public SwingWorker<Void, Void> getAddImages() {
        return addImages;
    }

    public SwingWorker<Void, Void> getRemoveImages() {
        return removeImages;
    }

    public void setUnifyNames(SwingWorker<Void, Void> unifyNames) {
        this.unifyNames = unifyNames;
    }

    public void setMapObjects(SwingWorker<Void, Void> mapObjects) {
        this.mapObjects = mapObjects;
    }

    public void setTransferObjects(SwingWorker<Void, Void> transferObjects) {
        this.transferObjects = transferObjects;
    }

    public void setRemoveObjects(SwingWorker<Void, Void> removeObjects) {
        this.removeObjects = removeObjects;
    }

    public void setAddImages(SwingWorker<Void, Void> addImages) {
        this.addImages = addImages;
    }

    public void setRemoveImages(SwingWorker<Void, Void> removeImages) {
        this.removeImages = removeImages;
    }

    private void setFirstTime() {
        isFirstTime = false;
    }

    private void setMassAction() {
        this.massAction = true;
    }
}
