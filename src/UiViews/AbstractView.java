package UiViews;

import UiComponents.UiHeader;

import javax.swing.*;
import java.awt.*;

public abstract class AbstractView extends JPanel {
    protected final UiHeader uiHeader_;

    public AbstractView() {
        this.uiHeader_ = new UiHeader();
        this.setLayout(new BorderLayout());

        this.add(this.uiHeader_, BorderLayout.NORTH);
    }

    public UiHeader getUiHeader_() {
        return uiHeader_;
    }
}
