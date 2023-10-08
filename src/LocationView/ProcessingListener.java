package LocationView;

import java.io.File;
import java.util.EventListener;
import java.util.EventObject;

public interface ProcessingListener extends EventListener {
    void actionPerformed(ProcessingEvent e);
}
