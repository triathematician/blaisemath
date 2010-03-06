
package specto.euclidean3;

import java.util.Vector;
import scio.coordinate.R3;

/**
 * <p> This interface describes a class that can produce a set of 3d points describing a 
 * set of "sampled" points... e.g. points along a surface or points along a line. </p>
 * @author ae3263
 */
public interface SampleSet3D {
    /** 
     * Returns a list of points sampled from the class.
     * @param options a set of options used to vary the resulting points
     * @return the list of points
     */
    public Vector<R3> getSampleSet(int options);
}
