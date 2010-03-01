/*
 * PrimitiveMapper.java
 * Created on Nov 4, 2009
 */

package org.bm.blaise.specto.visometry;

import java.util.List;

/**
 * <p>
 *  Functionality to map a primitive to each coordinate in a space. E.g. a vector
 *  field assigns arrows to each point in space. Implementing classes should describe
 *  precisely how this vector is constructed as a graphics primitive.
 * </p>
 * @param C the coordinate type for sampling
 * @param P the plottable primitive type
 * @param CV the coordinate type of the visometry
 * @author Elisha Peterson
 */
public interface PrimitiveMapper<C,P,CV> {

    /**
     * Construct and return the graphics primitive object at the
     * specified coordinate.
     *
     * @param coord the coordinate under consideration
     * @param vis the visometry
     * @param vg the visometry graphics object used for painting
     * @return a graphics primitive object of type <code>P</code>
     */
    public P primitiveAt(C coord, Visometry<CV> vis, VisometryGraphics<CV> vg);

    /**
     * Construct and return a list of graphics primitives at the specified
     * coordinates. Order should be maintained.
     *
     * @param coords the coordinates, as a list
     * @param vis the visometry
     * @param vg the visometry graphics object used for painting
     *
     * @return an array of graphics primitives at the specified coordinates
     */
    public P[] primitivesAt(List<C> coords, Visometry<CV> vis, VisometryGraphics<CV> vg);
}
