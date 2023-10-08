// Algorithm:
/*
*   Check if the dimensions of both images match
*   Get the RGB values of both images.
*   Calculate the difference in two corresponding pixels of three color components
*   Repeat Steps 2-3 for each pixel of the images.
*   Lastly, calculate the percentage by dividing the sum of differences by the number of pixels.
*/

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(View::new);
    }
}


