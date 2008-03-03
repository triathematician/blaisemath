/*
 * Constrains2D.java
 * Created on Mar 1, 2008
 */

package specto;

import sequor.model.PointRangeModel;
import specto.dynamicplottable.Point2D;

/**
 * Constrains2D is an interface for plottable elements which are able to return PointRangeModel's for points restricted to their space.
 * 
 * @author Elisha Peterson
 */
public interface Constrains2D {
    /** Returns a PointRangeModel which can be used to constrain a point to the current class. */
    public PointRangeModel getConstraintModel();
    /** Returns a point constrained by the current plottable class. */
    public Point2D getConstrainedPoint();
}
