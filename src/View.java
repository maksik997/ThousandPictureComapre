import MinorViews.LocationView;

import javax.swing.*;
import java.awt.*;

public class View extends JFrame {

    private final Controller controller;

    // Different Panels
    private final LocationView locationView;

    // Model Classes
//    private Processing processing;


    public View() throws HeadlessException {

//        processing = new Processing();
        locationView = new LocationView();

        // Add listeners to Processing start
        /*locationView.addProcessingListener(processing);
        processing.addFileLoadingListeners(locationView);*/


        this.add(locationView);

        this.setTitle("ImageComparator");
        this.setSize(800, 600);
        this.setVisible(true);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        controller = new Controller(this, new Model());
    }

    public LocationView getLocationView() {
        return locationView;
    }
}
