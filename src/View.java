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
        locationView = new LocationView(processing.getProcessAllData());

        // Add listeners to Processing start
        locationView.addProcessingListener(processing);
        processing.addTaskListeners(locationView);


        this.add(locationView);

        this.setTitle("ImageComparator");
        this.setSize(800, 600);
        this.setVisible(true);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
}
