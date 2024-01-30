/*
    This class holds all simple operations needed to compare.
    However, you'll need to define three different methods
*/
package Comparer;

import Comparer.Structures.Record;
import Comparer.Structures.Utils.Logger;

import java.io.File;
import java.io.IOException;
import java.util.*;

public abstract class Comparer<T extends Record> implements Logger {

    private static final boolean echo = false;

    protected ArrayList<T> duplicates;
    protected HashMap<Long, ArrayList<T>> mappedObjects;

                 // total number of valid files
    protected int totalObjectCount, processedObjectCount, duplicatesObjectCount;
                              //  total number of processed Objects

    protected List<File> sourceFiles;

    protected File sourceDirectory, destDirectory;

    protected String[] acceptedTypes;

    public Comparer(List<File> sourceFiles, File sourceDirectory, File destDirectory) throws IOException {
        _setUp(sourceFiles, sourceDirectory, destDirectory);
    }

    public Comparer() throws IOException {
        _reset();
    }

    public Comparer(File sourceDirectory, File destDirectory) throws IOException {
        this(null, sourceDirectory, destDirectory);
    }

    public Comparer(List<File> sourceFiles, File destDirectory) throws IOException {
        this(sourceFiles, null, destDirectory);
    }

    public int getTotalObjectCount() {
        return totalObjectCount;
    }

    public int getProcessedObjectCount() {
        return processedObjectCount;
    }

    public int getDuplicatesObjectCount() {
        return duplicatesObjectCount;
    }

    public void _setUp(File sourceDirectory, File destDirectory) throws IOException {
        _setUp(null, sourceDirectory, destDirectory);
    }
    public void _setUp(List<File> sourceFiles, File destDirectory) throws IOException {
        _setUp(sourceFiles, null, destDirectory);
    }

    public void _setUp(List<File> sourceFiles, File sourceDirectory, File destDirectory) throws IOException {
        if (
            this.duplicates != null ||
            this.mappedObjects != null ||
            this.totalObjectCount > 0 ||
            this.processedObjectCount > 0 ||
            this.sourceFiles != null ||
            this.sourceDirectory != null ||
            this.destDirectory != null
        ) {
            _reset();
        }

        this.sourceFiles = sourceFiles;
        this.sourceDirectory = sourceDirectory;
        this.destDirectory = destDirectory;

        if (this.sourceFiles == null ){
            if(this.sourceDirectory == null || !this.sourceDirectory.isDirectory())
                throw new IOException("Couldn't find any source files.");

            this.sourceFiles = Arrays.asList(Objects.requireNonNull(
                this.sourceDirectory.listFiles(File::isFile)
            ));
        }

        if(acceptedTypes != null) {
            String pattern = generateTypePattern();
            this.sourceFiles.stream()
                    .filter(file -> file.getName().matches(pattern))
                    .forEach(f -> totalObjectCount++);
        }
    }

    public void _reset() {
        this.duplicates = null;
        this.mappedObjects = null;
        this.totalObjectCount = 0;
        this.processedObjectCount = 0;
        this.sourceFiles = null;
        this.sourceDirectory = null;
        this.destDirectory = null;
    }


    protected String generateTypePattern(){
        StringBuilder types = new StringBuilder();
        Arrays.stream(acceptedTypes).forEach(
            type -> types.append(String.format(".*\\.%s$|", type).toLowerCase())
                        .append(String.format(".*\\.%s$|", type).toUpperCase())
        );

        return types.toString();
    }

    public abstract void map() throws IOException;

    public abstract void compare();

    public abstract void move();

    @Override
    public void log(String msg) {
        if(echo) {
            System.out.printf("Comparer -> %s%n", msg);
            System.out.flush();
        }
    }
}
