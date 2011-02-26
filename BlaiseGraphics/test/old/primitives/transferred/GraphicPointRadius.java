/*
 * GraphicPointRadius.java
 * Created Jun 14, 2010
 */

package old.primitives.transferred;

/**
 * A point with a radius.
 *
 * @param <C> the coordinate for the anchor point
 * @author Elisha Peterson
 */
public class GraphicPointRadius<C> {

    public C anchor;
    public double rad;

    /** Construct the primitive with specified point and radius */
    public GraphicPointRadius(C point, double rad) {
        this.anchor = point;
        this.rad = rad;
    }

    /** @return anchor point */
    public C getAnchor() { return anchor; }
    /** @param newValue new anchor point */
    public void setAnchor(C newValue) { anchor = newValue; }
    /** @return radius */
    public double getRadius() { return rad; }
    /** @param newValue new radius */
    public void setRadius(double newValue) { rad = newValue; }

}
