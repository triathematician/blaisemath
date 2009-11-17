/*
 * ComplexProduct.java
 * Created on Nov 16, 2009
 */

package org.bm.blaise.specto.complex;

import java.awt.geom.Point2D;
import org.bm.blaise.scio.coordinate.utils.ComplexMathUtils;

/**
 * <p>
 *   Visualization of a complex product.
 * </p>
 *
 * @author Elisha Peterson
 */
public class ComplexProduct extends ComplexPointSet {

    static Point2D.Double[] SAMPLES = new Point2D.Double[] { new Point2D.Double(1,0), new Point2D.Double(0,1), new Point2D.Double(1,0) };

    public ComplexProduct() {
        super(SAMPLES);
    }

    public ComplexProduct(Point2D.Double[] values) {
        super(values);
    }

    /**
     *
     * @param index the index of one of the points; only 0 and 1 are permitted
     * @param value
     */
    @Override
    public void setValue(int index, Point2D.Double value) {
        if (index == 0 || index == 1) {
            values[index] = value;
            if (index != 2) {
                values[2] = ComplexMathUtils.product(values[0], values[1]);
            }
            fireStateChanged();
        }
    }
}
