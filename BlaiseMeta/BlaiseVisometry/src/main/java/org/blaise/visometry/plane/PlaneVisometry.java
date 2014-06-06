/**
 * PlaneVisometry.java
 * Created on Jul 30, 2009
 */
package org.blaise.visometry.plane;

/*
 * #%L
 * BlaiseVisometry
 * --
 * Copyright (C) 2009 - 2014 Elisha Peterson
 * --
 * Licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import org.blaise.math.coordinate.DomainHint;
import org.blaise.math.coordinate.SampleSet;
import org.blaise.math.coordinate.ScreenSampleDomainProvider;
import org.blaise.math.line.RealIntervalSamplerProvider;
import org.blaise.math.line.RealIntervalStepSampler;
import org.blaise.math.plane.SquareDomainBroadcaster;
import org.blaise.math.plane.SquareDomainStepSampler;
import org.blaise.visometry.Visometry;

/**
 * <p>
 *   Converts a standard Euclidean plane onto a window.
 *   The class ensures that all of a desired range is kept within the window, and that
 *   the aspect ratio is also maintained. The displayed window is the smallest possible
 *   around the desired range with the specified aspect.
 * </p>
 *
 * @author Elisha Peterson
 */
public final class PlaneVisometry implements Visometry<Point2D.Double> {

    /** Stores the boundary of the display window, in window coordinates. */
    private RectangularShape windowBounds;
    /** Stores the desired range of values. The class should ensure that this entire range is displayed. */
    private Point2D.Double desiredMin = new Point2D.Double(-5, -5);
    /** Stores the desired range of values. The class should ensure that this entire range is displayed. */
    private Point2D.Double desiredMax = new Point2D.Double(5, 5);
    /** Stores the window's AspectRatio. */
    private double aspectRatio = 1.0;
    
    /** Stores the affine transformation that converts the actual range to the window bounds. */
    private transient AffineTransform at = new AffineTransform();
    /** Stores the actual range of values that is displayed, in local coordiantes. */
    private transient Rectangle2D.Double displayRange = new Rectangle2D.Double();

    private RealIntervalSamplerProvider xDomain;
    private RealIntervalSamplerProvider yDomain;
    private PlaneDomain planeDomain;

    private final ChangeEvent changeEvent = new ChangeEvent(this);
    private final EventListenerList listenerList = new EventListenerList();

    
    //<editor-fold defaultstate="collapsed" desc="PROPERTY PATTERNS">
    //
    // PROPERTY PATTERNS
    //

    /** 
     * Get bounding rectangle provided by the window. 
     */
    public RectangularShape getWindowBounds() {
        return windowBounds;
    }

    /**
     * Sets window bounds.
     * Recomputes transformation after setting.
     */
    public void setWindowBounds(RectangularShape newBounds) {
        checkNotNull(newBounds);
        if (!newBounds.equals(windowBounds)) {
            this.windowBounds = newBounds;
            computeTransformation();
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
     */
    public void setAspectRatio(double aspectRatio) {
        checkArgument(aspectRatio > 0, "Aspect ratio must be positive!");
        this.aspectRatio = aspectRatio;
        computeTransformation();
    }

    public Point2D.Double getDesiredMin() {
        return desiredMin;
    }

    public Point2D.Double getDesiredMax() {
        return desiredMax;
    }
    
    /**
     * Get desired range as a rectanlge
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
        checkArgument(minX != maxX && minY != maxY, "Range must be nonempty");
        desiredMin = new Point2D.Double(Math.min(minX, maxX), Math.min(minY, maxY));
        desiredMax = new Point2D.Double(Math.max(minX, maxX), Math.max(minY, maxY));
        computeTransformation();
    }

    /** Get visible range (in coordinates) */
    public Rectangle2D.Double getVisibleRange() {
        return displayRange;
    }

    /** Get minimum visible coordinate */
    public Point2D.Double getMinPointVisible() {
        return new Point2D.Double(displayRange.x, displayRange.y);
    }

    /** Get maximum visible coordinate */
    public Point2D.Double getMaxPointVisible() {
        return new Point2D.Double(displayRange.x + displayRange.width, displayRange.y + displayRange.height);
    }

    /**
     * Get the number of pixels/unit in the x direction
     * @return x scaling to transform local to window coordinates
     */
    public double getHorizontalScale() {
        return at.getScaleX();
    }

    /**
     * Get the number of pixels/unit in the y direction
     * @return y scaling to transform local to window coordinates
     */
    public double getVerticalScale() {
        return at.getScaleY();
    }

    /** 
     * Get domain associated with the horizontal axis 
     */
    public RealIntervalSamplerProvider getHorizontalDomain() {
        if (xDomain == null) {
            xDomain = new RealIntervalSamplerProvider() {
                public double getNewMinimum() { 
                    return PlaneVisometry.this.displayRange.x;
                }
                public double getNewMaximum() {
                    return PlaneVisometry.this.displayRange.x + PlaneVisometry.this.displayRange.width;
                }
                public double getScale(float pixSpacing) { 
                    return Math.abs(pixSpacing / PlaneVisometry.this.getHorizontalScale());
                }
            };
            addChangeListener(xDomain);
        }
        return xDomain;
    }

    /** 
     * Get domain associated with the vertical axis 
     */
    public RealIntervalSamplerProvider getVerticalDomain() {
        if (yDomain == null) {
            yDomain = new RealIntervalSamplerProvider() {
                public double getNewMinimum() {
                    return PlaneVisometry.this.displayRange.y;
                }
                public double getNewMaximum() {
                    return PlaneVisometry.this.displayRange.y + PlaneVisometry.this.displayRange.height;
                }
                public double getScale(float pixSpacing) { 
                    return Math.abs(pixSpacing / PlaneVisometry.this.getVerticalScale()); 
                }
            };
            addChangeListener(yDomain);
        }
        return yDomain;
    }

    /** 
     * Get domain associated with both axes. 
     */
    public SquareDomainBroadcaster getPlaneDomain() {
        if (planeDomain == null) {
            planeDomain = new PlaneDomain();
        }
        return planeDomain;
    }
    
    //</editor-fold>
    

    public void computeTransformation() {
        checkState(windowBounds != null && windowBounds.getWidth() > 0 && windowBounds.getHeight() > 0
                && desiredMin != null && desiredMax != null && aspectRatio > 0 && aspectRatio != Double.NaN);

        // compute displayed range of values
        double windowAspect = windowBounds.getWidth() / windowBounds.getHeight();
        double actualDisplayedAspect = windowAspect / aspectRatio;
        double desiredAspect = (desiredMax.x - desiredMin.x) / (desiredMax.y - desiredMin.y);

        if (actualDisplayedAspect >= desiredAspect) {       
            // will show more horizontally
            displayRange.y = desiredMin.y;
            displayRange.height = desiredMax.y - desiredMin.y;
            displayRange.width = actualDisplayedAspect * displayRange.height;
            displayRange.x = 0.5 * (desiredMin.x + desiredMax.x) - 0.5 * displayRange.width;
        } else {                                   
            // will show more vertically
            displayRange.x = desiredMin.x;
            displayRange.width = desiredMax.x - desiredMin.x;
            displayRange.height = displayRange.width / actualDisplayedAspect;
            displayRange.y = 0.5 * (desiredMin.y + desiredMax.y) - 0.5 * displayRange.height;
        }

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

    public Point2D.Double toWindow(Point2D.Double coordinate) {
        checkState(at != null);
        if (Double.isInfinite(coordinate.x)) {
            return getWindowPointOfInfiniteAngle(coordinate.y);
        }
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
        checkState(at != null);
        try {
            Point2D result = at.inverseTransform(point, null);
            if (result instanceof Point2D.Double) {
                return (Point2D.Double) result;
            } else {
                return new Point2D.Double(result.getX(), result.getY());
            }
        } catch (NoninvertibleTransformException ex) {
            throw new IllegalStateException("Transform was not invertible", ex);
        }
    }


    //<editor-fold defaultstate="collapsed" desc="EVENT HANDLING">
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
            fireStateChanged();
        }
    }

    public synchronized void addChangeListener(ChangeListener l) { 
        listenerList.add(ChangeListener.class, l);
    }
    
    public synchronized void removeChangeListener(ChangeListener l) { 
        listenerList.remove(ChangeListener.class, l); 
    }

    /** Notify interested listeners of an (unspecified) change in the plottable. */
    public synchronized void fireStateChanged() {
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
    
    //</editor-fold>


    //<editor-fold defaultstate="collapsed" desc="INNER CLASSES">
    //
    // INNER CLASSES
    //

    /** Domain that combines x and y domains; uses embedded x and y domains. */
    public final class PlaneDomain extends SquareDomainBroadcaster implements ScreenSampleDomainProvider<Point2D.Double> {
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
    
    //</editor-fold>

}
