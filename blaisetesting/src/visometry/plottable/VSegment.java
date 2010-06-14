/**
 * VSegment.java
 * Created on Apr 8, 2010
 */

package visometry.plottable;

import java.awt.Color;
import primitive.style.PathStylePoints;
import primitive.style.PointLabeledStyle;
import visometry.PointDragListener;

/**
 * <p>
 *   <code>VSegment</code> displays a segment between two points. It is dynamic, allowing
 *   the points to be moved around.
 * </p>
 *
 * @author Elisha Peterson
 */
public class VSegment<C> extends VAbstractTwoPoint<C>
        implements PointDragListener<C> {
    
    /** Style for points. */
    PointLabeledStyle pointStyle;

    /** Construct to specified coordinates */
    public VSegment(C... values) {
        super(values, new PointLabeledStyle(), new PathStylePoints(new Color(64, 0, 0)));
    }

    /** @return current style of stroke for this plottable */
    public PathStylePoints getStrokeStyle() { return (PathStylePoints) entrySeg.style; }
    /** Set current style of stroke for this plottable */
    public void setStrokeStyle(PathStylePoints newValue) { if (entrySeg.style != newValue) { entrySeg.style = newValue; firePlottableStyleChanged(); } }

    /** @return true if points at vertices of polygon are visible */
    public boolean isPointsVisible() { return entryP1.visible; }
    /** Sets visiblity of the vertices. */
    public void setPointsVisible(boolean value) { if (entryP1.visible != value) { entryP1.visible = value; entryP2.visible = false; firePlottableStyleChanged(); } }

    /** @return current style of stroke for this plottable */
    public PointLabeledStyle getPointStyle() { return (PointLabeledStyle) entryP1.style; }
    /** Set current style of stroke for this plottable */
    public void setPointStyle(PointLabeledStyle newValue) { if (entryP1.style != newValue) { entryP1.style = newValue; entryP2.style = newValue; firePlottableStyleChanged(); } }

}
