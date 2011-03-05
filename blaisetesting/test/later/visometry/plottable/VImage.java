/**
 * VImage.java
 * Created on Aug 9, 2010
 */

package later.visometry.plottable;

import visometry.DynamicPlottable;
import java.awt.Image;
import java.awt.geom.Point2D;
import graphics.GraphicImage;
import primitive.style.temp.ImageStyle;
import deprecated.visometry.VMouseDragListener;
import deprecated.visometry.DraggableVGraphicEntry;
import visometry.graphics.VGraphicEntry;

/**
 * <p>
 *   <code>VImage</code> displays draggable images on a plot canvas.
 * </p>
 *
 * @author Elisha Peterson
 */
public class VImage<C> extends DynamicPlottable<C>
        implements VMouseDragListener<C> {

    /** Stores the table entry. */
    protected VGraphicEntry entry;

    /** Constructs w/ default stlye. */
    public VImage(C anchor, Image image) {
        addPrimitive(entry = new DraggableVGraphicEntry(new GraphicImage<C>(anchor, image), new ImageStyle(), this));
    }

    /** Constructs w/ specified style. */
    public VImage(C anchor, Image image, ImageStyle style) {
        addPrimitive(entry = new DraggableVGraphicEntry(new GraphicImage<C>(anchor, image), style, this));
    }

    @Override
    public String toString() {
        return "VImage[" + entry.local + "]";
    }

    /** @return image style */
    public ImageStyle getStyle() { return (ImageStyle) entry.renderer; }
    /** @param newStyle new image style */
    public void setStyle(ImageStyle newStyle) { if (entry.renderer != newStyle) { entry.renderer = newStyle; firePlottableStyleChanged(); } }

    /** @return current location of the image's anchor */
    public C getAnchor() {
        return ((GraphicImage<C>)entry.local).anchor;
    }
    /** Sets location of image's anchor point */
    public void setAnchor(C value) {
        if (value == null)
            throw new IllegalArgumentException("Non-null value required.");
        if (!value.equals(getAnchor())) {
            GraphicImage<C> gs = (GraphicImage<C>) entry.local;
            gs.setAnchor(value);
            entry.needsConversion = true;
            firePlottableChanged();
        }
    }

    /** @return current location of image's corner */
    public C getCorner() {
        return ((GraphicImage<C>)entry.local).corner;
    }
    /** Sets location of image's corner point */
    public void setCorner(C value) {
        if ((value == null && getCorner() != null) || !value.equals(getCorner())) {
            GraphicImage<C> gs = (GraphicImage<C>) entry.local;
            gs.setCorner(value);
            entry.needsConversion = true;
            firePlottableChanged();
        }
    }

    public Image getImage() {
        return ((GraphicImage<C>)entry.local).image;
    }

    public void setImage(Image img) {
        if (img == null)
            throw new IllegalArgumentException("Non-null value required.");
        if (img != getImage()) {
            ((GraphicImage<C>)entry.local).image = img;
            entry.needsConversion = true;
            firePlottableChanged();
        }
    }

    //
    // MOUSE METHODS
    //

    private transient C startA = null, startC, startDrag = null;

    public void mouseEntered(Object source, C start) {}
    public void mouseExited(Object source, C start) {}
    public void mouseMoved(Object source, C start) {}
    public void mouseDragInitiated(Object source, C start) { startA = getAnchor(); startC = getCorner(); startDrag = start; }
    public void mouseDragged(Object source, C current) {
        if (current instanceof Point2D && startA instanceof Point2D) {
            Point2D ps = (Point2D) startA;
            Point2D psd = (Point2D) startDrag;
            Point2D pcd = (Point2D) current;
            Point2D.Double relative = new Point2D.Double(
                    ps.getX() + pcd.getX() - psd.getX(), ps.getY() + pcd.getY() - psd.getY()
                    );
            setAnchor((C) relative);

            if (startC != null) {
                ps = (Point2D) startC;
                relative = new Point2D.Double(
                    ps.getX() + pcd.getX() - psd.getX(), ps.getY() + pcd.getY() - psd.getY()
                    );
                setCorner((C) relative);
            }
        } else
            setAnchor(current);
    }
    public void mouseDragCompleted(Object source, C end) { mouseDragged(source, end); }

}
