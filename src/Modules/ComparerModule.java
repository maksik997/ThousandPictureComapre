package Modules;

import pl.magzik.PictureComparer;

import java.io.File;
import java.io.IOException;

public class ComparerModule {
    private File sourceDir, destDir;

    private final PictureComparer pc;

    public ComparerModule() {
        destDir = new File(System.getProperty("user.dir"));
        pc = new PictureComparer();
    }

    public void _reset(){
        sourceDir = null;
        pc._reset();
    }

    public void setUp() throws IOException{
        pc._setUp(sourceDir, destDir, pc.getMode());
    }

    // This method compares all images checksums
    public void compareAndExtract() {
        if(sourceDir == null || destDir == null)
            throw new RuntimeException("Source directory and destination directory shouldn't be null");

        pc.map();
        pc.compare();
    }

    public void fileTransfer(){
        pc.move();
    }

    public File getSourceDir() {
        return sourceDir;
    }

    public File getDestDir() {
        return destDir;
    }

    public PictureComparer getPc() {
        return pc;
    }

    public void setSourceDir(File sourceDir) {
        this.sourceDir = sourceDir;
    }

    public void setDestDir(File destDir) {
        this.destDir = destDir;
    }
}
