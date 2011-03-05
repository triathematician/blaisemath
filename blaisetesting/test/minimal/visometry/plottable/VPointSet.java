/**
 * VPointSet.java
 * Created on Jul 30, 2009
 */

package minimal.visometry.plottable;

import java.awt.geom.Point2D;
import java.lang.reflect.Array;
import java.util.Arrays;
import primitive.style.temp.PointLabeledStyle;
import deprecated.visometry.VMouseDragListener;
import deprecated.visometry.VMouseInputListener;

/**
 * <p>
 *   <code>VPointSet</code> is a set of points in the visometry. Each point is
 *   displayed as a separate entity. Points may be dragged around the screen. If this
 *   is registered as a plot component's <i>mouse input listener</i>, then screen-clicks
 *   result in points being added to the plottable.
 * </p>
 * @author Elisha Peterson
 * @seealso VPath
 */
public class VPointSet<C> extends VAbstractPointArray<C>
        implements VMouseDragListener<C>, VMouseInputListener<C> {

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
    public PointLabeledStyle getPointStyle() { return (PointLabeledStyle) entry.renderer; }
    /** Set current style of point for this plottable */
    public void setPointStyle(PointLabeledStyle newValue) { if (entry.renderer != newValue) { entry.renderer = newValue; firePlottableStyleChanged(); } }


    //
    // MOUSE DRAG METHODS
    //

    private transient int dragIndex = -1;
    private transient C start = null, startDrag = null;

    private static final boolean MOUSEVERBOSE = false;
    private static final void verboseOut(String s, Object pt) { if (MOUSEVERBOSE) System.out.println("VPointSet: " + s + " @ " + pt); }

    public void mouseEntered(Object source, C current) { verboseOut("mouseEntered", current); }
    public void mouseExited(Object source, C current) { verboseOut("mouseExited", current); }
    public void mouseMoved(Object source, C current) { verboseOut("mouseMoved", current); }
    public void mouseDragInitiated(Object source, C start) { verboseOut("mouseDragInitiated", start);
        dragIndex = entry.getActiveIndex();
        if (dragIndex != -1) {
            this.start = getPoint(dragIndex);
            this.startDrag = start;
        }
    }
    public void mouseDragged(Object source, C current) { verboseOut("mouseDragged", current);
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
    public void mouseDragCompleted(Object source, C end) { verboseOut("mouseDragCompleted", end);
        mouseDragged(source, end);
        dragIndex = -1;
    }

    //
    // MOUSE INPUT METHODS
    //

    public void mouseClicked(C point) { verboseOut("i:mouseClicked", point);
        // click adds a point to the array
        C[] newArr = (C[]) Array.newInstance(point.getClass(), points.length + 1);
        System.arraycopy(points, 0, newArr, 0, points.length);
        newArr[points.length] = point;
        setPoint(newArr);
    }

    public boolean handlesDragEvents() { return false; }
    public void mouseDragInitiated(C start) { verboseOut("i:mouseDragInitiated", start); }
    public void mouseDragged(C current) { verboseOut("i:mouseDragged", current); }
    public void mouseDragCompleted(C end) { verboseOut("i:mouseDragCompleted", end); }
}
