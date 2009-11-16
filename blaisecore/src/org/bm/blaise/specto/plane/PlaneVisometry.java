/**
 * PlaneVisometry.java
 * Created on Jul 30, 2009
 */
package org.bm.blaise.specto.plane;

import scio.coordinate.utils.RandomPointGenerator;
import java.awt.geom.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import org.bm.blaise.specto.visometry.*;

/**
 * <p>
 *   <code>PlaneVisometry</code> converts a standard Euclidean plane onto a window.
 *   The class ensures that all of a desired range is kept within the window, and that
 *   the aspect ratio is also maintained. The displayed window is the smallest possible
 *   around the desired range with the specified aspect.
 * </p>
 *
 * @author Elisha Peterson
 */
public class PlaneVisometry implements Visometry<Point2D.Double>, RandomPointGenerator<Point2D.Double> {

    //
    //
    // PROPERTIES
    //
    //
    /** Stores the boundary of the display window. */
    RectangularShape windowBounds;
    /** Stores the desired range of values. The class should ensure that this entire range is displayed. */
    Point2D.Double desiredMin = new Point2D.Double(0, 0);
    /** Stores the desired range of values. The class should ensure that this entire range is displayed. */
    Point2D.Double desiredMax = new Point2D.Double(1, 1);
    /** Stores the window's AspectRatio. */
    double aspectRatio = 1.0;
    /** Stores the affine transformation that converts the actual range to the window bounds. */
    transient AffineTransform at = new AffineTransform();
    /** Stores the actual range of values that is displayed. */
    transient Rectangle2D.Double displayRange = new Rectangle2D.Double();

    //
    //
    // BEAN PATTERNS
    //
    //

    /** @return bounding rectangle provided by the window. */
    public RectangularShape getWindowBounds() {
        return windowBounds;
    }

    /**
     * Sets window bounds.
     * Recomputes transformation after setting.
     */
    public void setWindowBounds(RectangularShape newBounds) {
        if (newBounds == null) {
            throw new NullPointerException();
        }
        if (!newBounds.equals(windowBounds)) {
            this.windowBounds = newBounds;
            // TODO - operate better here!
            try {
                computeTransformation();
            } catch (IllegalStateException e) {
            }
        }
    }

    /** @return aspect ratio of the plot */
    public double getAspectRatio() {
        return aspectRatio;
    }

    /** 
     * Sets aspect ratio of the plot. Must be positive. This is guaranteed to be obeyed.
     * Recomputes transformation after setting.
     * @param aspectRatio new aspect ratio
     * @throws IllegalArgumentException if the argument is zero or negative
     */
    public void setAspectRatio(double aspectRatio) {
        if (aspectRatio <= 0) {
            throw new IllegalArgumentException("Aspect ratio must be positive!");
        }
        this.aspectRatio = aspectRatio;
        // TODO - operate better here!
        try {
            computeTransformation();
        } catch (IllegalStateException e) {
        }
    }

    /**
     * @return desired range as a rectanlge
     */
    public Rectangle2D.Double getDesiredRange() {
        return new Rectangle2D.Double(desiredMin.x, desiredMin.y, desiredMax.x - desiredMin.x, desiredMax.y - desiredMin.y);
    }

    /**
     * Sets desired range based on bouning rectangle.
     */
    public void setDesiredRange(Rectangle2D.Double range) {
        setDesiredRange(range.getMinX(), range.getMinY(), range.getMaxX(), range.getMaxY());
    }

    /**
     * Set desired range of values.
     * Recomputes transformation after setting.
     * @param minX
     * @param minY
     * @param maxX
     * @param maxY
     */
    public void setDesiredRange(double minX, double minY, double maxX, double maxY) {
        if (minX == maxX || minY == maxY) {
            throw new IllegalArgumentException("Range must be nonempty");
        }
        // ensure proper order
        if (minX > maxX) {
            double swap = minX;
            minX = maxX;
            maxX = swap;
        }
        if (minY > maxY) {
            double swap = minY;
            minY = maxY;
            maxY = swap;
        }
        desiredMin = new Point2D.Double(minX, minY);
        desiredMax = new Point2D.Double(maxX, maxY);
        //System.out.println("range: "+minX+","+minY+","+maxX+","+maxY);
        // TODO - operate better here!
        try {
            computeTransformation();
        } catch (IllegalStateException e) {
        }
    }

    public Rectangle2D.Double getVisibleRange() {
        return displayRange;
    }

    public Point2D.Double getMinPointVisible() {
        return new Point2D.Double(displayRange.x, displayRange.y);
    }

    public Point2D.Double getMaxPointVisible() {
        return new Point2D.Double(displayRange.x + displayRange.width, displayRange.y + displayRange.height);
    }

    /** @return x scaling to transform local to window coordinates */
    public double getScaleX() {
        return at.getScaleX();
    }

    /** @return y scaling to transform local to window coordinates */
    public double getScaleY() {
        return at.getScaleY();
    }

    public void computeTransformation() throws IllegalStateException {
        // ensure proper parameters
        if (windowBounds == null || windowBounds.getWidth() == 0 || windowBounds.getHeight() == 0 || desiredMin == null || desiredMax == null || aspectRatio == 0.0 || aspectRatio == Double.NaN) {
            throw new IllegalStateException();
        }
        //System.out.println("recompute transformation");
        //System.out.println("bounds: "+windowBounds);
        //System.out.println("desired: "+desiredMin+"   "+desiredMax);

        // compute displayed range of values

        double winAspect = windowBounds.getWidth() / windowBounds.getHeight();                  // aspect ratio of the window
        double displayAspect = winAspect / aspectRatio;                                         // aspect ratio that will be used
        double desiredAspect = (desiredMax.x - desiredMin.x) / (desiredMax.y - desiredMin.y);   // desired aspect ratio according to bounds; will not be used

        if (displayAspect >= desiredAspect) {       // will show more horizontally
            displayRange.y = desiredMin.y;
            displayRange.height = desiredMax.y - desiredMin.y;
            displayRange.width = displayAspect * displayRange.height;
            displayRange.x = 0.5 * (desiredMin.x + desiredMax.x) - 0.5 * displayRange.width;
        } else {                                    // will show more vertically
            displayRange.x = desiredMin.x;
            displayRange.width = desiredMax.x - desiredMin.x;
            displayRange.height = displayRange.width / displayAspect;
            displayRange.y = 0.5 * (desiredMin.y + desiredMax.y) - 0.5 * displayRange.height;
        }
        //System.out.println("display: "+displayRange);

        // Compute the transformation. This converts a regular coordinate to the window coordinate.
        // Note that composition of transforms works backwards, so the process by which a point is
        // converted is reversed from the order in which it is computed.
        //    1. Shift the origin to the lower left.
        //    2. Scale appropriately (y-value is negative since the underlying graphics has y increasing downward).
        //    3. Translate to get inside the window.
        at.setToIdentity();
        at.translate(windowBounds.getMinX(), windowBounds.getMaxY());
        at.scale(windowBounds.getWidth() / displayRange.width, -windowBounds.getHeight() / displayRange.height);
        at.translate(-displayRange.x, -displayRange.y);
        fireStateChanged();
    }

    public Point2D getWindowPointOf(Point2D.Double coordinate) {
        if (at == null) {
            throw new IllegalStateException();
        }
        return at.transform(coordinate, null);
    }

    public Point2D.Double getCoordinateOf(Point2D point) {
        if (at == null) {
            throw new IllegalStateException();
        }
        try {
            Point2D result = at.inverseTransform(point, null);
            if (result instanceof Point2D.Double) {
                return (Point2D.Double) result;
            } else {
                return new Point2D.Double(result.getX(), result.getY());
            }
        } catch (NoninvertibleTransformException ex) {
            Logger.getLogger(PlaneVisometry.class.getName()).log(Level.SEVERE, null, ex);
        }
        throw new IllegalStateException();
    }

    //
    //
    // SAMPLING ALGORITHMS
    //
    //

    public Point2D.Double randomValue() {
        return new Point2D.Double(
                displayRange.x + Math.random() * displayRange.width,
                displayRange.y + Math.random() * displayRange.height);
    }

    //
    //
    // EVENT HANDLING
    //
    //
    
    /**
     * Handles a change event. By default, passes the ChangeEvent along
     * to interested listeners (particularly the parent class), provided this class
     * itself did not originate the event.
     * @param e the change event
     */
    public void stateChanged(ChangeEvent e) {
        if (!e.getSource().equals(this)) {
            changeEvent = e;
            fireStateChanged();
        }
    }

    // Event handling code copied from DefaultBoundedRangeModel.
    protected ChangeEvent changeEvent = new ChangeEvent(this);
    protected EventListenerList listenerList = new EventListenerList();

    /**
     * Removes a listener from the list of classes receiving <code>ChangeEvent</code>s
     * @param l the listener
     */
    public void addChangeListener(ChangeListener l) {
        listenerList.add(ChangeListener.class, l);
    }

    /**
     * Adds a listener to receive <code>ChangeEvent</code>s
     * @param l the listener
     */
    public void removeChangeListener(ChangeListener l) {
        listenerList.remove(ChangeListener.class, l);
    }

    /**
     * Fires the change event to listeners.
     */
    protected void fireStateChanged() {
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ChangeListener.class) {
                if (changeEvent == null) {
                    return;
                }
                ((ChangeListener) listeners[i + 1]).stateChanged(changeEvent);
            }
        }
    }
}