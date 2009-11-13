/**
 * VComputedPointPath.java
 * Created on Sep 17, 2009
 */

package org.bm.blaise.specto.plottable;

import java.awt.Color;
import java.awt.geom.GeneralPath;
import javax.swing.event.ChangeEvent;
import org.bm.blaise.specto.primitive.BlaisePalette;
import org.bm.blaise.specto.primitive.PathStyle;
import org.bm.blaise.specto.primitive.PointStyle;
import org.bm.blaise.specto.visometry.VisometryChangeListener;
import org.bm.blaise.specto.visometry.VisometryGraphics;

/**
 * <p>
 *   <code>VComputedPointPath</code> displays a path that is recomputed whenever a point
 *   changes. The point is also shown. The class is essentially incomplete with the
 *   exception of the <code>recompute</code> method, provided as an abstract method
 *   here so that subclasses can override it.
 * </p>
 *
 * @param <C> coordinate type of visometry
 * @author Elisha Peterson
 */
public abstract class VComputedPointPath<C> extends VPoint<C> implements VisometryChangeListener {

    //
    //
    // PROPERTIES
    //
    //
    
    /** Style of stroke */
    protected PathStyle strokeStyle = new PathStyle(BlaisePalette.STANDARD.function());

    //
    //
    // CONSTRUCTOR
    //
    //

    /** Constructs with given point. */
    public VComputedPointPath(C point) {
        super(point);
        style = PointStyle.SMALL;
        style.setStrokeColor(BlaisePalette.STANDARD.function());
        addChangeListener(this); // listen for changes to the underlying point... causes path to be recomputed.
    }

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
    /** Stores the path computed and displayed. */
    transient protected GeneralPath path;

    @Override
    public void stateChanged(ChangeEvent e) {
        super.stateChanged(e);
        if (e.getSource() == this) {
            needsComputation = true;
        }
    }

    /** This method is called to recompute the path. */
    abstract protected void recompute(VisometryGraphics<C> vg);

    @Override
    public void paintComponent(VisometryGraphics<C> vg) {
        if (needsComputation) {
            recompute(vg);
        }
        super.paintComponent(vg);
        vg.setPathStyle(strokeStyle);
        vg.drawPath(path);
    }
}
