/*
 * PaintsCanvas.java
 * Created Apr 13, 2010
 */

package visometry;

import java.awt.Graphics2D;

/**
 * Interface for an object that draws something on a canvas.
 *
 * @author Elisha Peterson
 */
public interface PaintsCanvas {

    /** Draws something on the provided canvas. */
    public void paint(Graphics2D gr);
}
