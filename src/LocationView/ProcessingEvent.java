package LocationView;

import java.util.EventObject;

public class ProcessingEvent extends EventObject {
    private final String path;

    public ProcessingEvent(Object source, String path) {
        super(source);
        this.path = path;
    }

    public ProcessingEvent(Object source){
        this(source, "");
    }

    public String getPath() {
        return path;
    }
}
