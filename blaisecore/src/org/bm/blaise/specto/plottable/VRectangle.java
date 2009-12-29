/**
 * VPoint.java
 * Created on Jul 30, 2009
 */

package org.bm.blaise.specto.plottable;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import org.bm.blaise.specto.primitive.ShapeStyle;
import org.bm.blaise.specto.visometry.AbstractDynamicPlottable;
import org.bm.blaise.specto.visometry.VisometryGraphics;
import org.bm.blaise.specto.visometry.VisometryMouseEvent;

/**
 * <p>
 *   <code>VRectangle</code> is a rectangle between a maximum point and a minimum point,
 *   drawn by the underlying <code>VisometryGraphics</code>. The rectangle's sides are
 *   straight in the window's coordinate system, but not necessarily in the underlying coordinates.
 *   All four corners may be dragged.
 * </p>
 *
 * @author Elisha Peterson
 */
public class VRectangle<C> extends AbstractDynamicPlottable<C> {

    ShapeStyle style = new ShapeStyle(Color.BLACK, Color.GRAY);

    protected C value1;
    protected C value2;

    /** Construct with two of rectangle's vertices.
     * @param value1 first vertex of rectangle
     * @param value2 second vertex of rectangle
     */
    public VRectangle(C value1, C value2) {
        this.value1 = value1;
        this.value2 = value2;
    }

    //
    //
    // BEAN PATTERNS
    //
    //

    /** @return coordinates of 1st vertex of the rectangle */
    public C getPoint1() {
        return value1;
    }

    public void setPoint1(C value1) {
        if (value1 == null) {
            throw new NullPointerException();
        }
        if (!value1.equals(this.value1)) {
            this.value1 = value1;
            fireStateChanged();
        }
    }

    /** @return coordinates of 2nd vertex of the rectangle */
    public C getPoint2() {
        return value2;
    }

    public void setPoint2(C value2) {
        if (value2 == null) {
            throw new NullPointerException();
        }
        if (!value2.equals(this.value2)) {
            this.value2 = value2;
            fireStateChanged();
        }
    }

    public ShapeStyle getStyle() {
        return style;
    }

    public void setStyle(ShapeStyle style) {
        this.style = style;
    }

    

    //
    //
    // PAINTING
    //
    //

    @Override
    public void paintComponent(VisometryGraphics<C> vg) {
        visrec = vg.getRectangle(value1, value2);
        vg.setShapeStyle(style);
        vg.drawWinShape(visrec);
    }

    //
    //
    // DYNAMIC EDITING
    //
    //

    transient Rectangle2D visrec = null;
    transient Rectangle2D startrec = null;
    transient DragMode dragMode = DragMode.NONE;
    transient Point pressedAt = null;
    final static int LOCK = 4;

    public boolean isClickablyCloseTo(VisometryMouseEvent<C> e) {
        if (visrec == null) {
            return false;
        }
        Point pt = e.getWindowPoint();
        // determine region in which mouse was clicked
        return (pt.x >= visrec.getMinX() - LOCK) && (pt.x <= visrec.getMaxX() + LOCK)
                && (pt.y >= visrec.getMinY() - LOCK) && (pt.y <= visrec.getMaxY() + LOCK);
    }

    @Override
    public void mousePressed(VisometryMouseEvent<C> e) {
        if (visrec == null) {
            return;
        }
        Point pt = e.getWindowPoint();
        // determine region in which mouse was clicked
        int xZone =
            pt.x < visrec.getMinX() - LOCK ? 0 :
                pt.x < visrec.getMinX() + LOCK ? 1 :
                    pt.x < visrec.getMaxX() - LOCK ? 2 :
                        pt.x < visrec.getMaxX() + LOCK ? 3 : 4;
        int yZone =
            pt.y < visrec.getMinY() - LOCK ? 0 :
                pt.y < visrec.getMinY() + LOCK ? 1 :
                    pt.y < visrec.getMaxY() - LOCK ? 2 :
                        pt.y < visrec.getMaxY() + LOCK ? 3 : 4;
        dragMode = DragMode.getState(xZone, yZone);
        adjusting = (dragMode != DragMode.NONE);
        if (adjusting) {
            pressedAt = pt;
            startrec = (Rectangle2D) visrec.clone();
        }
    }

    @Override
    public void mouseDragged(VisometryMouseEvent<C> e) {
        if (adjusting) {
            Rectangle2D newRec = dragMode.getRec(startrec, pressedAt, e.getWindowPoint());
            Point2D.Double newMin = new Point2D.Double(newRec.getMinX(), newRec.getMaxY());
            Point2D.Double newMax = new Point2D.Double(newRec.getMaxX(), newRec.getMinY());
            setPoint1(e.getCoordinateOf(newMin));
            setPoint2(e.getCoordinateOf(newMax));
        }
    }

    @Override
    public void mouseReleased(VisometryMouseEvent<C> e) {
        super.mouseReleased(e);
        pressedAt = null;
    }



    static enum DragMode {
        NW() {
            Rectangle2D getRec(Rectangle2D initial, Point start, Point end){
                Point diff = new Point(end.x - start.x, end.y - start.y);
                return new Rectangle2D.Double( 
                        initial.getMinX() + diff.x, initial.getMinY() + diff.y,
                        initial.getWidth() - diff.x, initial.getHeight() - diff.y );
            }
        }, N() {
            Rectangle2D getRec(Rectangle2D initial, Point start, Point end){
                Point diff = new Point(0, end.y - start.y);
                return new Rectangle2D.Double( 
                        initial.getMinX(), initial.getMinY() + diff.y,
                        initial.getWidth(), initial.getHeight() - diff.y );
            }
        }, NE() {
            Rectangle2D getRec(Rectangle2D initial, Point start, Point end){
                Point diff = new Point(end.x - start.x, end.y - start.y);
                return new Rectangle2D.Double(
                        initial.getMinX(), initial.getMinY() + diff.y,
                        initial.getWidth() + diff.x, initial.getHeight() - diff.y );
            }
        }, E() {
            Rectangle2D getRec(Rectangle2D initial, Point start, Point end){
                Point diff = new Point(end.x - start.x, 0);
                return new Rectangle2D.Double( 
                        initial.getMinX(), initial.getMinY(),
                        initial.getWidth() + diff.x, initial.getHeight() );
            }
        }, SE() {
            Rectangle2D getRec(Rectangle2D initial, Point start, Point end){
                Point diff = new Point(end.x - start.x, end.y - start.y);
                return new Rectangle2D.Double(
                        initial.getMinX(), initial.getMinY(),
                        initial.getWidth() + diff.x, initial.getHeight() + diff.y );
            }
        }, S() {
            Rectangle2D getRec(Rectangle2D initial, Point start, Point end){
                Point diff = new Point(0, end.y - start.y);
                return new Rectangle2D.Double(
                        initial.getMinX(), initial.getMinY(),
                        initial.getWidth(), initial.getHeight() + diff.y );
            }
        }, SW() {
            Rectangle2D getRec(Rectangle2D initial, Point start, Point end){
                Point diff = new Point(end.x - start.x, end.y - start.y);
                return new Rectangle2D.Double( 
                        initial.getMinX() + diff.x, initial.getMinY(),
                        initial.getWidth() - diff.x, initial.getHeight() + diff.y );
            }
        }, W() {
            Rectangle2D getRec(Rectangle2D initial, Point start, Point end){
                Point diff = new Point(end.x - start.x, 0);
                return new Rectangle2D.Double( 
                        initial.getMinX() + diff.x, initial.getMinY(),
                        initial.getWidth() - diff.x, initial.getHeight() );
            }
        }, CENTRAL() {
            Rectangle2D getRec(Rectangle2D initial, Point start, Point end){
                Point diff = new Point(end.x - start.x, end.y - start.y);
                return new Rectangle2D.Double(
                        initial.getMinX() + diff.x, initial.getMinY() + diff.y,
                        initial.getWidth(), initial.getHeight());
            }
        }, NONE() {
            Rectangle2D getRec(Rectangle2D initial, Point start, Point end){
                return (Rectangle2D) initial.clone();
            }
        };

        abstract Rectangle2D getRec(Rectangle2D initial, Point start, Point end);

        static DragMode getState(int i, int j) {
            if (i == 1) {
                return (j==1) ? NW : (j==2) ? W : SW;
            } else if (i == 2) {
                return (j==1) ? N : (j==2) ? CENTRAL : S;
            } else if (i == 3) {
                return (j==1) ? NE : (j==2) ? E : SE;
            }
            return NONE;
        }
    }
}
