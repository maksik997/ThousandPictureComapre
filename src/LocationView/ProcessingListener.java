package LocationView;

import java.util.EventListener;

public interface ProcessingListener extends EventListener {
    void actionPerformed(ProcessingEvent e);
}
