/**
 * VComputedPath.java
 * Created on Sep 17, 2009
 */

package org.bm.blaise.specto.plottable;

import java.awt.geom.GeneralPath;
import org.bm.blaise.specto.primitive.BlaisePalette;
import org.bm.blaise.specto.primitive.PathStyle;
import org.bm.blaise.specto.visometry.AbstractDynamicPlottable;
import org.bm.blaise.specto.visometry.VisometryChangeListener;
import org.bm.blaise.specto.visometry.VisometryGraphics;

/**
 * <p>
 *   <code>VComputedPath</code> displays a path that may be recomputed when the visometry
 *   or other elements change
 * </p>
 *
 * @param <C> coordinate type of visometry
 * @author Elisha Peterson
 */
public abstract class VComputedPath<C> extends AbstractDynamicPlottable<C> implements VisometryChangeListener {

    //
    //
    // PROPERTIES
    //
    //
    
    /** Style of stroke */
    protected PathStyle strokeStyle = new PathStyle(BlaisePalette.STANDARD.function());


    //
    //
    // BEAN PATTERNS
    //
    //

    public PathStyle getStrokeStyle() {
        return strokeStyle;
    }

    public void setStrokeStyle(PathStyle style) {
        this.strokeStyle = style;
    }

    //
    //
    // DRAW METHODS
    //
    //
    
    /** Determines whether path needs to be recomputed */
    transient protected boolean needsComputation = true;
    /** Stores the path computed and displayed (in WINDOW coordinates) */
    transient protected GeneralPath path;

    /** This method is called to recompute the path. */
    abstract protected void recompute(VisometryGraphics<C> vg);

    @Override
    public void paintComponent(VisometryGraphics<C> vg) {
        if (needsComputation) {
            recompute(vg);
        }
        vg.setPathStyle(strokeStyle);
        vg.drawPath(path);
    }
}
