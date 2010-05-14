/**
 * VPoint.java
 * Created on Jul 30, 2009
 */

package visometry.plottable;

import java.awt.Color;
import java.util.Arrays;
import primitive.style.PathStyle;

/**
 * <p>
 *   <code>VPoint</code> is a static set of points in the visometry, accessible as an array.
 *   This is the "non-dynamic" version of <code>VPointSet</code>, allowing a potentially
 *   a huge number of points stored for display.
 * </p>
 *
 * @author Elisha Peterson
 * @seealso VPointSet
 */
public class VPath<C> extends VAbstractPointArray<C> {

    /** 
     * Constructs the plottable with an array of points
     * @param path the path
     */
    public VPath(C... path) {
        super(new PathStyle( new Color(64, 0, 0) ), path);
    }

    @Override
    public String toString() {
        return "VPath " + Arrays.toString((C[]) entry.local);
    }

    /** @return current style of stroke for this plottable */
    public PathStyle getStyle() { return (PathStyle) entry.style; }
    /** Set current style of stroke for this plottable */
    public void setStyle(PathStyle newValue) { if (entry.style != newValue) { entry.style = newValue; firePlottableStyleChanged(); } }
    
}
