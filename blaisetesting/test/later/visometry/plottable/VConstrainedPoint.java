/*
 * VConstrainedPoint.java
 * Created Apr 30, 2010
 */

package later.visometry.plottable;

import other.PointBounder;
import visometry.plottable.VPoint;

/**
 * A point whose movement is limited by a particular bounding class.
 * @param <C> the coordinate type of the point
 *
 * @author Elisha Peterson
 */
public class VConstrainedPoint<C> extends VPoint<C> {

    /** Used to bound values of the point */
    PointBounder<C> bounder;

    /** Constructs constrained point with specified starting value and bounding class. */
    public VConstrainedPoint(C value, PointBounder<C> bounder) {
        super(value);
        this.bounder = bounder;
        setPoint(value);
    }

    /** 
     * Sets the value of the underlying point, by first checking with the bounder to see
     * if the possible value is limited or should be changed.
     * @param value the new coordinate of the point
     */
    @Override
    public void setPoint(C value) {
        super.setPoint( bounder == null ? value : bounder.getConstrainedValue(value) );
    }

}
