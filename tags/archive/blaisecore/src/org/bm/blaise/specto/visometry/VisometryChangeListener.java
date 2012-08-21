/**
 * VisometryChangeListener.java
 * Created on Sep 7, 2009
 */

package org.bm.blaise.specto.visometry;

/**
 * <p>
 *   Since generating the plottable's display elements may be time consuming, the
 *   <code>visometryChanged</code> method is included to allow for "smart" computation, e.g.
 *   only when the visometry window changes location, or when the element is edited.
 *   The plottable is drawn whenever it is asked to by another class, using the
 *   supplied <code>VisometryGraphics</code>.
 * </p>
 *
 * @param <C> the class type of the local coordinates
 *
 * @see Plottable, Visometry
 *
 * @author Elisha Peterson
 */
public interface VisometryChangeListener {

    /**
     * Recompute the graphics primitives based upon the underlying coordinate
     * transformations and display window.
     * @param vis the underlying visometry
     * @param canvas the graphics object upon which painting will occur
     */
    public void visometryChanged(Visometry vis, VisometryGraphics canvas);

}
