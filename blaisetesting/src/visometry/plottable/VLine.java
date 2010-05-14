/**
 * VLine.java
 * Created on Sep 18, 2009
 */

package visometry.plottable;

import java.awt.Color;
import primitive.style.LineStyle;
import primitive.style.PointStyle;
import visometry.PointDragListener;

/**
 * <p>
 *   <code>VLine</code> represents the straight line passing through two points.
 * </p>
 *
 * @author Elisha Peterson
 */
public class VLine<C> extends VAbstractTwoPoint<C> {
    
    /** Style for points. */
    PointStyle pointStyle;
    /** Whether points are visible. */
    boolean pointsVisible;

    /** Construct to specified coordinates */
    public VLine(C... values) {
        super(values, new PointStyle(), new LineStyle(new Color(64, 0, 0)));
    }

    /** @return current style of stroke for this plottable */
    public LineStyle getStrokeStyle() { return (LineStyle) entrySeg.style; }
    /** Set current style of stroke for this plottable */
    public void setStrokeStyle(LineStyle newValue) { if (entrySeg.style != newValue) { entrySeg.style = newValue; firePlottableStyleChanged(); } }

    /** @return true if points at vertices of polygon are visible */
    public boolean isPointsVisible() { return entryP1.visible; }
    /** Sets visiblity of the vertices. */
    public void setPointsVisible(boolean value) { if (entryP1.visible != value) { entryP1.visible = value; entryP2.visible = false; firePlottableStyleChanged(); } }

    /** @return current style of stroke for this plottable */
    public PointStyle getPointStyle() { return (PointStyle) entryP1.style; }
    /** Set current style of stroke for this plottable */
    public void setPointStyle(PointStyle newValue) { if (entryP1.style != newValue) { entryP1.style = newValue; entryP2.style = newValue; firePlottableStyleChanged(); } }

}
