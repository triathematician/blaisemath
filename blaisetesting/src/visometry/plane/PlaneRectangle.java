/**
 * PlaneRectangle.java
 * Created Aug 2009
 * Updated Apr 8, 2010
 */

package visometry.plane;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import primitive.style.HandleStyle;
import primitive.style.ShapeStyle;
import visometry.VMouseDragListener;
import visometry.VDraggablePrimitiveEntry;
import visometry.plottable.DynamicPlottable;

/**
 * <p>
 *   <code>PlaneRectangle</code> is a rectangle between a maximum point and a minimum point,
 *   drawn by the underlying <code>VisometryGraphics</code>.
 * </p>
 *
 * @author Elisha Peterson
 */
public class PlaneRectangle extends DynamicPlottable<Point2D.Double>
        implements VMouseDragListener<Point2D.Double> {

    /** Boundaries. */
    double minX, minY, maxX, maxY;
    /** Entry containing the rectangle's shape as a Rectangle2D.Double, and the corresponding shape style. */
    VDraggablePrimitiveEntry entry;
    /** Entry containing the control boxes. */
    VDraggablePrimitiveEntry controlEntry;

    /** Construct with default boundaries: (1,1) to (2,2). */
    public PlaneRectangle() {
        this(1,1,2,2);
    }

    /** Construct with specified boundaries.
     * @param minx minimum x value
     * @param miny minimum y value
     * @param maxx maximum x value
     * @param maxy maximum y value
     */
    public PlaneRectangle(double minX, double minY, double maxX, double maxY) {
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
        addPrimitive(entry = new VDraggablePrimitiveEntry(new Rectangle2D.Double(), new ShapeStyle(Color.BLACK, Color.GRAY), this));
        addPrimitive(controlEntry = new VDraggablePrimitiveEntry(null, new HandleStyle(), this));
        controlEntry.visible = false;
    }

    /** Set boundaries of rectangle.
     * @param minx minimum x value
     * @param miny minimum y value
     * @param maxx maximum x value
     * @param maxy maximum y value
     */
    public void setBounds(double minx, double miny, double maxx, double maxy) {
        this.minX = minx;
        this.minY = miny;
        this.maxX = maxx;
        this.maxY = maxy;
        firePlottableChanged();
    }

    public double getMaxX() {
        return maxX;
    }

    public void setMaxX(double x) {
        if (this.maxX != x) {
            this.maxX = x;
            firePlottableChanged();
        }
    }

    public double getMaxY() {
        return maxY;
    }

    public void setMaxY(double y) {
        if (this.maxY != y) {
            this.maxY = y;
            firePlottableChanged();
        }
    }

    public double getMinX() {
        return minX;
    }

    public void setMinX(double x) {
        if (this.minX != x) {
            this.minX = x;
            firePlottableChanged();
        }
    }

    public double getMinY() {
        return minY;
    }

    public void setMinY(double y) {
        if (this.minY != y) {
            this.minY = y;
            firePlottableChanged();
        }
    }

    /** @return current style of stroke for this plottable */
    public ShapeStyle getStyle() { return (ShapeStyle) entry.style; }
    /** Set current style of stroke for this plottable */
    public void setStyle(ShapeStyle newValue) { if (entry.style != newValue) { entry.style = newValue; firePlottableStyleChanged(); } }

    @Override
    protected void recompute() {
        Rectangle2D.Double shape = (Rectangle2D.Double) entry.local;
        shape.x = Math.min(minX, maxX);
        shape.y = Math.min(minY, maxY);
        shape.width =  Math.abs(minX - maxX);
        shape.height = Math.abs(minY - maxY);
        entry.needsConversion = true;
        controlEntry.local = new Point2D.Double[] {
            new Point2D.Double(minX, minY),
            new Point2D.Double(minX, .5*(minY+maxY)),
            new Point2D.Double(minX, maxY),
            new Point2D.Double(.5*(minX+maxX), maxY),
            new Point2D.Double(maxX, maxY),
            new Point2D.Double(maxX, .5*(minY+maxY)),
            new Point2D.Double(maxX, minY),
            new Point2D.Double(.5*(minX+maxX), minY)
        };
        controlEntry.needsConversion = true;
        needsComputation = false;
    }

    //
    // MOUSE HANDLING
    //

    int dragMode;
    Point2D.Double start;
    double oldMinX, oldMinY, oldMaxX, oldMaxY;

    public void mouseEntered(Object source, Point2D.Double current) {
        controlEntry.visible = true;
        firePlottableStyleChanged();
    }
    public void mouseExited(Object source, Point2D.Double current) {
        controlEntry.visible = false;
        firePlottableStyleChanged();
    }

    public void mouseMoved(Object src, Point2D.Double current) {}

    public void mouseDragInitiated(Object src, Point2D.Double start) {
        if (src == controlEntry)
            dragMode = controlEntry.getActiveIndex();
        else
            dragMode = -1;
        this.start = start;
        oldMinX = minX;
        oldMinY = minY;
        oldMaxX = maxX;
        oldMaxY = maxY;
    }

    public void mouseDragged(Object src, Point2D.Double current) {
        switch (dragMode) {
            case -1:
                setBounds(oldMinX + current.x-start.x, oldMinY + current.y-start.y, oldMaxX + current.x-start.x, oldMaxY + current.y-start.y);
                break;
            case 0:
                setBounds(oldMinX + current.x-start.x, oldMinY + current.y-start.y, oldMaxX, oldMaxY);
                break;
            case 1:
                setBounds(oldMinX + current.x-start.x, oldMinY, oldMaxX, oldMaxY);
                break;
            case 2:
                setBounds(oldMinX + current.x-start.x, oldMinY, oldMaxX, oldMaxY + current.y-start.y);
                break;
            case 3:
                setBounds(oldMinX, oldMinY, oldMaxX, oldMaxY + current.y-start.y);
                break;
            case 4:
                setBounds(oldMinX, oldMinY, oldMaxX + current.x-start.x, oldMaxY + current.y-start.y);
                break;
            case 5:
                setBounds(oldMinX, oldMinY, oldMaxX + current.x-start.x, oldMaxY);
                break;
            case 6:
                setBounds(oldMinX, oldMinY + current.y-start.y, oldMaxX + current.x-start.x, oldMaxY);
                break;
            case 7:
                setBounds(oldMinX, oldMinY + current.y-start.y, oldMaxX, oldMaxY);
                break;
        }
    }

    public void mouseDragCompleted(Object src, Point2D.Double end) {
        mouseDragged(src, end);
    }
    
}
