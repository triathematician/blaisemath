/**
 * VImage.java
 * Created on Aug 9, 2010
 */

package visometry.plottable;

import java.awt.Image;
import java.awt.geom.Point2D;
import primitive.GraphicImage;
import primitive.style.ImageStyle;
import visometry.PointDragListener;
import visometry.VDraggablePrimitiveEntry;
import visometry.VPrimitiveEntry;

/**
 * <p>
 *   <code>VImage</code> displays draggable images on a plot canvas.
 * </p>
 *
 * @author Elisha Peterson
 */
public class VImage<C> extends DynamicPlottable<C>
        implements PointDragListener<C> {

    /** Stores the table entry. */
    protected VPrimitiveEntry entry;

    /** Constructs w/ specified style. */
    public VImage(C anchor, Image image, ImageStyle style) {
        addPrimitive(entry = new VDraggablePrimitiveEntry(new GraphicImage<C>(anchor, image), style, this));
    }

    @Override
    public String toString() {
        return "VImage[" + entry.local + "]";
    }

    /** @return image style */
    public ImageStyle getStyle() { return (ImageStyle) entry.style; }
    /** @param newStyle new image style */
    public void setStyle(ImageStyle newStyle) { if (entry.style != newStyle) { entry.style = newStyle; firePlottableStyleChanged(); } }

    /** @return current location of the coordinate */
    public C getPoint() {
        return ((GraphicImage<C>)entry.local).anchor;
    }

    public void setPoint(C value) {
        if (value == null)
            throw new NullPointerException();
        if (!value.equals(getPoint())) {
            GraphicImage<C> gs = (GraphicImage<C>) entry.local;
            gs.setAnchor(value);
            entry.needsConversion = true;
            firePlottableChanged();
        }
    }

    //
    // MOUSE METHODS
    //

    private transient C start = null, startDrag = null;

    public void mouseEntered(Object source, C start) {}
    public void mouseExited(Object source, C start) {}
    public void mouseMoved(Object source, C start) {}
    public void mouseDragInitiated(Object source, C start) { this.start = getPoint(); this.startDrag = start; }
    public void mouseDragged(Object source, C current) {
        if (current instanceof Point2D && start instanceof Point2D) {
            Point2D ps = (Point2D) start;
            Point2D psd = (Point2D) startDrag;
            Point2D pcd = (Point2D) current;
            Point2D.Double relative = new Point2D.Double(
                    ps.getX() + pcd.getX() - psd.getX(), ps.getY() + pcd.getY() - psd.getY()
                    );
            setPoint((C) relative);
        } else
            setPoint(current);
    }
    public void mouseDragCompleted(Object source, C end) { setPoint(end); }

}
