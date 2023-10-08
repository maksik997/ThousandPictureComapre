package LocationView;

import java.io.File;
import java.util.EventObject;

public class ProcessingEvent extends EventObject {

    private String path;

    public ProcessingEvent(Object source, String path) {
        super(source);
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
