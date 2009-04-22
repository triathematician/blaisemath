/**
 * MouseGestureListener.java
 * Created on Mar 26, 2008
 */

package sequor.control.gestures;

import sequor.control.gestures.MouseGestureEvent;
import java.util.EventListener;

/**
 *
 * @author Elisha Peterson
 */
public interface MouseGestureListener extends EventListener {
    public void gestureMade(MouseGestureEvent e);
}
