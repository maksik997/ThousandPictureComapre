package LocationView;

import java.util.EventListener;
import java.util.EventObject;

public interface TaskListener extends EventListener {
    void actionPerformed(EventObject e);
}
