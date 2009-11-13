/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bm.blaise.specto.plane.complex;

import java.awt.geom.Point2D;
import org.bm.blaise.specto.plottable.VPointSet;
import scio.coordinate.utils.ComplexMathUtils;

/**
 *
 * @author ae3263
 */
public class ComplexProduct extends VPointSet<Point2D.Double> {

    static Point2D.Double[] SAMPLES = new Point2D.Double[] { new Point2D.Double(1,0), new Point2D.Double(0,1), new Point2D.Double(1,0) };

    public ComplexProduct() {
        super(SAMPLES);
    }

    public ComplexProduct(Point2D.Double[] values) {
        super(values);
    }

    @Override
    public void setValue(int index, Point2D.Double value) {
        values[index] = value;
        if (index != 2) {
            values[2] = ComplexMathUtils.product(values[0], values[1]);
        }
        fireStateChanged();
    }

    /** Hook for subclasses to provide custom formatting of displayed coordinates of point. */
    @Override
    public String getValueString(int i) {
        if (values[i] instanceof Point2D) {
            Point2D p2d = ((Point2D)values[i]);
            return formatter.format(p2d.getX()) + " + " + formatter.format(p2d.getY()) + "i";

        }
        return values[i].toString();
    }
}
