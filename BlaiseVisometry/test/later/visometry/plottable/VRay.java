/**
 * VLine.java
 * Created on Sep 18, 2009
 */

package later.visometry.plottable;

import java.awt.Color;
import primitive.style.temp.PointLabeledStyle;
import primitive.style.temp.RayStyle;

/**
 * <p>
 *   <code>VLine</code> represents the straight line passing through two points.
 * </p>
 *
 * @author Elisha Peterson
 */
public class VRay<C> extends VAbstractTwoPoint<C> {
    
    /** Style for points. */
    PointLabeledStyle pointStyle;
    /** Whether points are visible. */
    boolean pointsVisible;

    /** Construct to specified coordinates */
    public VRay(C... values) {
        super(values, new PointLabeledStyle(), new RayStyle(new Color(64, 0, 0)));
    }

    /** @return current style of stroke for this plottable */
    public RayStyle getRayStyle() { return (RayStyle) entrySeg.renderer; }
    /** Set current style of stroke for this plottable */
    public void setRayStyle(RayStyle newValue) { if (entrySeg.renderer != newValue) { entrySeg.renderer = newValue; firePlottableStyleChanged(); } }

    /** @return true if points at vertices of polygon are visible */
    public boolean isPointsVisible() { return entryP1.visible; }
    /** Sets visiblity of the vertices. */
    public void setPointsVisible(boolean value) { if (entryP1.visible != value) { entryP1.visible = value; entryP2.visible = false; firePlottableStyleChanged(); } }

    /** @return current style of stroke for this plottable */
    public PointLabeledStyle getPointStyle() { return (PointLabeledStyle) entryP1.renderer; }
    /** Set current style of stroke for this plottable */
    public void setPointStyle(PointLabeledStyle newValue) { if (entryP1.renderer != newValue) { entryP1.renderer = newValue; entryP2.renderer = newValue; firePlottableStyleChanged(); } }

}
