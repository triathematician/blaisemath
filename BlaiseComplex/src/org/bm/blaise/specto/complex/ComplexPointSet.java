/*
 * ComplexPointSet.java
 * Created on Nov 16, 2009
 */

package org.bm.blaise.specto.complex;

import java.awt.geom.Point2D;
import org.bm.blaise.specto.plottable.VPointSet;

/**
 * <p>
 *  This class represents a collection of points in the complex plane.
 * </p>
 *
 * @author Elisha Peterson
 */
public class ComplexPointSet extends VPointSet<Point2D.Double> {

    public ComplexPointSet(Point2D.Double[] values) {
        super(values);
    }

    @Override
    public String getValueString(int i) {
        if (values[i] instanceof Point2D) {
            Point2D p2d = (Point2D) values[i];
            return formatter.format(p2d.getX()) + " + " + formatter.format(p2d.getY()) + "i";
        }
        return values[i].toString();
    }

}
