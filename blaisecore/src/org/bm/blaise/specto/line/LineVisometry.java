/**
 * LineVisometry.java
 * Created on Sep 19, 2009
 */
package org.bm.blaise.specto.line;

import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.RectangularShape;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import org.bm.blaise.specto.visometry.Visometry;

/**
 * <p>
 *   <code>LineVisometry</code> converts a set of real numbers onto a line.
 * </p>
 *
 * @author Elisha Peterson
 */
public class LineVisometry
        implements Visometry<Double> {

    //
    // PROPERTIES
    //

    /** Stores the boundary of the display window. */
    RectangularShape windowBounds;
    /** Stores the desired range of values. The class should ensure that this entire range is displayed. */
    Double minimum = -5.0;
    /** Stores the desired range of values. The class should ensure that this entire range is displayed. */
    Double maximum = 5.0;
    /** Stores the affine transformation that converts the actual range to the window bounds. */
    AffineTransform at;
    /** Stores the inverse of the transform */
    transient AffineTransform atInverse;

    //
    // CONSTRUCTORS
    //

    public LineVisometry() {
        at = new AffineTransform();
    }
    

    //
    // BEAN PATTERNS
    //
    
    /**
     * Retrieve the underlying window's boundary.
     * @return bounding rectangle provided by the window.
     */
    public RectangularShape getWindowBounds() {
        return windowBounds;
    }

    /**
     * Sets window bounds.
     * Recomputes transformation after setting.
     */
    public void setWindowBounds(RectangularShape newBounds) {
        if (newBounds == null)
            throw new NullPointerException();
        
        if (!newBounds.equals(windowBounds)) {
            this.windowBounds = newBounds;
            try {
                computeTransformation();
            } catch (IllegalStateException ex) {
                this.windowBounds = null;
            }
        }
    }

    /** Retrieve current maximum coordinate */
    public Double getMaxPointVisible() {
        return maximum;
    }

    /** Set current maximum coordinate */
    public void setMaxPointVisible(Double maximum) {
        setDesiredRange(minimum, maximum);
    }

    /** Retrieve current minimum coordinate */
    public Double getMinPointVisible() {
        return minimum;
    }

    /** Set current minimum coordinate */
    public void setMinPointVisible(Double minimum) {
        setDesiredRange(minimum, maximum);
    }

    /**
     * Set desired range of values.
     * Recomputes transformation after setting.
     * @param min
     * @param max
     */
    public void setDesiredRange(double min, double max) {
        if (min == max)
            throw new IllegalArgumentException("Range must be nonempty");

        minimum = Math.min(min, max);
        maximum = Math.max(min, max);

        try {
            computeTransformation();
        } catch (IllegalStateException ex) {
            this.windowBounds = null;
        }
    }

    /** @return x scaling to transform local to window coordinates */
    public double getScaleX() {
        return at.getScaleX();
    }

    /** Margin on the sides of the window. */
    int MARGIN = 10;

    public void computeTransformation() throws IllegalStateException {
        // ensure proper parameters
        if (windowBounds == null || windowBounds.getWidth() == 0 || windowBounds.getHeight() == 0 || minimum == null || maximum == null)
            throw new IllegalStateException();
        //System.out.println("recompute transformation");
        //System.out.println("bounds: "+windowBounds);
        //System.out.println("desired: "+desiredMin+"   "+desiredMax);

        // The axis is transformed horizontally, and centered vertically.
        // The specified margin is left out on either end.
        at.setToIdentity();
        double scaleX = (windowBounds.getWidth() - 2 * MARGIN) / (maximum - minimum);
        at.translate(windowBounds.getMinX() + MARGIN, windowBounds.getMinY() + windowBounds.getHeight() / 2);
        at.scale(scaleX, 1);
        at.translate(-minimum, 0);
        
        try {
            atInverse = at.createInverse();
        } catch (NoninvertibleTransformException ex) {
            throw new IllegalStateException();
        }
        fireStateChanged();
    }

    public Point2D.Double getWindowPointOf(Double coordinate) {
        return (Point2D.Double) at.transform(new Point2D.Double(coordinate, 0), null);
    }

    public Double getCoordinateOf(Point2D point) {
        return atInverse.transform(point, null).getX();
    }

    /**
     * Computes and returns the step size corresponding to a given number of pixels in the x-direction.
     * @param pixelSpacing # of pixels
     * @return coordinate range for specified number of pixels.
     */
    public double getIdealStepForPixelSpacing(double pixelSpacing) {
        return Math.abs(pixelSpacing / getScaleX());
    }

    //
    // EVENT HANDLING
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

    //
    // Event handling code copied from DefaultBoundedRangeModel.
    //

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
        for (int i = listeners.length - 2; i >= 0; i -= 2)
            if (listeners[i] == ChangeListener.class) {
                if (changeEvent == null)
                    return;
                ((ChangeListener) listeners[i + 1]).stateChanged(changeEvent);
            }
    }
}
