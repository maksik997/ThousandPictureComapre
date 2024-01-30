package Comparer.Structures;

import Comparer.Structures.Utils.Checksum;

import java.io.File;
import java.util.Objects;

public abstract class Record<T> implements Checksum<T>{

    // File Reference
    private final File file;


    // Checksum of the content
    private long checksum;


    public Record(File file) {
        this.file = file;
        this.checksum = this.hashCode();
    }
    public Record(Record<T> r){
        this(r.file);
    }

    public File getFile() {
        return file;
    }

    public long getChecksum() {
        return checksum;
    }

    public void setChecksum(long checksum) {
        this.checksum = checksum;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Record record)) return false;
        return checksum == record.checksum && Objects.equals(file, record.file);
    }

    @Override
    public int hashCode() {
        return Objects.hash(file, checksum);
    }

    @Override
    public String toString() {
        return "Record{" +
                "file=" + file +
                ", checksum=" + checksum +
                '}';
    }

}
