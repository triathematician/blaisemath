/**
 * SpacialMathUtils.java
 * Created on Jul 29, 2009
 */

package scio.coordinate;

/**
 * <p>
 *   <code>SpacialMathUtils</code> ...
 * </p>
 *
 * @author Elisha Peterson
 */
public class SpacialMathUtils {

    /** Non-instantiable class. */
    private SpacialMathUtils() {
    }

    /** Computes and returns triple product of 3 3-vectors */
    public static double tripleProduct(P3D p1, P3D p2, P3D p3) {
        return p1.crossProduct(p2).dotProduct(p3);
    }

}
