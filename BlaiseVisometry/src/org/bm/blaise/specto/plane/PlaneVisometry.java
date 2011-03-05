/**
 * PlaneVisometry.java
 * Created on Jul 30, 2009
 */
package org.bm.blaise.specto.plane;

import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import org.bm.blaise.scio.coordinate.sample.SampleSet;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.bm.blaise.scio.coordinate.DomainHint;
import org.bm.blaise.scio.coordinate.RealIntervalSamplerProvider;
import org.bm.blaise.scio.coordinate.RealIntervalStepSampler;
import org.bm.blaise.scio.coordinate.ScreenSampleDomainProvider;
import org.bm.blaise.scio.coordinate.SquareDomainBroadcaster;
import org.bm.blaise.scio.coordinate.SquareDomainStepSampler;
import org.bm.blaise.specto.Visometry;
import util.ChangeBroadcaster;
import util.DefaultChangeBroadcaster;

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
public class PlaneVisometry implements Visometry<Point2D.Double>,
        ChangeBroadcaster, ChangeListener {

    /** Stores the boundary of the display window, in window coordinates. */
    RectangularShape windowBounds;
    /** Stores the desired range of values. The class should ensure that this entire range is displayed. */
    Point2D.Double desiredMin = new Point2D.Double(-5, -5);
    /** Stores the desired range of values. The class should ensure that this entire range is displayed. */
    Point2D.Double desiredMax = new Point2D.Double(5, 5);
    /** Stores the window's AspectRatio. */
    double aspectRatio = 1.0;
    /** Stores the affine transformation that converts the actual range to the window bounds. */
    transient AffineTransform at = new AffineTransform();
    /** Stores the actual range of values that is displayed, in local coordiantes. */
    transient Rectangle2D.Double displayRange = new Rectangle2D.Double();

    //
    // BEAN PROPERTIES
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
            try {
                computeTransformation();
            } catch (IllegalStateException e) {
                System.out.println("Unable to calculate new transformation for PlaneVisometry; attempted to set window bounds to " + newBounds);
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
        if (aspectRatio <= 0)
            throw new IllegalArgumentException("Aspect ratio must be positive!");
        this.aspectRatio = aspectRatio;
        // TODO - operate better here!
        try {
            computeTransformation();
        } catch (IllegalStateException e) {
                System.out.println("Unable to calculate new PlaneVisometry transformation; attempted to set aspectRatio = " + aspectRatio);
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
     * @param minX first coordinate min
     * @param minY second coordinate min
     * @param maxX first coordinate max
     * @param maxY second coordinate max
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
        try {
            computeTransformation();
        } catch (IllegalStateException e) {
            System.out.println("Error setting plane visometry transformation: " + e);
        }
    }

    /** @return visible range (in coordinates) */
    public Rectangle2D.Double getVisibleRange() {
        return displayRange;
    }

    /** @return minimum visible coordinate */
    public Point2D.Double getMinPointVisible() {
        return new Point2D.Double(displayRange.x, displayRange.y);
    }

    /** @return maximum visible coordinate */
    public Point2D.Double getMaxPointVisible() {
        return new Point2D.Double(displayRange.x + displayRange.width, displayRange.y + displayRange.height);
    }

    /**
     * Returns the number of pixels/unit in the x direction
     * @return x scaling to transform local to window coordinates
     */
    public double getHorizontalScale() {
        return at.getScaleX();
    }

    /**
     * Returns the number of pixels/unit in the y direction
     * @return y scaling to transform local to window coordinates
     */
    public double getVerticalScale() {
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
        changer.fireStateChanged();
    }

    public Point2D.Double toWindow(Point2D.Double coordinate) {
        if (at == null)
            throw new IllegalStateException();
        if (Double.isInfinite(coordinate.x))
            return getWindowPointOfInfiniteAngle(coordinate.y);
        return (Point2D.Double) at.transform(coordinate, null);
    }

    /**
     * Returns an approximate location of a point at infinity... projects out at least 1000 times the size of the window, relative to the
     * center of the window.
     * @param angle the angle of the point
     * @return an approximate window location for a point at infinity.
     */
    public Point2D.Double getWindowPointOfInfiniteAngle(double angle) {
        double diagonal = 1000*Math.hypot(displayRange.width, displayRange.height);
        return (Point2D.Double) at.transform(new Point2D.Double(
                displayRange.getCenterX() + diagonal * Math.cos(angle),
                displayRange.getCenterY() + diagonal*Math.sin(angle) )
                , null);
    }


    public Point2D.Double toLocal(Point2D point) {
        if (at == null)
            throw new IllegalStateException();
        try {
            Point2D result = at.inverseTransform(point, null);
            if (result instanceof Point2D.Double)
                return (Point2D.Double) result;
            else
                return new Point2D.Double(result.getX(), result.getY());
        } catch (NoninvertibleTransformException ex) {
            Logger.getLogger(PlaneVisometry.class.getName()).log(Level.SEVERE, null, ex);
        }
        throw new IllegalStateException();
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
        if (!e.getSource().equals(this))
            changer.fireStateChanged();
    }

    protected DefaultChangeBroadcaster changer = new DefaultChangeBroadcaster(this);
    public void addChangeListener(ChangeListener l) { changer.addChangeListener(l); }
    public void removeChangeListener(ChangeListener l) { changer.removeChangeListener(l); }


    //
    // DOMAIN METHODS
    //

    RealIntervalSamplerProvider xDomain;
    RealIntervalSamplerProvider yDomain;
    PlaneDomain planeDomain;

    /** @return domain associated with the horizontal axis */
    public RealIntervalSamplerProvider getHorizontalDomain() {
        if (xDomain == null)
            xDomain = new RealIntervalSamplerProvider(this) {
                public double getNewMinimum() { return PlaneVisometry.this.displayRange.x; }
                public double getNewMaximum() { return PlaneVisometry.this.displayRange.x + PlaneVisometry.this.displayRange.width; }
                public double getScale(float pixSpacing) { return Math.abs(pixSpacing / PlaneVisometry.this.getHorizontalScale()); }
            }; 
        return xDomain;
    }

    /** @return domain associated with the vertical axis */
    public RealIntervalSamplerProvider getVerticalDomain() {
        if (yDomain == null)
            yDomain = new RealIntervalSamplerProvider(this) {
                public double getNewMinimum() { return PlaneVisometry.this.displayRange.y; }
                public double getNewMaximum() { return PlaneVisometry.this.displayRange.y + PlaneVisometry.this.displayRange.height; }
                public double getScale(float pixSpacing) { return Math.abs(pixSpacing / PlaneVisometry.this.getVerticalScale()); }
            };
        return yDomain;
    }

    /** @return domain associated with both axes. */
    public SquareDomainBroadcaster getPlaneDomain() { 
        if (planeDomain == null)
            planeDomain = new PlaneDomain();
        return planeDomain;
    }

    /** Domain that combines x and y domains; uses embedded x and y domains. */
    class PlaneDomain extends SquareDomainBroadcaster
            implements ScreenSampleDomainProvider<Point2D.Double>, ChangeListener {
        public PlaneDomain() {
            super(getHorizontalDomain(), getVerticalDomain());
        }
        public SampleSet<Point2D.Double> samplerWithPixelSpacing(final float pixSpacing, DomainHint hint) {
            final SquareDomainStepSampler result = new SquareDomainStepSampler(
                    (RealIntervalStepSampler) getHorizontalDomain().samplerWithPixelSpacing(pixSpacing, hint),
                    (RealIntervalStepSampler) getVerticalDomain().samplerWithPixelSpacing(pixSpacing, hint) );
            addChangeListener(new ChangeListener(){
                public void stateChanged(ChangeEvent e) { 
                    result.setStep1( Math.abs(pixSpacing/getHorizontalScale()) );
                    result.setStep2( Math.abs(pixSpacing/getVerticalScale()) );
                }
            });
            return result;
        }
    }

}
