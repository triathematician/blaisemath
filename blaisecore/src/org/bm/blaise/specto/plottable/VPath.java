/**
 * VPoint.java
 * Created on Jul 30, 2009
 */

package org.bm.blaise.specto.plottable;

import org.bm.blaise.sequor.timer.TimeClock;
import org.bm.blaise.specto.primitive.PathStyle;
import org.bm.blaise.specto.visometry.AbstractAnimatingPlottable;
import org.bm.blaise.specto.visometry.Visometry;
import org.bm.blaise.specto.visometry.VisometryGraphics;
import org.bm.blaise.specto.visometry.VisometryMouseEvent;

/**
 * <p>
 *   <code>VPoint</code> is a static set of points in the visometry.
 *   This is the "non-dynamic" version of <code>VPointSet</code>, allowing a potentially
 *   a huge number of points stored for display.
 * </p>
 *
 * @author Elisha Peterson
 * @seealso VPointSet
 */
public class VPath<C> extends AbstractAnimatingPlottable<C> {

    //
    // PROPERTIES
    //

    /** Values */
    protected C[] values;

    /** Style option */
    PathStyle style = new PathStyle();

    /** Whether to create a closed path */
    boolean closedPath;

    //
    // CONSTRUCTORS
    //

    /**
     * Construct with sequence of points
     * @param values the points
     */
    public VPath(C[] values) {
        this.values = values;
        animating = false;
    }

    //
    // BEAN PATTERNS
    //

    /** @return entire list of values, as an array */
    public C[] getValues() {
        return values;
    }

    /** @return value at specified index */
    public C getValue(int index) {
        return values[index];
    }

    /** Replaces entire list of values with those supplied */
    public void setValues(C[] values) {
        this.values = values;
        fireStateChanged();
    }

    /** Replaces value at specified index with that supplied. */
    public void setValue(int index, C value) {
        values[index] = value;
        fireStateChanged();
    }

    /** @return current style for the path */
    public PathStyle getStyle() {
        return style;
    }

    /** Sets the style for the path
     * @param style new style
     */
    public void setStyle(PathStyle style) {
        this.style = style;
    }

    public boolean isClosedPath() {
        return closedPath;
    }

    public void setClosedPath(boolean closedPath) {
        this.closedPath = closedPath;
    }

    @Override
    public String toString() {
        return "VPath [" + values.length + " points]";
    }
   

    //
    // PAINTING
    //

    double time;

    public void recomputeAtTime(Visometry<C> vis, VisometryGraphics<C> canvas, TimeClock clock) {
        time = clock.getTime();
    }

    @Override
    public void paintComponent(VisometryGraphics<C> vg) {
        vg.setPathStyle(style);
        if (animating) {
            // default to animating over each value
            int length = Math.min(values.length, (int) time);
            vg.drawPath(values, 0, length);
        } else {
            if (closedPath) {
                vg.drawClosedPath(values);
            } else {
                vg.drawPath(values);
            }
        }
    }

    //
    // DYNAMIC EDITING
    //

    public boolean isClickablyCloseTo(VisometryMouseEvent<C> e) {
        for (int i = 0; i < values.length; i++) {
            if (e.withinRangeOf(values[i], 10)) {
                return true;
            }
        }
        return false;
    }
}
