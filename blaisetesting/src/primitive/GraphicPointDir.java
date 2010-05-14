/*
 * GraphicPointDir.java
 * Created Apr 15, 2010
 */

package primitive;

/**
 * A anchor with a direction, both provided in the same coordinate system.
 *
 * @param <C> the coordinate for the anchor and direction
 * @author Elisha Peterson
 */
public class GraphicPointDir<C> {

    public C anchor;
    public C dir;

    /** Construct the primitive with specified point and direction */
    public GraphicPointDir(C point, C dir) {
        this.anchor = point;
        this.dir = dir;
    }

    /** @return anchor point */
    public C getAnchor() { return anchor; }
    /** @param newValue new anchor point */
    public void setAnchor(C newValue) { anchor = newValue; }
    /** @return direction */
    public C getDirection() { return dir; }
    /** @param newValue new direction */
    public void setDirection(C newValue) { dir = newValue; }

}
