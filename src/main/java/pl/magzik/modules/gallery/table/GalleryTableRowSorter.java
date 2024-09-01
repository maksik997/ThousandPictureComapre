package pl.magzik.modules.gallery.table;

import javax.swing.table.TableRowSorter;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A custom row sorter for the {@link GalleryTableModel} that provides custom sorting for table rows.
 * <p>
 * This sorter allows sorting based on natural ordering for text columns and a custom size comparator for size columns.
 * </p>
 */
public class GalleryTableRowSorter extends TableRowSorter<GalleryTableModel> {

    /**
     * Constructs a {@code GalleryTableRowSorter} with the specified table model.
     *
     * @param model the table model to be used for sorting
     */
    public GalleryTableRowSorter(GalleryTableModel model) {
        super(model);
    }

    @Override
    public boolean isSortable(int column) {
        return column < 3;
    }

    @Override
    public Comparator<?> getComparator(int column) {
        if (column == 0) return getNaturalComparator();
        else if (column == 1) return getSizeComparator();


        return super.getComparator(column);
    }

    /**
     * Provides a comparator for comparing size values.
     * <p>
     * Sizes are expected to be in a format like "12.5 MB" or "1.0 GB". The comparator converts these values
     * to bytes for comparison.
     * </p>
     *
     * @return the size comparator
     */
    private Comparator<String> getSizeComparator() {
        return (o1, o2) -> {
            String[] so1 = o1.split(" "),
                    so2 = o2.split(" ");
            double do1 = Double.parseDouble(so1[0].replace(',', '.')) * (so1[1].equals("MB") ? 1024 : so1[1].equals("GB") ? 1024 * 1024 : 1),
                    do2 = Double.parseDouble(so2[0].replace(',', '.')) * (so2[1].equals("MB") ? 1024 : so2[1].equals("GB") ? 1024 * 1024 : 1);

            return Double.compare(do1, do2);
        };
    }

    /**
     * Provides a natural comparator for comparing text values.
     * <p>
     * This comparator compares strings in a way that is more intuitive to human users, taking into account
     * numerical values embedded in the text.
     * </p>
     *
     * @return the natural comparator
     */
    private Comparator<String> getNaturalComparator() {
        return (o1, o2) -> {
            Pattern pattern = Pattern.compile("(\\d+)|(\\D+)");
            Matcher m1 = pattern.matcher(o1);
            Matcher m2 = pattern.matcher(o2);

            while (m1.find() && m2.find()) {
                String s1 = m1.group(), s2 = m2.group();

                int cmp;
                if (s1.matches("\\d+") && s2.matches("\\d+"))
                    cmp = Long.compare(Long.parseLong(s1), Long.parseLong(s2));
                else
                    cmp = s1.compareTo(s2);

                if (cmp != 0) return cmp;
            }

            return Integer.compare(o1.length(), o2.length());
        };
    }
}
