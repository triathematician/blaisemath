/*
 * LineVisometry.java
 * Created on Sep 19, 2009
 */

package later.visometry.line;

import coordinate.RealIntervalSamplerProvider;
import java.awt.geom.Point2D;
import java.awt.geom.RectangularShape;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import util.ChangeBroadcaster;
import util.DefaultChangeBroadcaster;
import visometry.Visometry;

/**
 * Handles a set of real numbers displayed along an axis... the visual location
 * is determined by two points, allowing for axes w/ arbitrary rotations and sizes.
 *
 * @author Elisha Peterson
 */
class LineVisometry implements Visometry<Double>,
        ChangeBroadcaster, ChangeListener {

    /** Stores the boundary of the display window, in window coordinates. */
    RectangularShape windowBounds;
    /** Minimum point of line */
    double min = -5.0;
    /** Maximum point of line */
    double max = 5.0;
    /** Position of minimum value on axis, in terms of window coordinates */
    transient Point2D.Double minPoint = new Point2D.Double();
    /** Position of maximum value on axis, in terms of window coordinates */
    transient Point2D.Double maxPoint = new Point2D.Double();


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
            minPoint = new Point2D.Double(newBounds.getMinX(), .5*(newBounds.getMinY()+newBounds.getMaxY()));
            maxPoint = new Point2D.Double(newBounds.getMaxX(), .5*(newBounds.getMinY()+newBounds.getMaxY()));
            try {
                computeTransformation();
            } catch (IllegalStateException e) {
                System.out.println("Unable to calculate LineVisometry!!");
            }
        }
    }

    public Double getMinPointVisible() {
        return min;
    }

    public Double getMaxPointVisible() {
        return max;
    }
    
    /** Sets the minimum point of the axis (in window coordinates) */
    void setMinPoint(Point2D point) {
        minPoint.setLocation(point);
        computeTransformation();
    }

    /** Sets the maximum point of the axis (in window coordinates) */
    void setMaxPoint(Point2D point) {
        maxPoint.setLocation(point);
        computeTransformation();
    }

    //
    // COMPUTATIONS
    //

    /** Sets new range of values displayed by the line. */
    public void setDesiredRange(double minimum, double maximum) {
        if (minimum == maximum)
            throw new IllegalArgumentException("Range must be nonempty");

        min = Math.min(minimum, maximum);
        max = Math.max(minimum, maximum);

        changer.fireStateChanged();
    }

    /**
     * Returns the number of pixels/unit
     * @return x scaling to transform local to window coordinates
     */
    public double getScale() {
        return minPoint.distance(maxPoint) / Math.abs(max - min);
    }

    /** Stores unit vector from min to max */
    transient Point2D.Double dir;
    /** Stores the "perpendicular" direction to the line */
    transient Point2D.Double perpDir;

    public void computeTransformation() throws IllegalStateException {
        dir = new Point2D.Double(maxPoint.x - minPoint.x, maxPoint.y - minPoint.y);
        double magn = dir.distance(0, 0);
        perpDir = new Point2D.Double(-dir.y / magn, dir.x / magn);
        changer.fireStateChanged();
    }

    public Point2D.Double toWindow(Double coordinate) {
        return new Point2D.Double(
            minPoint.x + dir.x * (coordinate - min) / (max - min),
            minPoint.y + dir.y * (coordinate - min) / (max - min)
        );
    }

    public Double toLocal(Point2D point) {
        // returns closest coordinate from the given point
        double t = ((minPoint.x - point.getX()) * (minPoint.x - maxPoint.x) + (minPoint.y - point.getY()) * (minPoint.y - maxPoint.y)) / minPoint.distanceSq(maxPoint);
        return min + t * (max-min);
    }


    //
    // DOMAIN METHODS
    //

    RealIntervalSamplerProvider domain;

    /** @return domain associated with the horizontal axis */
    public RealIntervalSamplerProvider getDomain() {
        if (domain == null)
            domain = new RealIntervalSamplerProvider(this) {
                public double getNewMinimum() { return LineVisometry.this.min; }
                public double getNewMaximum() { return LineVisometry.this.max; }
                public double getScale(float pixSpacing) { return Math.abs(pixSpacing / LineVisometry.this.getScale()); }
            };
        return domain;
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

}
