package Modules;

import java.nio.file.attribute.FileTime;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class Utility {

    private static final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    public static String formatInto(double bytes) {
        if (bytes < 1024)
            return bytes + " B";

        return bytes > Math.pow(1024, 3) ?
                formatInto(bytes, Sizes.GIGA)
                : bytes > Math.pow(bytes, 2) ?
                    formatInto(bytes, Sizes.MEGA) :
                    formatInto(bytes, Sizes.KILO);
    }

    public static String formatInto(double bytes, Sizes size) {
        double formatted = switch (size) {
            case MEGA -> bytes / (1024*1024);
            case KILO -> bytes / 1024;
            case GIGA -> bytes / (1024*1024*1024);
        };

        return formatted == 0 ? String.format("%.2f %s", formatted, size) : String.format("%d %s", (int)formatted, size);
    }

    public enum Sizes {
        MEGA, KILO, GIGA;

        @Override
        public String toString() {
            return switch (this) {
                case MEGA -> "MB";
                case KILO -> "KB";
                case GIGA -> "GB";
            };
        }
    }

    public static String formatDate(FileTime fileTime) {
        return dateFormat.format(fileTime.toMillis());
    }
}
