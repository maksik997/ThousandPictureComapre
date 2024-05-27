import com.formdev.flatlaf.FlatDarculaLaf;

import javax.swing.*;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        FlatDarculaLaf.setup();

        setUIManagerProperties();

        SwingUtilities.invokeLater(() -> {
            try {
                new View();
            } catch (IOException e) {
                System.exit(1); // In the case of someone smart enough to delete app folder while using it :P
                // Or in case of I/O error
            }
        });
    }

    public static void setUIManagerProperties(){
        UIManager.put( "TextComponent.arc", 10 );
        UIManager.put( "Component.focusWidth", 1 );
        UIManager.put( "Button.innerFocusWidth", 0 );
    }
}


