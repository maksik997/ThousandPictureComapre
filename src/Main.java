import com.formdev.flatlaf.FlatDarculaLaf;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        FlatDarculaLaf.setup();

        setUIManagerProperties();

        SwingUtilities.invokeLater(View::new);
    }

    public static void setUIManagerProperties(){
        UIManager.put( "TextComponent.arc", 10 );
        UIManager.put( "Component.focusWidth", 1 );
        UIManager.put( "Button.innerFocusWidth", 0 );
    }
}


