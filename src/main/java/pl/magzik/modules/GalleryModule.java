package pl.magzik.modules;

import pl.magzik.Comparator.FilePredicate;
import pl.magzik.Comparator.ImageFilePredicate;
import pl.magzik.modules.gallery.Entry;
import pl.magzik.modules.gallery.GalleryTableModel;
import pl.magzik.modules.gallery.GalleryTableRowSorter;
import pl.magzik.IO.FileOperator;
import pl.magzik.modules.loader.Module;

import javax.swing.*;
import javax.swing.table.TableRowSorter;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GalleryModule implements Module {

    private GalleryTableModel galleryTableModel;

    private TableRowSorter<GalleryTableModel> tableRowSorter;

    private File destination;

    private List<File> sources;

    private List<File> comparerOutput;

    private FileOperator fileOperator;

    private static final FilePredicate PREDICATE = new ImageFilePredicate();

//    private Mode mode;

    private boolean pHash, pixelByPixel, lowercaseExtension, massAction, isFirstTime;

    private String nameTemplate;

    private Set<String> existingTags;

    private SwingWorker<Void, Void> mapObjects, transferObjects, removeObjects, unifyNames, addImages, removeImages;

    public GalleryModule() { }

    @Override
    public void load() throws IOException {
        galleryTableModel = new GalleryTableModel();
        tableRowSorter = new GalleryTableRowSorter(galleryTableModel);
        existingTags = new HashSet<>();

        loadFromFile();

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
    /*@Override
    public void fileLoad() throws IOException, InterruptedException, TimeoutException {
        // Prepares Picture Comparer with destination path and source path
        sources = fileOperator.loadFiles(1, PREDICATE, sources);
    }*/

    /*@Override
    public void handle(List<File> output) {
        comparerOutput = output;
    }*/

    /*@Override
    public void fileDelete() throws IOException {
        performReduction();
        performDelete(comparerOutput, destination);
    }

    @Override
    public void fileTransfer() throws IOException {
        performReduction();
        performMove(comparerOutput, destination);
    }*/

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
                if (!PREDICATE.test(p.toFile())) return;

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
                            return PREDICATE.test(f);
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

    public void removeImage(List<Integer> indexes) {
        indexes = indexes.stream()
            .map(tableRowSorter::convertRowIndexToModel)
            .collect(Collectors.toList());

        indexes.sort(Integer::compare);

        int l = 0, r = indexes.size() - 1;
        while (l < r) {
            int t = indexes.get(l);
            indexes.set(l, indexes.get(r));
            indexes.set(r, t);
            l++;
            r--;
        }

        galleryTableModel.removeAllEntries(indexes);
    }

    public void deleteImage(List<Integer> indexes) {
        indexes = indexes.stream()
            .map(tableRowSorter::convertRowIndexToModel)
            .collect(Collectors.toList());

        indexes.sort(Integer::compare);

        int l = 0, r = indexes.size() - 1;
        while (l < r) {
            int t = indexes.get(l);
            indexes.set(l, indexes.get(r));
            indexes.set(r, t);
            l++;
            r--;
        }

        try {
            galleryTableModel.deleteAllImages(indexes);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
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

    // Other important methods...

    // Filtering
    public void filterTable(String filter) {
        filter = Pattern.quote(filter);
        RowFilter<GalleryTableModel, Object> rowFilter = RowFilter.regexFilter(".*"+filter+".*", 0);
        tableRowSorter.setRowFilter(rowFilter);
    }

    public void performReduction(List<File> output) throws IOException {
        if (output.isEmpty())
            return;

        List<Path> files = output.stream().map(File::toPath).toList();
        galleryTableModel.reduction(files);

        saveToFile();
    }

    private void loadFromFile() throws IOException {
        List<String> tagList = ResourceModule.getInstance().getTextFile("tags.tp");
        if (tagList == null) {
            ResourceModule.getInstance().addTextFile("tags.tp", new ArrayList<>());
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

        List<String> images = ResourceModule.getInstance().getTextFile("gallery.tp");

        if (images == null) {
            ResourceModule.getInstance().addTextFile("gallery.tp", new ArrayList<>());
            return;
        }

        try {
            images.stream()
                .map(separateLine)
                .filter(Objects::nonNull)
                .filter(e -> Files.exists(e.getPath()))
                .filter(e -> {
                    try {
                        return PREDICATE.test(e.getPath().toFile());
                    } catch (IOException ex) {
                        throw new UncheckedIOException(ex);
                    }
                })
                .forEach(galleryTableModel::addEntry);
        } catch (UncheckedIOException e) {
            throw e.getCause();
        }

        // Rebuild save file.
        saveToFile(galleryTableModel.getImages());
    }

    public void saveToFile() throws IOException {
        saveToFile(galleryTableModel.getImages());
    }

    private static void saveToFile(List<Entry> images) throws IOException {
        ResourceModule.getInstance().setTextFile(
            "gallery.tp",
            images.stream().map(Entry::serialize).toList(),
            true
        );
    }

    private void saveTagsToFile() throws IOException {
        ResourceModule.getInstance().setTextFile("tags.tp", existingTags.stream().toList());

        /*try (BufferedWriter writer = Files.newBufferedWriter(tagsReferenceFilePath)) {
            for (String tag : existingTags) {
                writer.write(tag);
                writer.newLine();
            }
        }*/
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

    /*public Mode getMode() {
        return mode;
    }*/

    public Set<String> getExistingTags() {
        return existingTags;
    }

    public boolean isPixelByPixel() {
        return pixelByPixel;
    }

    /*@Override
    public void setPixelByPixel(boolean pixelByPixel) {
        this.pixelByPixel = pixelByPixel;
    }

    @Override
    public boolean isPerceptualHash() {
        return pHash;
    }*/

    public String getNameTemplate() {
        return nameTemplate;
    }

    public boolean isLowercaseExtension() {
        return lowercaseExtension;
    }

    public void setLowercaseExtension(boolean lowercaseExtension) {
        this.lowercaseExtension = lowercaseExtension;
    }

    /*@Override
    public void setPerceptualHash(boolean pHash) {
        this.pHash = pHash;
    }

    @Override
    public void setMode(Mode mode) {
        this.mode = mode;
    }

    @Override
    public void setOutputPath(String destination) {
        this.destination = new File(destination);
    }

    @Override
    public String getOutputPath() {
        return destination.toString();
    }

    @Override
    public List<File> getInput() {
        return sources;
    }*/

    public void setSources(List<Integer> indexes) {
        this.sources = indexes.stream()
                .map(tableRowSorter::convertRowIndexToModel)
                .map(i -> galleryTableModel.getImages().get(i))
                .map(Entry::getPath)
                .map(Path::toFile)
                .toList();
    }

    public List<File> getFiles(int... indexes) {
        return getFiles(Arrays.stream(indexes).boxed().toList());
    }

    public List<File> getFiles(List<Integer> indexes) {
        return indexes.stream()
                        .map(tableRowSorter::convertRowIndexToModel)
                        .map(i -> galleryTableModel.getImages().get(i))
                        .map(Entry::getPath)
                        .map(Path::toFile)
                        .toList();
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
