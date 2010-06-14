/**
 * VPointSet.java
 * Created on Jul 30, 2009
 */

package visometry.plottable;

import java.util.Arrays;
import primitive.style.PointLabeledStyle;
import visometry.PointDragListener;

/**
 * <p>
 *   <code>VPointSet</code> is a set of points in the visometry. Each point is
 *   displayed as a separate entity. The individual points may be moved, if the
 *   <code>editable</code> property is set to <code>TRUE</code>.
 * </p>
 * @author Elisha Peterson
 * @seealso VPath
 */
public class VPointSet<C> extends VAbstractPointArray<C>
        implements PointDragListener<C> {

    /**
     * Construct with sequence of points
     * @param values the points
     */
    public VPointSet(C... values) {
        super(new PointLabeledStyle(), values);
        entry.listener = this;
    }

    @Override
    public String toString() {
        return "VPointSet " + Arrays.toString(points);
    }

    /** @return current style of point for this plottable */
    public PointLabeledStyle getPointStyle() { return (PointLabeledStyle) entry.style; }
    /** Set current style of point for this plottable */
    public void setPointStyle(PointLabeledStyle newValue) { if (entry.style != newValue) { entry.style = newValue; firePlottableStyleChanged(); } }

    public void mouseEntered(Object source, C start) {}
    public void mouseExited(Object source, C start) {}
    public void mouseMoved(Object source, C start) {}
    public void mouseDragInitiated(Object source, C start) {}

    public void mouseDragged(Object source, C current) {
        int index = entry.getActiveIndex();
        if (index != -1)
            setPoint(index, current);
    }
    public void mouseDragCompleted(Object source, C end) {
        mouseDragged(source, end);
    }
}
