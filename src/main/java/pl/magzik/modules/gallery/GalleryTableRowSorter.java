package pl.magzik.modules.gallery;

import javax.swing.table.TableRowSorter;
import java.util.Comparator;

public class GalleryTableRowSorter extends TableRowSorter<GalleryTableModel> {

    public GalleryTableRowSorter(GalleryTableModel model) {
        super(model);
    }

    @Override
    public boolean isSortable(int column) {
        return column < 3;
    }

    @Override
    public Comparator<?> getComparator(int column) {
        if (column == 1) {
            return (Comparator<String>) (o1, o2) -> {
                String[] so1 = o1.split(" "),
                        so2 = o2.split(" ");
                double do1 = Double.parseDouble(so1[0].replace(',', '.')) * (so1[1].equals("MB") ? 1024 : so1[1].equals("GB") ? 1024 * 1024 : 1),
                        do2 = Double.parseDouble(so2[0].replace(',', '.')) * (so2[1].equals("MB") ? 1024 : so2[1].equals("GB") ? 1024 * 1024 : 1);

                return Double.compare(do1, do2);
            };
        }

        return super.getComparator(column);
    }
}
