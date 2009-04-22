
package specto.euclidean3;

import java.util.Vector;
import scio.coordinate.R3;

/**
 * <p> This interface describes a class that can produce a set of 3d points describing a 
 * set of "sampled" points... e.g. points along a surface or points along a line. </p>
 * @author ae3263
 */
public interface SampleVector3D {
    /** Returns a list of vectors sampled from the class. The vectors are in terms of an initial position
     * and a displacement vector.
     * @param options a set of options used to vary the resulting vectors
     * @return the list of vectors.
     */
    public Vector<R3[]> getSampleVectors(int options);
}
