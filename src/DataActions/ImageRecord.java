package DataActions;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.zip.CRC32;

public class ImageRecord {

    private final File file;

//    private final String extension;

    private long checksum;

    // For checksum calc purposes
    private final static CRC32 crc32 = new CRC32();

    private final static String acceptedTypes = ".*\\.jpg$|.*\\.png$|.*\\.jpeg$|.*\\.gif$|.*\\.bmp$|.*\\.wbmp$|.*\\.tiff$|.*\\.tif$.*\\.JPG$|.*\\.PNG$|.*\\.JPEG$|.*\\.GIF$|.*\\.BMP$|.*\\.WBMP$|.*\\.TIFF$|.*\\.TIF$";

    public ImageRecord(File file) throws IOException {
        // Get file
        this.file = file;
        calculateChecksum(ImageIO.read(file));
    }

    private void calculateChecksum(BufferedImage img) throws IOException {
        // Update v0-3
        String[] fileNameArr = file.getName().split("\\.");
        String extension = fileNameArr[fileNameArr.length-1];

        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        ImageIO.write(img, extension, byteStream);
        crc32.update(byteStream.toByteArray());
        byteStream.close();
        checksum = crc32.getValue();
        crc32.reset();
    }

    public File getFile() {
        return file;
    }

    public long getChecksum() {
        return checksum;
    }

    @Override
    public String toString() {
        return "ImageRecord( \n" +
                "\t fileName = " + file.getName() + "\n" +
                "\t checksum = " + checksum + "\n" +
                ");";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ImageRecord that)) return false;
        return checksum == that.checksum && Objects.equals(file, that.file);
    }

    @Override
    public int hashCode() {
        return Objects.hash(file, checksum);
    }

    public boolean checksumEquals(ImageRecord o){
        return checksum == o.checksum;
    }

    // Couple of static methods

    // Update v3-0
    public static HashMap<Long, ArrayList<ImageRecord>> mappedImages(File dir) throws IOException {
        if (dir == null) {
            return null;
        }

        if (!dir.isDirectory()) {
            throw new IOException("Provided file is not a directory!");
        }

        File[] allFiles = dir.listFiles(File::isFile);
        if (allFiles == null)
            return null;

        HashMap<Long, ArrayList<ImageRecord>> mappedImages = new HashMap<>();

        Arrays.stream(allFiles)
                .filter(file -> file.getName().matches(acceptedTypes))
                .forEach(
                        file -> {
                            try {
                                ImageRecord ir = new ImageRecord(file);
                                if (!mappedImages.containsKey(ir.getChecksum()))
                                    mappedImages.put(ir.getChecksum(), new ArrayList<>());
                                mappedImages.get(ir.getChecksum()).add(ir);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }

                        }
                );

        return mappedImages;
    }
}
