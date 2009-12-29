/**
 * CoordinateHandler.java
 * Created on Nov 23, 2009
 */

package org.bm.blaise.specto.visometry;

/**
 * <p>
 *    This interface is for classes that can "handle" points. Essentially, a plot component
 *    may call the method defined in this interface whenever the user double-clicks on the plot.
 *    For example, a <code>VPointSet</code> class may use this to add another point to the plot.
 * </p>
 * @param C the type of coordinate
 * @author Elisha Peterson
 */
public interface CoordinateHandler<C> {

    /** 
     * Code to handle a point "created" on the plot component.
     *
     * @param coord the coordinate to handle
     * @return <code>true</code> if the coordinate is handled successfully.
     */
    public boolean handleCoordinate(C coord);

}
