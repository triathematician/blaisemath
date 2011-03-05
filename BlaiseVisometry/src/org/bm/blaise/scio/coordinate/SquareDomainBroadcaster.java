/*
 * SquareDomainBroadcaster.java
 */

package org.bm.blaise.scio.coordinate;

import java.awt.geom.Point2D;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.bm.blaise.scio.coordinate.Domain;
import org.bm.blaise.scio.coordinate.MinMaxBean;
import org.bm.blaise.scio.random.RandomCoordinateGenerator;
import util.ChangeBroadcaster;
import util.DefaultChangeBroadcaster;

/**
 *
 * @author ae3263
 */
public class SquareDomainBroadcaster
        implements Domain<Point2D.Double>, RandomCoordinateGenerator<Point2D.Double>, MinMaxBean<Point2D.Double>, ChangeBroadcaster, ChangeListener {

    RealIntervalBroadcaster x, y;

    /**
     * Constructs the domain with specified bounds.
     * @param minX minimum in first direction
     * @param minY minimum in second direction
     * @param maxX maximum in first direction
     * @param maxY maximum in second direction
     */
    public SquareDomainBroadcaster(double minX, double minY, double maxX, double maxY) {
        this( new RealIntervalBroadcaster(minX, maxX), new RealIntervalBroadcaster(minY, maxY) );
    }

    /**
     * Constructs square domain based on two existing interval domains
     * @param xDomain first domain
     * @param yDomain second domain
     */
    public SquareDomainBroadcaster(RealIntervalBroadcaster xDomain, RealIntervalBroadcaster yDomain) {
        x = xDomain;
        y = yDomain;
        x.addChangeListener(this);
        y.addChangeListener(this);
    }

    public boolean contains(Point2D.Double coord) {
        return x.contains(coord.x) && y.contains(coord.y);
    }

    public Point2D.Double randomValue() {
        return new Point2D.Double(x.randomValue(), y.randomValue());
    }

    public Point2D.Double getMinimum() {
        return new Point2D.Double(x.getMinimum(), y.getMinimum());
    }

    public Point2D.Double getMaximum() {
        return new Point2D.Double(x.getMaximum(), y.getMaximum());
    }

    DefaultChangeBroadcaster changer = new DefaultChangeBroadcaster();
    public void addChangeListener(ChangeListener l) { changer.addChangeListener(l); }
    public void removeChangeListener(ChangeListener l) { changer.removeChangeListener(l); }

    public void stateChanged(ChangeEvent e) { changer.fireStateChanged(); }
}
