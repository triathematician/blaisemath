/**
 * SpacialMathUtils.java
 * Created on Jul 29, 2009
 */

package org.bm.blaise.scio.coordinate.utils;

import org.bm.blaise.scio.coordinate.*;

/**
 * <p>
 *   <code>SpacialMathUtils</code> is a library of static methods for use on elements of space (vectors).
 *   All methods work with <code>P3D</code>s.
 * </p>
 *
 * @see P3D
 *
 * @author Elisha Peterson
 */
public class SpacialMathUtils {

    /** Non-instantiable class. */
    private SpacialMathUtils() {
    }

    /** Computes and returns triple product of 3 3-vectors */
    public static double tripleProduct(Point3D p1, Point3D p2, Point3D p3) {
        return p1.crossProduct(p2).dotProduct(p3);
    }

}
