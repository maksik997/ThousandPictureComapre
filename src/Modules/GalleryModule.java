package Modules;

import Modules.Gallery.Entry;
import Modules.Gallery.GalleryTableModel;
import Modules.Gallery.GalleryTableRowSorter;
import pl.magzik.Comparator.FilePredicate;
import pl.magzik.Comparator.ImageFilePredicate;
import pl.magzik.IO.FileOperator;
import pl.magzik.Structures.ImageRecord;
import pl.magzik.Structures.Record;
import Modules.ComparerModule.Mode;

import javax.swing.*;
import javax.swing.table.TableRowSorter;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;
import java.util.stream.Stream;

public class GalleryModule {

    private static final Path imageReferenceFilePath = Path.of(".", "data", "gallery.tp"),
        tagsReferenceFilePath = Path.of(".", "data", "tags.tp");

    private final GalleryTableModel galleryTableModel;

    private final TableRowSorter<GalleryTableModel> tableRowSorter;

    private final FileOperator fileOperator;

    private File destination;

    private List<File> sources;

    private List<File> comparerOutput;

    private Mode mode;

    private boolean pHash, pixelByPixel, lowercaseExtension;

    private String nameTemplate;

    private final static FilePredicate filePredicate = new ImageFilePredicate();

    private SwingWorker<Void, Void> mapObjects, transferObjects, removeObjects, unifyNames, addImages, removeImages;

    private final Set<String> existingTags;

    // Helpers for exception handling while loading.
    private boolean massAction, isFirstTime;

    public GalleryModule() throws IOException {
        galleryTableModel = new GalleryTableModel();
        tableRowSorter = new GalleryTableRowSorter(galleryTableModel);
        existingTags = new HashSet<>();

        if (Files.exists(imageReferenceFilePath)) {
            loadFromFile();
        }

        fileOperator = new FileOperator();
        destination = new File(System.getProperty("user.home"));
        sources = new LinkedList<>();
        comparerOutput = null;

        pHash = false;
        pixelByPixel = false;
        lowercaseExtension = false;

        nameTemplate = "tp_img_";
    }

    // Comparer interaction
    public void prepareComparer(String destPath, List<Integer> indexes) throws IOException, InterruptedException, TimeoutException {
        // Prepares Picture Comparer with destination path and source path

        indexes = indexes.stream().map(tableRowSorter::convertRowIndexToModel).toList();

        List<Path> toCheck = new ArrayList<>();
        for (int i = 0; i < galleryTableModel.getImages().size(); i++) {
            if (indexes.contains(i))
                toCheck.add(galleryTableModel.getImages().get(i).getPath());
        }

        destination = new File(destPath);
        sources = fileOperator.loadFiles(1, filePredicate, toCheck.stream().map(Path::toFile).toList());
    }

    public void compare() throws IOException, ExecutionException {
        // Finds all redundant images
        // Do not call before setUp
        Objects.requireNonNull(sources);

        Map<?, List<Record<BufferedImage>>> map;

        if (pHash && pixelByPixel)
            map = Record.process(sources, ComparerModule.imageRecordFunction, ImageRecord.pHashFunction, ImageRecord.pixelByPixelFunction);
        else if (pHash)
            map = Record.process(sources, ComparerModule.imageRecordFunction, ImageRecord.pHashFunction);
        else if (pixelByPixel)
            map = Record.process(sources, ComparerModule.imageRecordFunction, ImageRecord.pixelByPixelFunction);
        else
            map = Record.process(sources, ComparerModule.imageRecordFunction);

        comparerOutput = map
            .values().stream()
            .filter(list -> list.size() > 1)
            .flatMap(Collection::stream)
            .map(Record::getFile)
            .toList();
    }

    public void removeRedundant() throws IOException {
        Objects.requireNonNull(destination);
        Objects.requireNonNull(comparerOutput);

        if (comparerOutput.isEmpty()) return;

        performReduction();

        // Removes all the redundant images
        try {
            comparerOutput.parallelStream().forEach(file -> {
                try {
                    Files.delete(file.toPath());
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            });
        } catch (UncheckedIOException e) {
            throw e.getCause();
        }
    }

    public void moveRedundant() throws IOException {
        Objects.requireNonNull(destination);
        Objects.requireNonNull(comparerOutput);

        if (comparerOutput.isEmpty()) return;

        performReduction();

        // Moves all the redundant images
        String separator = File.pathSeparator;

        try {
            comparerOutput.parallelStream().forEach(file -> {
                try {
                    Files.move(file.toPath(), Paths.get(destination + separator + file.getName()), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            });
        } catch (UncheckedIOException e) {
            throw e.getCause();
        }
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
                if (!filePredicate.test(p.toFile())) return;

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
                    .filter(f -> {
                        try {
                            return filePredicate.test(f);
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    })
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
        galleryTableModel.removeEntry(tableRowSorter.convertRowIndexToModel(idx));
    }

    public void deleteImage(int idx) throws IOException {
        galleryTableModel.deleteImage(tableRowSorter.convertRowIndexToModel(idx));
    }

    public void openImage(int idx) throws IOException {
        galleryTableModel.openEntry(tableRowSorter.convertRowIndexToModel(idx));
    }

    public void addTag(int idx, String tag) throws IOException {
        if (!existingTags.contains(tag)) {
            existingTags.add(tag);
            saveTagsToFile();
        }
        galleryTableModel.addTag(tableRowSorter.convertRowIndexToModel(idx), tag);
    }

    public void removeTag(int idx, String tag) {
        galleryTableModel.removeTag(tableRowSorter.convertRowIndexToModel(idx), tag);
    }

    public String[] getTags(int idx) {
        String tags = ((String) galleryTableModel.getValueAt(getTableRowSorter().convertRowIndexToModel(idx), 3));
        if (!tags.matches("^.+\\w+.+$")) return new String[0];
        else return tags.split(", ");
    }

    // Special set of operations

    public void unifyNames() throws IOException {
        galleryTableModel.unifyNames(nameTemplate, lowercaseExtension);
    }

    // todo more of this...

    // Other important methods...

    // Filtering
    public void filterTable(String filter) {
        RowFilter<GalleryTableModel, Object> rowFilter = RowFilter.regexFilter(".*"+filter+".*", 0);
        tableRowSorter.setRowFilter(rowFilter);
    }

    private void performReduction() throws IOException {
        if (comparerOutput.isEmpty())
            return;

        List<Path> files = comparerOutput.stream().map(File::toPath).toList();
        galleryTableModel.reduction(files);

        saveToFile();
    }

    private void loadFromFile() throws IOException {
        if (!Files.exists(tagsReferenceFilePath)) {
            Files.createFile(tagsReferenceFilePath);
        } else {
            try (BufferedReader reader = Files.newBufferedReader(tagsReferenceFilePath)) {
                existingTags.addAll(reader.lines().filter(l -> l.matches("^[\\-\\w]+$")).toList());
            }
        }

        Function<String, Entry> separateLine = l -> {
            // Create a path
            String[] split = l.split(" : ");
            Path p = Path.of(split[0]);
            String[] tags;
            if (split.length == 2) tags = split[1].split(",");
            else tags = new String[0];

            // Create entry
            try {
                if (tags.length == 0) return new Entry(p);
                else return new Entry(p, tags);
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
                    .filter(e -> {
                        try {
                            return filePredicate.test(e.getPath().toFile());
                        } catch (IOException ex) {
                            throw new UncheckedIOException(ex);
                        }
                    })
                    .forEach(galleryTableModel::addEntry);
        } catch (UncheckedIOException e) {
            throw e.getCause();
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
            .map(Entry::serialize)
            .toList();

            for (String image : toSave) {
                writer.write(image);
                writer.newLine();
            }
        }
    }

    private void saveTagsToFile() throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(tagsReferenceFilePath)) {
            for (String tag : existingTags) {
                writer.write(tag);
                writer.newLine();
            }
        }
    }

    public GalleryTableModel getGalleryTableModel() {
        return galleryTableModel;
    }

    public TableRowSorter<GalleryTableModel> getTableRowSorter() {
        return tableRowSorter;
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

    public File getDestination() {
        return destination;
    }

    public Mode getMode() {
        return mode;
    }

    public Set<String> getExistingTags() {
        return existingTags;
    }

    public boolean getPixelByPixel() {
        return pixelByPixel;
    }

    public void setPixelByPixel(boolean pixelByPixel) {
        this.pixelByPixel = pixelByPixel;
    }

    public boolean getPHash() {
        return pHash;
    }

    public String getNameTemplate() {
        return nameTemplate;
    }

    public boolean isLowercaseExtension() {
        return lowercaseExtension;
    }

    public void setLowercaseExtension(boolean lowercaseExtension) {
        this.lowercaseExtension = lowercaseExtension;
    }

    public void setPHash(boolean pHash) {
        this.pHash = pHash;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public void setDestination(File destination) {
        this.destination = destination;
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

    public void setNameTemplate(String nameTemplate) {
        this.nameTemplate = nameTemplate;
    }
}
