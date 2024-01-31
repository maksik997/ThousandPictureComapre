import pl.magzik.PictureComparer;

import java.io.File;
import java.io.IOException;

public class ComparerLayer {
    private File sourceDir, destDir;

    private final PictureComparer pc;



    public ComparerLayer() {
        destDir = new File(System.getProperty("user.dir"));
        try {
            pc = new PictureComparer();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void _reset(){
        sourceDir = null;
        pc._reset();
    }

    public void setUp(){
        try {
            pc._setUp(sourceDir, destDir);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // This method compares all images checksums
    public void compareAndExtract(){
        if(sourceDir == null || destDir == null)
            throw new RuntimeException("Test exception::");

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
