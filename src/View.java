import LocationView.LocationView;

import javax.swing.*;
import java.awt.*;

public class View extends JFrame {

    // Different Panels
    LocationView locationView;

    // Model Classes
    Processing processing;


    public View() throws HeadlessException {
        processing = new Processing();
        locationView = new LocationView(
            processing.getFileLoadingWorker(),
            processing.getLookForDuplicatesWorker(),
            processing.getMoveFilesWorker()
        );

        // Add listeners to Processing start
        locationView.addProcessingListener(processing);
        processing.addFileLoadingListeners(locationView);


        this.add(locationView);

        this.setTitle("ImageComparator");
        this.setSize(800, 600);
        this.setVisible(true);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
}
