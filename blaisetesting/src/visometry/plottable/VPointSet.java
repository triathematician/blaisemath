/**
 * VPointSet.java
 * Created on Jul 30, 2009
 */

package visometry.plottable;

import java.awt.geom.Point2D;
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


    //
    // MOUSE METHODS
    //

    private transient int dragIndex = -1;
    private transient C start = null, startDrag = null;

    public void mouseEntered(Object source, C start) {}
    public void mouseExited(Object source, C start) {}
    public void mouseMoved(Object source, C start) {}
    public void mouseDragInitiated(Object source, C start) {
        dragIndex = entry.getActiveIndex();
        if (dragIndex != -1) {
            this.start = getPoint(dragIndex);
            this.startDrag = start;
        }
    }
    public void mouseDragged(Object source, C current) {
        if (dragIndex != -1) {
            if (current instanceof Point2D && start instanceof Point2D) {
                Point2D ps = (Point2D) start;
                Point2D psd = (Point2D) startDrag;
                Point2D pcd = (Point2D) current;
                Point2D.Double relative = new Point2D.Double(
                        ps.getX() + pcd.getX() - psd.getX(), ps.getY() + pcd.getY() - psd.getY()
                        );
                setPoint(dragIndex, (C) relative);
            } else
                setPoint(dragIndex, current);
        }
    }
    public void mouseDragCompleted(Object source, C end) {
        mouseDragged(source, end);
        dragIndex = -1;
    }
}
