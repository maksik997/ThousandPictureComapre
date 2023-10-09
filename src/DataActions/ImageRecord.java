package DataActions;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.zip.CRC32;

public class ImageRecord {

    private final File file;
    private final BufferedImage image;

    private final String extension;

    private long checksum;

    private final ByteArrayOutputStream baos;

    // For checksum calc purposes
    private final static CRC32 crc32 = new CRC32();

    public ImageRecord(File file) throws IOException {
        // Get file
        this.file = file;

        String fileName = this.file.getName();
        // Get extension
        this.extension = fileName.split("\\.")[
            fileName.split("\\.").length-1
        ];

        // Checking if file is an image, will be done at this point
        this.image = ImageIO.read(file);

        // Convert image to byte array to calculate checksum
        baos = new ByteArrayOutputStream();
        ImageIO.write(image, extension, baos);
        calculateChecksum(baos.toByteArray());
        baos.close();
    }

    private void calculateChecksum(byte[] data){
        crc32.update(data);
        this.checksum = crc32.getValue();
        crc32.reset();
    }

    public File getFile() {
        return file;
    }

    public BufferedImage getImage() {
        return image;
    }

    public String getExtension() {
        return extension;
    }

    public long getChecksum() {
        return checksum;
    }

    @Override
    public String toString() {
        return "ImageRecord( \n" +
                "\t fileName = " + file.getName() + "\n" +
                "\t extension = " + extension + "\n" +
                "\t checksum = " + checksum + "\n" +
                ");";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ImageRecord that)) return false;
        return checksum == that.checksum && Objects.equals(file, that.file);
    }

    public boolean checksumEquals(ImageRecord o){
        return checksum == o.checksum;
    }

    // Couple of static methods

    // Get all images from directory, user can pick directory only.
    public static ArrayList<ImageRecord> getAllImages(File dir) throws IOException {
        if (dir == null){
            return null;
        }

        if(!dir.isDirectory()) {
            throw new IOException("Provided file is not a directory!");
        }

        File[] allFiles = dir.listFiles(File::isFile);
        ArrayList<ImageRecord> images = new ArrayList<>();

        if(allFiles == null)
            return null;

        Arrays.stream(allFiles).forEach(
            f -> {
                if (f.getName().matches(".*\\.jpg$|.*\\.png$")) {
                    try {
                        images.add(new ImageRecord(f));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        );

        return images;
    }
}
